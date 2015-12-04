from google.appengine.ext import ndb
import datetime


class Comment(ndb.Model):
    content = ndb.StringProperty(required=True, indexed=False)
    liked_by = ndb.StringProperty(repeated=True, indexed=False)
    disliked_by = ndb.StringProperty(repeated=True, indexed=False)
    like_num = ndb.IntegerProperty(default=0)
    dislike_num = ndb.IntegerProperty(default=0)
    time = ndb.DateTimeProperty(auto_now_add=True)
    author_email = ndb.IntegerProperty(required=True)

    def get_replies(self):
        return CommentReply.query(ancestor=self.key).order(-CommentReply.time).fetch()

    def edit(self, content):
        self.content = content
        self.time = datetime.datetime.now()
        self.put()

    def like(self, user_email):
        self.liked_by.append(user_email)
        self.like_num = len(self.liked_by)
        if user_email in self.disliked_by:
            self.disliked_by.remove(user_email)
            self.dislike_num = len(self.disliked_by)
        self.put()

    def dislike(self, user_email):
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


class CommentReply(ndb.Model):
    content = ndb.StringProperty(required=True, indexed=False)
    time = ndb.DateTimeProperty(auto_now_add=True)
    author_email = ndb.StringProperty(required=True, indexed=False)

    def edit(self, content):
        self.content = content
        self.time = datetime.datetime.now()
        self.put()

    def delete(self):
        self.key.delete()


class Article(ndb.Model):
    author_email = ndb.StringProperty(required=True)
    title = ndb.StringProperty(required=True)
    content = ndb.StringProperty(required=True, indexed=False)
    liked_by = ndb.StringProperty(repeated=True, indexed=False)
    disliked_by = ndb.StringProperty(repeated=True, indexed=False)
    like_num = ndb.IntegerProperty(default=0)
    dislike_num = ndb.IntegerProperty(default=0)
    time = ndb.DateTimeProperty(auto_now_add=True)

    def get_comments(self):
        return Comment.query(ancestor=self.key).order(-Comment.time).fetch()

    def edit(self, title, content):
        self.title = title
        self.content = content
        self.time = datetime.datetime.now()
        self.put()

    def like(self, user_email):
        self.liked_by.append(user_email)
        self.like_num = len(self.liked_by)
        if user_email in self.disliked_by:
            self.disliked_by.remove(user_email)
            self.dislike_num = len(self.disliked_by)
        self.put()

    def dislike(self, user_email):
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


class ProfRating(ndb.Model):
    author_email = ndb.StringProperty(required=True)
    personality = ndb.FloatProperty(default=-1.0)
    teaching_skill = ndb.FloatProperty(default=-1.0)
    research_skill = ndb.FloatProperty(default=-1.0)
    knowledge_level = ndb.FloatProperty(default=-1.0)

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
    overallSingleValueRating = ndb.FloatProperty(default=-1.0)
    liked_by = ndb.StringProperty(repeated=True, indexed=False)
    disliked_by = ndb.StringProperty(repeated=True, indexed=False)
    like_num = ndb.IntegerProperty(default=0)
    dislike_num = ndb.IntegerProperty(default=0)

    @staticmethod
    def get_all_professors():
        return Professor.query().order(-Professor.overallSingleValueRating, -Professor.like_num, Professor.dislike_num).fetch()

    def get_comments(self):
        return Professor.query(ancestor=self.key).order(-Comment.time)

    def get_articles(self):
        return Article.query(ancestor=self.key).order(-Article.time)

    def get_ratings(self):
        return ProfRating.query(ancestor=self.key)

    def rate(self, user_email, personality, teaching_skill, research_skill, knowledge_level):
        query = ProfRating.query(author_email=user_email, ancestor=self.key).fetch()
        if len(query) > 0:
            rating = query[0]
            rating.personality = personality
            rating.teaching_skill = teaching_skill
            rating.research_skill = research_skill
            rating.knowledge_level = knowledge_level
        else:
            rating = ProfRating(parent=self.key, author_email=user_email, personality=personality, teaching_skill=teaching_skill,
                            research_skill=research_skill, knowledge_level=knowledge_level)
        rating.put()

    def comment(self, user_email, content):
        comment = Comment(parent=self.key, author_email=user_email, content=content)
        comment.put()

    def write_article(self, user_email, title, content):
        article = Article(parent=self.key, author_email=user_email, title=title, content=content)
        article.put()
