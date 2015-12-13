import endpoints
import API
import datetime
import comment_api
from protorpc import remote
from protorpc import messages
from Source.service.storage import Article

package = 'Profrate'


class ArticleCommentRequest(messages.Message):
    id = messages.IntegerField(1, required=True)
    content = messages.StringField(2, required=True)


class ArticleEditRequest(messages.Message):
    id = messages.IntegerField(1, required=True)
    title = messages.StringField(2, required=True)
    content = messages.StringField(3, required=True)


class ArticleMessage(messages.Message):
    author_email = messages.StringField(1, required=True)
    title = messages.StringField(2, required=True)
    content = messages.StringField(3, required=True)
    liked_by = messages.StringField(4, repeated=True)
    disliked_by = messages.StringField(5, repeated=True)
    time = messages.IntegerField(6, required=True)
    id = messages.IntegerField(7, required=True)
    comment_num = messages.IntegerField(8, required=True)


def createArticleMessage(article):
    return ArticleMessage(
        author_email=article.author_email,
        title=article.title,
        content=article.content,
        liked_by=article.liked_by,
        disliked_by=article.disliked_by,
        time=int((article.time - datetime.datetime.utcfromtimestamp(0)).total_seconds()*1000.0),
        id=article.get_id(),
        comment_num=article.comment_num
    )


class ArticleResponse(messages.Message):
    article = messages.MessageField(ArticleMessage, 1)


class MultiArticleResponse(messages.Message):
    articles = messages.MessageField(ArticleMessage, 1, repeated=True)


@API.ProfrateAPI.api_class()
class ArticleAPI(remote.Service):
    @endpoints.method(ArticleCommentRequest, API.IntegerMessage, http_method='POST', name='article_comment')
    def article_comment(self, request):
        user = endpoints.get_current_user()
        article = Article.get_article(request.id)
        if not(user and article):
            return API.IntegerMessage(value=-1)
        return API.IntegerMessage(value=article.comment(user.email(), request.content))

    @endpoints.method(API.IntegerMessage, API.BooleanMessage, http_method='POST', name='article_toggle_like')
    def article_toggle_like(self, request):
        user = endpoints.get_current_user()
        article = Article.get_article(request.value)
        if not(user and article):
            return API.BooleanMessage(value=False)
        article.toggle_like(user.email())
        return API.BooleanMessage(value=True)

    @endpoints.method(API.IntegerMessage, API.BooleanMessage, http_method='POST', name='article_toggle_dislike')
    def article_toggle_dislike(self, request):
        user = endpoints.get_current_user()
        article = Article.get_article(request.value)
        if not(user and article):
            return API.BooleanMessage(value=False)
        article.toggle_dislike(user.email())
        return API.BooleanMessage(value=True)

    @endpoints.method(ArticleEditRequest, API.BooleanMessage, http_method='POST', name='article_edit')
    def article_edit(self, request):
        user = endpoints.get_current_user()
        article = Article.get_article(request.id)
        if not(user and article and user.email() == article.author_email):
            return API.BooleanMessage(value=False)
        article.edit(request.title, request.content)
        return API.BooleanMessage(value=True)

    @endpoints.method(API.IntegerMessage, API.BooleanMessage, http_method='POST', name='article_delete')
    def article_delete(self, request):
        user = endpoints.get_current_user()
        article = Article.get_article(request.value)
        if not(user and article and user.email() == article.author_email):
            return API.BooleanMessage(value=False)
        article.delete()
        return API.BooleanMessage(value=True)

    @endpoints.method(API.IntegerMessage, ArticleResponse, http_method='GET', name='article_get')
    def article_get(self, request):
        article = Article.get_article(request.value)
        if not article:
            return ArticleResponse()
        return ArticleResponse(article=createArticleMessage(article))

    @endpoints.method(API.IntegerMessage, comment_api.MultiCommentResponse, http_method='GET', name='article_get_comments')
    def article_get_comments(self, request):
        article = Article.get_article(request.value)
        if not article:
            return comment_api.MultiCommentResponse(comments=[])
        comments = [comment_api.createCommentMessage(comment) for comment in article.get_comments()]
        return comment_api.MultiCommentResponse(comments=comments)