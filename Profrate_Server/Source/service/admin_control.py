import webapp2
import json

from google.appengine.ext import blobstore
from google.appengine.ext.webapp import blobstore_handlers

from Source.frontend.template import renderTemplate
from Source.service.storage import Professor

template = 'admin.html'


class AdminPage(webapp2.RequestHandler):
    def get(self):
        parameters = dict()
        parameters['professors'] = Professor.get_all_professors()
        parameters['upload_url'] = blobstore.create_upload_url('/admin/upload')
        self.response.write(renderTemplate(template, parameters))


class UploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        upload = self.get_uploads('professor_info')[0]
        blob_reader = blobstore.BlobReader(upload.key())
        prof_info = json.loads(blob_reader.read())
        blobstore.delete(upload.key())
        for prof in prof_info:
            if len(Professor.query(Professor.name==prof['name'], Professor.title==prof['title']).fetch()) == 0:
                professor = Professor(name=prof['name'], title=prof['title'], special_title=prof['special title'],
                                      image=prof['image'], introduction=prof['introduction'], research_areas=prof['research areas'],
                                      research_interests=prof['research interests'], research_groups=prof['research groups'],
                                      office=prof['office'], phone=prof['phone'], email=prof['email'],
                                      personal_website=prof['personal website'])
            else:
                professor = Professor.query(Professor.name==prof['name'], Professor.title==prof['title']).fetch()[0]
                professor.special_title = prof['special title']
                professor.image = prof['image']
                professor.introduction = prof['introduction']
                professor.research_areas = prof['research areas']
                professor.research_interests = prof['research interests']
                professor.research_groups = prof['research groups']
                professor.office = prof['office']
                professor.phone=prof['phone']
                professor.email=prof['email']
                professor.personal_website=prof['personal website']
            professor.put()
        self.redirect('/admin', True)


app = webapp2.WSGIApplication([
    ('/admin/upload', UploadHandler),
    ('/admin', AdminPage)
], debug=True)