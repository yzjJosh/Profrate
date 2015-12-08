from google.appengine.ext import ndb
from google.appengine.api.images import get_serving_url
from google.appengine.ext import blobstore
from google.appengine.ext.webapp import blobstore_handlers
import datetime
import urllib
import webapp2


class Comment(ndb.Model):
    target_id = ndb.IntegerProperty(required=True)
    target_type = ndb.StringProperty(required=True)
    author_email = ndb.StringProperty(required=True)
    content = ndb.StringProperty(required=True, indexed=False)
    liked_by = ndb.StringProperty(repeated=True, indexed=False)
    disliked_by = ndb.StringProperty(repeated=True, indexed=False)
    like_num = ndb.IntegerProperty(default=0)
    dislike_num = ndb.IntegerProperty(default=0)
    reply_num = ndb.IntegerProperty(default=0)
    time = ndb.DateTimeProperty(auto_now_add=True)

    @staticmethod
    def parent_key():
        return ndb.Key("Comment_Ancestor", "Ancestor")

    @staticmethod
    def get_Comment(comment_id):
        return Comment.get_by_id(comment_id, Comment.parent_key())

    def get_id(self):
        return self.key.id()

    def get_replies(self):
        return CommentReply.query(CommentReply.comment_id==self.get_id()).order(-CommentReply.time).fetch()

    def edit(self, content):
        self.content = content
        self.time = datetime.datetime.now()
        self.put()

    def toggle_like(self, user_email):
        if user_email in self.liked_by:
            self.liked_by.remove(user_email)
            self.like_num = len(self.liked_by)
        else:
            self.liked_by.append(user_email)
            self.like_num = len(self.liked_by)
            if user_email in self.disliked_by:
                self.disliked_by.remove(user_email)
                self.dislike_num = len(self.disliked_by)
        self.put()

    def toggle_dislike(self, user_email):
        if user_email in self.disliked_by:
            self.disliked_by.remove(user_email)
            self.dislike_num = len(self.disliked_by)
        else:
            self.disliked_by.append(user_email)
            self.dislike_num = len(self.disliked_by)
            if user_email in self.liked_by:
                self.liked_by.remove(user_email)
                self.like_num = len(self.liked_by)
        self.put()

    def delete(self):
        for reply in self.get_replies():
            reply.delete()
        self.key.delete()
        target = None
        if self.target_type == "Professor":
            target = Professor.get_professor(self.target_id)
        elif self.target_type == "Article":
            target = Article.get_article(self.target_id)
        target.comment_num = target.comment_num - 1
        target.put()

    def reply(self, user_email, content):
        reply = CommentReply(parent=CommentReply.parent_key(), comment_id=self.get_id(), author_email=user_email,
                             content=content)
        reply.put()
        self.reply_num = self.reply_num + 1
        self.put()


class CommentReply(ndb.Model):
    comment_id = ndb.IntegerProperty(required=True)
    content = ndb.StringProperty(required=True, indexed=False)
    time = ndb.DateTimeProperty(auto_now_add=True)
    author_email = ndb.StringProperty(required=True, indexed=False)

    @staticmethod
    def parent_key():
        return ndb.Key("CommentReply_Ancestor", "Ancestor")

    @staticmethod
    def get_CommentReply(reply_id):
        return CommentReply.get_by_id(reply_id, CommentReply.parent_key())

    def get_id(self):
        return self.key.id()

    def edit(self, content):
        self.content = content
        self.time = datetime.datetime.now()
        self.put()

    def delete(self):
        self.key.delete()
        comment = Comment.get_Comment(self.comment_id)
        comment.reply_num = comment.reply_num - 1
        comment.put()


