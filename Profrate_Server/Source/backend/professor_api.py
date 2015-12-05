import endpoints
import API
import comment_api
from protorpc import remote
from protorpc import messages
from protorpc import message_types
from Source.service.storage import Professor

package = 'Profrate'


class CommentRequest(messages.Message):
    id = messages.IntegerField(1, required=True)
    content = messages.StringField(2, required=True)


class RateRequest(messages.Message):
    id = messages.IntegerField(1, required=True)
    personality = messages.FloatField(2, default=-1.0)
    teaching_skill = messages.FloatField(3, default=-1.0)
    research_skill = messages.FloatField(4, default=-1.0)
    knowledge_level = messages.FloatField(5, default=-1.0)


class RatingMessage(messages.Message):
    personality = messages.FloatField(2)
    teaching_skill = messages.FloatField(3)
    research_skill = messages.FloatField(4)
    knowledge_level = messages.FloatField(5)


def createRatingMessage(rating):
    return RatingMessage(
        personality=rating.personality,
        teaching_skill=rating.teaching_skill,
        research_skill=rating.research_skill,
        knowledge_level=rating.knowledge_level
    )


class ProfessorMessage(messages.Message):
    name = messages.StringField(1, required=True)
    title = messages.StringField(2, required=True)
    special_title = messages.StringField(3)
    image = messages.StringField(4)
    introduction = messages.StringField(5)
    research_areas = messages.StringField(6, repeated=True)
    research_interests = messages.StringField(7, repeated=True)
    research_groups = messages.StringField(8, repeated=True)
    office = messages.StringField(9)
    phone = messages.StringField(10)
    email = messages.StringField(11)
    personal_website = messages.StringField(12)
    overall_rating = messages.MessageField(RatingMessage, 13)
    overall_single_value_rating = messages.FloatField(14)
    liked_by = messages.StringField(15, repeated=True)
    disliked_by = messages.StringField(16, repeated=True)


def createProfessorMessage(professor):
    return ProfessorMessage(
        name=professor.name,
        title=professor.title,
        special_title=professor.special_title,
        image=professor.image,
        introduction=professor.introduction,
        research_areas=professor.research_areas,
        research_interests=professor.research_interests,
        research_groups=professor.research_groups,
        office=professor.office,
        phone=professor.phone,
        email=professor.email,
        personal_website=professor.personal_website,
        overall_rating=createRatingMessage(professor.overallRating),
        overall_single_value_rating=professor.overallSingleValueRating,
        liked_by=professor.liked_by,
        disliked_by=professor.disliked_by
    )


class ProfessorResponse(messages.Message):
    professor = messages.MessageField(ProfessorMessage, 1)


class MultiProfessorResponse(messages.Message):
    professors = messages.MessageField(ProfessorMessage, 1, repeated=True)

class RatingResponse(messages.Message):
    rating = messages.MessageField(RatingMessage, 1)


@API.ProfrateAPI.api_class()
class ProfessorAPI(remote.Service):
    @endpoints.method(CommentRequest, API.BooleanMessage, http_method='POST', name='professor_comment')
    def professor_comment(self, request):
        user = endpoints.get_current_user()
        professor = Professor.get_professor(request.id)
        if not(user and professor):
            return API.BooleanMessage(value=False)
        professor.comment(user.email(), request.content)
        return API.BooleanMessage(value=True)

    @endpoints.method(RateRequest, API.BooleanMessage, http_method='POST', name='professor_rate')
    def professor_rate(self, request):
        user = endpoints.get_current_user()
        professor = Professor.get_professor(request.id)
        if not(user and professor):
            return API.BooleanMessage(value=False)
        professor.rate(user.email(), request.personality, request.teaching_skill,
                       request.research_skill, request.knowledge_level)
        return API.BooleanMessage(value=True)

    @endpoints.method(API.IntegerMessage, ProfessorResponse, http_method='GET', name='professor_get')
    def professor_get(self, request):
        professor = Professor.get_professor(request.value)
        if not professor:
            return ProfessorResponse()
        return ProfessorResponse(professor=createProfessorMessage(professor))

    @endpoints.method(message_types.VoidMessage, MultiProfessorResponse, http_method='GET', name='professor_get_all')
    def professor_get_all(self, request):
        professors = [createProfessorMessage(professor) for professor in Professor.get_all_professors()]
        return MultiProfessorResponse(professors=professors)

    @endpoints.method(API.IntegerMessage, comment_api.MultiCommentResponse, http_method='GET', name='professor_get_comments')
    def professor_get_comments(self, request):
        professor = Professor.get_professor(request.value)
        if not professor:
            return comment_api.MultiCommentResponse(comments=[])
        comments = [comment_api.createCommentMessage(comment) for comment in professor.get_comments()]
        return comment_api.MultiCommentResponse(comments=comments)

    @endpoints.method(API.IntegerMessage, RatingResponse, http_method='GET', name='professor_get_rating')
    def professor_get_rating(self, request):
        user = endpoints.get_current_user()
        professor = Professor.get_professor(request.value)
        if not (professor and user):
            return RatingResponse()
        return RatingResponse(rating=createRatingMessage(professor.get_rating(user.email())))
