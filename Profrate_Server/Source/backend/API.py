import endpoints
from protorpc import messages

WEB_CLIENT_ID = '1012711565395-2s9q7jujl7lqi3ea7o1j0ujghfp08cag.apps.googleusercontent.com'
ANDROID_CLIENT_ID = '1012711565395-7sev6r5h9tcupra37tfi189m81eb9b98.apps.googleusercontent.com'
ANDROID_AUDIENCE = WEB_CLIENT_ID

ProfrateAPI = endpoints.api(name='profrateAPI', version='v1.0',
                            allowed_client_ids=[WEB_CLIENT_ID, ANDROID_CLIENT_ID,
                            endpoints.API_EXPLORER_CLIENT_ID],
                            audiences=[ANDROID_AUDIENCE],
                            scopes=[endpoints.EMAIL_SCOPE])


class IntegerMessage(messages.Message):
    value = messages.IntegerField(1, required=True)


class BooleanMessage(messages.Message):
    value = messages.BooleanField(1, required=True)


class StringMessage(messages.Message):
    value = messages.StringField(1, required=True)