class Article(ndb.Model):
    target_id = ndb.IntegerProperty(required=True)
    author_email = ndb.StringProperty(required=True)
    title = ndb.StringProperty(required=True)
    content = ndb.StringProperty(required=True, indexed=False)
    liked_by = ndb.StringProperty(repeated=True, indexed=False)
    disliked_by = ndb.StringProperty(repeated=True, indexed=False)
    like_num = ndb.IntegerProperty(default=0)
    dislike_num = ndb.IntegerProperty(default=0)
    comment_num = ndb.IntegerProperty(default=0)
    time = ndb.DateTimeProperty(auto_now_add=True)

    @staticmethod
    def parent_key():
        return ndb.Key("Article_Ancestor", "Ancestor")

    @staticmethod
    def get_article(article_id):
        return Article.get_by_id(article_id, Article.parent_key())

    def get_id(self):
        return self.key.id()

    def get_comments(self):
        return Comment.query(Comment.target_id==self.get_id(), ancestor=Comment.parent_key()).order(-Comment.time).fetch()

    def edit(self, title, content):
        self.title = title
        self.content = content
        self.time = datetime.datetime.now()
        self.put()

    def toggle_like(self, user_email):
        if user_email in self.liked_by:
            self.liked_by.remove(user_email)
            self.like_num = len(self.liked_by)
        else:
            self.liked_by.append(user_email)
            self.like_num = len(self.liked_by)
            if user_email in self.disliked_by:
                self.disliked_by.remove(user_email)
                self.dislike_num = len(self.disliked_by)
        self.put()

    def toggle_dislike(self, user_email):
        if user_email in self.disliked_by:
            self.disliked_by.remove(user_email)
            self.dislike_num = len(self.disliked_by)
        else:
            self.disliked_by.append(user_email)
            self.dislike_num = len(self.disliked_by)
            if user_email in self.liked_by:
                self.liked_by.remove(user_email)
                self.like_num = len(self.liked_by)
        self.put()

    def delete(self):
        for comment in self.get_comments():
            comment.delete()
        self.key.delete()
        professor = Professor.get_professor(self.target_id)
        professor.article_num = professor.article_num - 1
        professor.put()

    def comment(self, user_email, content):
        comment = Comment(parent=Comment.parent_key(), target_id=self.get_id(), target_type='Article', author_email=user_email, content=content)
        comment.put()
        self.comment_num = self.comment_num + 1
        self.put()


class ProfRating(ndb.Model):
    author_email = ndb.StringProperty(required=True)
    personality = ndb.FloatProperty(default=0.0)
    teaching_skill = ndb.FloatProperty(default=0.0)
    research_skill = ndb.FloatProperty(default=0.0)
    knowledge_level = ndb.FloatProperty(default=0.0)

    def overall_rating(self):
        return (self.personality + self.teaching_skill
               + self.research_skill + self.knowledge_level)/4


