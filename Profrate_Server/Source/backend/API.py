import endpoints

from protorpc import messages
from protorpc import message_types
from protorpc import remote

package = 'Profrate'
WEB_CLIENT_ID = '1012711565395-2s9q7jujl7lqi3ea7o1j0ujghfp08cag.apps.googleusercontent.com'
ANDROID_CLIENT_ID = '1012711565395-7sev6r5h9tcupra37tfi189m81eb9b98.apps.googleusercontent.com'
ANDROID_AUDIENCE = WEB_CLIENT_ID


@endpoints.api(name='profrateAPI', version='v1.0',
              allowed_client_ids=[WEB_CLIENT_ID, ANDROID_CLIENT_ID,
              endpoints.API_EXPLORER_CLIENT_ID],
              audiences=[ANDROID_AUDIENCE],
              scopes=[endpoints.EMAIL_SCOPE])
class BackendAPI(remote.Service):
    @endpoints.method(message_types.VoidMessage, message_types.VoidMessage, http_method='GET', name='test')
    def test(self, request):
        pass


APPLICATION = endpoints.api_server([BackendAPI])