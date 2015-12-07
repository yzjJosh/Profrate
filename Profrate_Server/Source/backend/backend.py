import endpoints

import comment_reply_api
import comment_api
import professor_api
import article_api
import user_api

APPLICATION = endpoints.api_server([comment_reply_api.CommentReplyAPI, comment_api.CommentAPI,
                                    professor_api.ProfessorAPI, article_api.ArticleAPI, user_api.UserAPI])