cd Profrate_Server
/usr/local/google_appengine/endpointscfg.py get_client_lib java -bs gradle -o . Source.backend.article_api.ArticleAPI Source.backend.comment_api.CommentAPI Source.backend.comment_reply_api.CommentReplyAPI Source.backend.professor_api.ProfessorAPI
unzip -a profrateAPI-v1.0.zip
rm profrateAPI-v1.0.zip
cd ..
rm -r Profrate_Android/profrateAPI
mv Profrate_Server/profrateAPI Profrate_Android