class Professor(ndb.Model):
    name = ndb.StringProperty(required=True)
    title = ndb.StringProperty(required=True)
    special_title = ndb.StringProperty(indexed=False)
    image = ndb.StringProperty(indexed=False)
    introduction = ndb.StringProperty(indexed=False)
    research_areas = ndb.StringProperty(repeated=True)
    research_interests = ndb.StringProperty(repeated=True)
    research_groups = ndb.StringProperty(repeated=True)
    office = ndb.StringProperty(indexed=False)
    phone = ndb.StringProperty(indexed=False)
    email = ndb.StringProperty(indexed=False)
    personal_website = ndb.StringProperty(indexed=False)
    overallRating = ndb.StructuredProperty(ProfRating, default=ProfRating(author_email=None))
    rating_num = ndb.IntegerProperty(indexed=False, default=0)
    overallSingleValueRating = ndb.FloatProperty(default=0.0)
    liked_by = ndb.StringProperty(repeated=True, indexed=False)
    disliked_by = ndb.StringProperty(repeated=True, indexed=False)
    like_num = ndb.IntegerProperty(default=0)
    dislike_num = ndb.IntegerProperty(default=0)
    comment_num = ndb.IntegerProperty(default=0)
    article_num = ndb.IntegerProperty(default=0)

    @staticmethod
    def get_all_professors():
        return Professor.query().order(-Professor.overallSingleValueRating, -Professor.like_num, Professor.dislike_num).fetch()

    @staticmethod
    def get_professor(prof_id):
        return Professor.get_by_id(prof_id)

    def get_id(self):
        return self.key.id()

    def get_comments(self):
        return Comment.query(Comment.target_id==self.get_id(), ancestor=Comment.parent_key()).order(-Comment.time)

    def get_articles(self):
        return Article.query(Article.target_id==self.get_id(), ancestor=Article.parent_key()).order(-Article.time)

    def get_all_ratings(self):
        return ProfRating.query(ancestor=self.key)

    def get_rating(self, user_email):
        result = ProfRating.query(ProfRating.author_email==user_email, ancestor=self.key).fetch()
        return result[0] if len(result)>0 else None

    def rate(self, user_email, personality, teaching_skill, research_skill, knowledge_level):
        if personality > 5.0:
            personality = 5.0
        if teaching_skill > 5.0:
            teaching_skill = 5.0
        if research_skill > 5.0:
            research_skill = 5.0
        if knowledge_level > 5.0:
            knowledge_level = 5.0
        if personality < 0.0:
            personality = 0.0
        if teaching_skill < 0.0:
            teaching_skill = 0.0
        if research_skill < 0.0:
            research_skill = 0.0
        if knowledge_level < 0.0:
            knowledge_level = 0.0
        rating = self.get_rating(user_email)
        if rating:
            self.overallRating.personality = (self.overallRating.personality*self.rating_num - rating.personality + personality)/self.rating_num
            self.overallRating.teaching_skill = (self.overallRating.teaching_skill*self.rating_num - rating.teaching_skill + teaching_skill)/self.rating_num
            self.overallRating.research_skill = (self.overallRating.research_skill*self.rating_num - rating.research_skill + research_skill)/self.rating_num
            self.overallRating.knowledge_level = (self.overallRating.knowledge_level*self.rating_num - rating.knowledge_level + knowledge_level)/self.rating_num
            rating.personality = personality
            rating.teaching_skill = teaching_skill
            rating.research_skill = research_skill
            rating.knowledge_level = knowledge_level

        else:
            rating = ProfRating(parent=self.key, author_email=user_email, personality=personality, teaching_skill=teaching_skill,
                            research_skill=research_skill, knowledge_level=knowledge_level)
            self.overallRating.personality = (self.overallRating.personality*self.rating_num + personality)/(self.rating_num+1)
            self.overallRating.teaching_skill = (self.overallRating.teaching_skill*self.rating_num + teaching_skill)/(self.rating_num+1)
            self.overallRating.research_skill = (self.overallRating.research_skill*self.rating_num + research_skill)/(self.rating_num+1)
            self.overallRating.knowledge_level = (self.overallRating.knowledge_level*self.rating_num + knowledge_level)/(self.rating_num+1)
            self.rating_num = self.rating_num + 1
        rating.put()
        self.overallSingleValueRating = self.overallRating.overall_rating()
        self.put()


    def comment(self, user_email, content):
        comment = Comment(parent=Comment.parent_key(), target_id=self.get_id(), target_type='Professor', author_email=user_email, content=content)
        comment.put()
        self.comment_num = self.comment_num + 1
        self.put()

    def write_article(self, user_email, title, content):
        article = Article(parent=Article.parent_key(), target_id=self.get_id(), author_email=user_email, title=title, content=content)
        article.put()
        self.article_num = self.article_num + 1
        self.put()

    def toggle_like(self, user_email):
        if user_email in self.liked_by:
            self.liked_by.remove(user_email)
            self.like_num = len(self.liked_by)
        else:
            self.liked_by.append(user_email)
            self.like_num = len(self.liked_by)
            if user_email in self.disliked_by:
                self.disliked_by.remove(user_email)
                self.dislike_num = len(self.disliked_by)
        self.put()

    def toggle_dislike(self, user_email):
        if user_email in self.disliked_by:
            self.disliked_by.remove(user_email)
            self.dislike_num = len(self.disliked_by)
        else:
            self.disliked_by.append(user_email)
            self.dislike_num = len(self.disliked_by)
            if user_email in self.liked_by:
                self.liked_by.remove(user_email)
                self.like_num = len(self.liked_by)
        self.put()


class User(ndb.Model):
    name = ndb.StringProperty(required=True)
    photo = ndb.BlobKeyProperty(indexed=False)

    @staticmethod
    def parent_key():
        return ndb.Key("User_Ancestor", "Ancestor")

    @staticmethod
    def create_User(email, name, photo=None):
        user = User(parent=User.parent_key(), id=email, name=name, photo=photo)
        user.put()
        return user

    @staticmethod
    def find_User(email):
        return User.get_by_id(email, User.parent_key())

    def get_photo_url(self):
        if self.photo:
            return get_serving_url(self.photo)
        else:
            return '/assets/images/default_user_photo.png'

    def get_photo_upload_url(self):
        return blobstore.create_upload_url('/storage/upload/user_photo?'+urllib.urlencode({'email': self.get_email()}))

    def get_email(self):
        return self.key.id()

    def edit_name(self, name):
        self.name = name
        self.put()

    def edit_photo(self, photo):
        if self.photo:
            blobstore.delete(self.photo)
        self.photo = photo
        self.put()


class UserPhotoUploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        email = self.request.get('email')
        photo = self.get_uploads('photo')[0]
        if not email:
            blobstore.delete(photo.key())
            self.error(400)
            self.response.write("fail")
            return
        user = User.find_User(email)
        if not user:
            blobstore.delete(photo)
            self.error(400)
            self.response.write('fail');
            return
        if user.photo:
            blobstore.delete(user.photo)
        user.photo = photo.key()
        user.put()
        self.response.write('success')


app = webapp2.WSGIApplication([
    ("/storage/upload/user_photo(?:/(?:\?.*)?)?", UserPhotoUploadHandler)
], debug=True)