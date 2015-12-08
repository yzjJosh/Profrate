import endpoints
import API
from Source.service.storage import User
from protorpc import remote
from protorpc import messages
from protorpc import message_types

package='Profrate'


class UserMessage(messages.Message):
    email = messages.StringField(1, required=True)
    name = messages.StringField(2, required=True)
    photo = messages.StringField(3, required=True)


def createUserMessage(user):
    return UserMessage(
        email=user.get_email(),
        name=user.name,
        photo=user.get_photo_url()
    )


class UserResponse(messages.Message):
    user = messages.MessageField(UserMessage, 1)


@API.ProfrateAPI.api_class()
class UserAPI(remote.Service):
    @endpoints.method(API.StringMessage, UserResponse, http_method='GET', name='user_get')
    def user_get(self, request):
        user = User.find_User(request.value)
        if not user:
            return UserResponse()
        return UserResponse(user=createUserMessage(user))

    @endpoints.method(message_types.VoidMessage, API.StringMessage, http_method='GET', name='user_get_photo_upload_url')
    def user_get_photo_upload_url(self, request):
        user = endpoints.get_current_user()
        if not user:
            return API.StringMessage()
        USER = User.find_User(user.email())
        if not USER:
            return API.StringMessage()
        url = USER.get_photo_upload_url()
        if url == '/assets/images/default_user_photo.png':
            url = 'http://www.profrate-1148.appspot.com'+url
        return API.StringMessage(value=url)

    @endpoints.method(API.StringMessage, API.BooleanMessage, http_method='POST', name='user_create')
    def user_create(self, request):
        user = endpoints.get_current_user()
        if not user:
            return API.BooleanMessage(value=False)
        if User.find_User(user.email()):
            return API.BooleanMessage(value=False)
        if len(User.query(User.name==request.value, ancestor=User.parent_key()).fetch()) > 0:
            return API.BooleanMessage(value=False)
        User.create_User(user.email(), request.value)
        return API.BooleanMessage(value=True)

    @endpoints.method(API.StringMessage, API.BooleanMessage, http_method='POST', name='user_edit_name')
    def user_edit_name(self, request):
        user = endpoints.get_current_user()
        if not user:
            return API.BooleanMessage(value=False)
        USER = User.find_User(user.email())
        if not USER:
            return API.BooleanMessage(value=False)
        if len(User.query(User.name==request.value, ancestor=User.parent_key()).fetch()) > 0:
            return API.BooleanMessage(value=False)
        USER.edit_name(request.value)
        return API.BooleanMessage(value=True)