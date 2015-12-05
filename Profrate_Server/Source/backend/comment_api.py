import endpoints
import API
import datetime
import comment_reply_api
from protorpc import messages
from protorpc import remote
from Source.service.storage import Comment

package = 'Profrate'


class EditRequest(messages.Message):
    id = messages.IntegerField(1, required=True)
    content = messages.StringField(2, required=True)


class ReplyRequest(messages.Message):
    id = messages.IntegerField(1, required=True)
    content = messages.StringField(2, required=True)


class CommentMessage(messages.Message):
    content = messages.StringField(1, required=True)
    liked_by = messages.StringField(2, repeated=True)
    disliked_by = messages.StringField(3, repeated=True)
    time = messages.IntegerField(4, required=True)
    author_email = messages.StringField(5, required=True)


def createCommentMessage(comment):
    return CommentMessage(
        content=comment.content,
        liked_by=comment.liked_by,
        disliked_by=comment.disliked_by,
        time=int((comment.time - datetime.datetime.utcfromtimestamp(0)).total_seconds()*1000.0),
        author_email=comment.author_email
    )


class CommentResponse(messages.Message):
    comment = messages.MessageField(CommentMessage, 1)


class MultiCommentResponse(messages.Message):
    comments = messages.MessageField(CommentMessage, 1, repeated=True)


@API.ProfrateAPI.api_class()
class CommentAPI(remote.Service):
    @endpoints.method(EditRequest, API.BooleanMessage, http_method='POST', name='comment_edit')
    def comment_edit(self, request):
        user = endpoints.get_current_user()
        comment = Comment.get_Comment(request.id)
        if not (user and comment and user.email() == comment.author_email):
            return API.BooleanMessage(value=False)
        comment.edit(request.content)
        return API.BooleanMessage(value=True)

    @endpoints.method(API.IntegerMessage, API.BooleanMessage, http_method='POST', name='comment_delete')
    def comment_delete(self, request):
        user = endpoints.get_current_user()
        comment = Comment.get_Comment(request.value)
        if not (user and comment and user.email() == comment.author_email):
            return API.BooleanMessage(value=False)
        comment.delete()
        return API.BooleanMessage(value=True)

    @endpoints.method(API.IntegerMessage, CommentResponse, http_method='GET', name='comment_get')
    def comment_get(self, request):
        comment = Comment.get_Comment(request.value)
        if not comment:
            return CommentResponse()
        return CommentResponse(comment=createCommentMessage(comment))

    @endpoints.method(API.IntegerMessage, comment_reply_api.MultiCommentReplyResponse, http_method='GET', name='comment_get_replies')
    def comment_get_replies(self, request):
        comment = Comment.get_Comment(request.value)
        if not comment:
            return comment_reply_api.MultiCommentReplyResponse(comment_replies=[])
        replies = [comment_reply_api.createCommentReplyMessage(reply) for reply in comment.get_replies()]
        return comment_reply_api.MultiCommentReplyResponse(comment_replies=replies)

    @endpoints.method(API.IntegerMessage, API.BooleanMessage, http_method='POST', name='comment_like')
    def comment_like(self, request):
        user = endpoints.get_current_user()
        comment = Comment.get_Comment(request.value)
        if not (user and comment and user.email() == comment.author_email):
            return API.BooleanMessage(value=False)
        comment.like(user.email())
        return API.BooleanMessage(value=True)

    @endpoints.method(API.IntegerMessage, API.BooleanMessage, http_method='POST', name='comment_dislike')
    def comment_dislike(self, request):
        user = endpoints.get_current_user()
        comment = Comment.get_Comment(request.value)
        if not (user and comment and user.email() == comment.author_email):
            return API.BooleanMessage(value=False)
        comment.dislike(user.email())
        return API.BooleanMessage(value=True)

    @endpoints.method(ReplyRequest, API.BooleanMessage, http_method='POST', name='comment_reply')
    def comment_reply(self, request):
        user = endpoints.get_current_user()
        comment = Comment.get_Comment(request.id)
        if not (user and comment):
            return API.BooleanMessage(value=False)
        comment.reply(user.email(), request.content)
        return API.BooleanMessage(value=True)