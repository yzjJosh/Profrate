import endpoints
import API
import datetime
from protorpc import messages
from protorpc import remote
from Source.service.storage import CommentReply

package = 'Profrate'


class EditRequest(messages.Message):
    id = messages.IntegerField(1, required=True)
    content = messages.StringField(2, required=True)


class CommentReplyMessage(messages.Message):
    content = messages.StringField(1)
    time = messages.IntegerField(2)
    author_email = messages.StringField(3)


def createCommentReplyMessage(comment_reply):
    return CommentReplyMessage(
        content=comment_reply.content,
        time=int((comment_reply.time - datetime.datetime.utcfromtimestamp(0)).total_seconds()*1000.0),
        author_email=comment_reply.author_email
    )


class CommentReplyResponse(messages.Message):
    comment_reply = messages.MessageField(CommentReplyMessage, 1)


class MultiCommentReplyResponse(messages.Message):
    comment_replies = messages.MessageField(CommentReplyMessage, 1, repeated=True)


@API.ProfrateAPI.api_class()
class CommentReplyAPI(remote.Service):
    @endpoints.method(EditRequest, API.BooleanMessage, http_method='POST', name='comment_reply_edit')
    def comment_reply_edit(self, request):
        user = endpoints.get_current_user()
        reply = CommentReply.get_CommentReply(request.id)
        if not (reply and user and reply.author_email == user.email()):
            return API.BooleanMessage(value=False)
        reply.edit(request.content)
        return API.BooleanMessage(value=True)

    @endpoints.method(API.IntegerMessage, API.BooleanMessage, http_method='POST', name='comment_reply_delete')
    def comment_reply_delete(self, request):
        user = endpoints.get_current_user()
        reply = CommentReply.get_CommentReply(request.value)
        if not (reply and user and reply.author_email == user.email()):
            return API.BooleanMessage(value=False)
        reply.delete()
        return API.BooleanMessage(value=True)

    @endpoints.method(API.IntegerMessage, CommentReplyResponse, http_method='GET', name='comment_reply_get')
    def comment_reply_get(self, request):
        reply = CommentReply.get_CommentReply(request.value)
        if not reply:
            return CommentReplyResponse(comment_reply=None)
        return CommentReplyResponse(comment_reply=createCommentReplyMessage(reply))