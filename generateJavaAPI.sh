cd Profrate_Server
/usr/local/google_appengine/endpointscfg.py get_client_lib java -bs gradle -o . Source.backend.article_api.ArticleAPI
/usr/local/google_appengine/endpointscfg.py get_client_lib java -bs gradle -o . Source.backend.comment_api.CommentAPI
/usr/local/google_appengine/endpointscfg.py get_client_lib java -bs gradle -o . Source.backend.comment_reply_api.CommentReplyAPI
/usr/local/google_appengine/endpointscfg.py get_client_lib java -bs gradle -o . Source.backend.professor_api.ProfessorAPI
unzip -a profrateAPI-v1.0.zip
rm profrateAPI-v1.0.zip
cd ..
rm -r Profrate_Android/profrateAPI
mv Profrate_Server/profrateAPI Profrate_Android