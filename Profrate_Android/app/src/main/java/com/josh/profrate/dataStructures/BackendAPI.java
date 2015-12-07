package com.josh.profrate.dataStructures;

import android.support.annotation.Nullable;

import com.appspot.profrate_1148.profrateAPI.ProfrateAPI;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateArticleCommentRequest;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateArticleEditRequest;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateArticleMessage;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentEditRequest;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentMessage;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentReplyEditRequest;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentReplyMessage;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateProfessorCommentRequest;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateProfessorMessage;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateRateRequest;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateRatingMessage;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateReplyRequest;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateWriteArtilceRequest;
import com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIIntegerMessage;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackendAPI {

    private static ProfrateAPI buildAPI(@Nullable GoogleAccountCredential credential){
        ProfrateAPI.Builder profrate = new ProfrateAPI.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), credential);
        return profrate.build();
    }

    static boolean professor_comment(long prof_id, String content, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ProfrateProfessorCommentRequest request = new ProfrateProfessorCommentRequest();
        request.setId(prof_id);
        request.setContent(content);
        return buildAPI(credential).professorComment(request).execute().getValue();
    }

    static boolean professor_rate(long prof_id, double personality, double teaching_skill, double research_skill,
                                         double knowledge_level, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ProfrateRateRequest request = new ProfrateRateRequest();
        request.setId(prof_id);
        request.setPersonality(personality);
        request.setTeachingSkill(teaching_skill);
        request.setResearchSkill(research_skill);
        request.setKnowledgeLevel(knowledge_level);
        return buildAPI(credential).professorRate(request).execute().getValue();
    }

    static boolean professor_write_article(long prof_id, String title, String content,
                                                  GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ProfrateWriteArtilceRequest request = new ProfrateWriteArtilceRequest();
        request.setId(prof_id);
        request.setTitle(title);
        request.setContent(content);
        return buildAPI(credential).professorWriteArticle(request).execute().getValue();
    }

    static boolean professor_toggle_like(long prof_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(prof_id);
        return buildAPI(credential).professorToggleLike(request).execute().getValue();
    }

    static boolean professor_toggle_dislike(long prof_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(prof_id);
        return buildAPI(credential).professorToggleDislike(request).execute().getValue();
    }

    static Professor professor_get(long prof_id) throws IOException{
        ProfrateProfessorMessage prof = buildAPI(null).professorGet(prof_id).execute().getProfessor();
        return prof==null? null: new Professor(prof);
    }

    static List<Professor> professor_get_all() throws IOException{
        List<Professor> ret = new ArrayList<Professor>();
        List<ProfrateProfessorMessage> professors = buildAPI(null).professorGetAll().execute().getProfessors();
        if(professors != null)
            for(ProfrateProfessorMessage prof: professors)
                ret.add(new Professor(prof));
        return ret;
    }

    static List<Comment> professor_get_comments(long prof_id) throws IOException{
        List<Comment> ret = new ArrayList<Comment>();
        List<ProfrateCommentMessage> comments = buildAPI(null).professorGetComments(prof_id).execute().getComments();
        if(comments != null)
            for(ProfrateCommentMessage comment: comments)
                ret.add(new Comment(comment));
        return ret;
    }

    static Rating professor_get_rating(long prof_id, GoogleAccountCredential credential) throws IOException{
        ProfrateRatingMessage rating = buildAPI(credential).professorGetRating(prof_id).execute().getRating();
        return rating==null? null: new Rating(rating);
    }

    static List<Article> professor_get_articles(long prof_id) throws IOException{
        List<Article> ret = new ArrayList<Article>();
        List<ProfrateArticleMessage> articles = buildAPI(null).professorGetArticles(prof_id).execute().getArticles();
        if(articles != null)
            for(ProfrateArticleMessage article: articles)
                ret.add(new Article(article));
        return ret;
    }

    static boolean comment_edit(long comment_id, String content, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ProfrateCommentEditRequest request = new ProfrateCommentEditRequest();
        request.setId(comment_id);
        request.setContent(content);
        return buildAPI(credential).commentEdit(request).execute().getValue();
    }

    static boolean comment_delete(long comment_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(comment_id);
        return buildAPI(credential).commentDelete(request).execute().getValue();
    }

    static Comment comment_get(long comment_id) throws IOException{
        ProfrateCommentMessage comment = buildAPI(null).commentGet(comment_id).execute().getComment();
        return comment==null? null: new Comment(comment);
    }

    static List<CommentReply> comment_get_replies(long comment_id) throws IOException{
        List<CommentReply> ret = new ArrayList<CommentReply>();
        List<ProfrateCommentReplyMessage> repies = buildAPI(null).commentGetReplies(comment_id).execute().getCommentReplies();
        if(repies != null)
            for(ProfrateCommentReplyMessage reply: repies)
                ret.add(new CommentReply(reply));
        return ret;
    }

    static boolean comment_toggle_like(long comment_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(comment_id);
        return buildAPI(credential).commentToggleLike(request).execute().getValue();
    }

    static boolean comment_toggle_dislike(long comment_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(comment_id);
        return buildAPI(credential).commentToggleDislike(request).execute().getValue();
    }

    static boolean comment_reply(long comment_id, String content, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ProfrateReplyRequest request = new ProfrateReplyRequest();
        request.setId(comment_id);
        request.setContent(content);
        return buildAPI(credential).commentReply(request).execute().getValue();
    }

    static boolean comment_reply_edit(long reply_id, String content, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ProfrateCommentReplyEditRequest request = new ProfrateCommentReplyEditRequest();
        request.setId(reply_id);
        request.setContent(content);
        return buildAPI(credential).commentReplyEdit(request).execute().getValue();
    }

    static boolean comment_reply_delete(long reply_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(reply_id);
        return buildAPI(credential).commentReplyDelete(request).execute().getValue();
    }

    static CommentReply comment_reply_get(long reply_id) throws IOException{
        ProfrateCommentReplyMessage reply = buildAPI(null).commentReplyGet(reply_id).execute().getCommentReply();
        return reply==null? null: new CommentReply(reply);
    }

    static boolean article_comment(long article_id, String content, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ProfrateArticleCommentRequest request =  new ProfrateArticleCommentRequest();
        request.setId(article_id);
        request.setContent(content);
        return buildAPI(credential).articleComment(request).execute().getValue();
    }

    static boolean article_toggle_like(long article_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(article_id);
        return buildAPI(credential).articleToggleLike(request).execute().getValue();
    }

    static boolean article_toggle_dislike(long article_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(article_id);
        return buildAPI(credential).articleToggleDislike(request).execute().getValue();
    }

    static boolean article_edit(long article_id, String title, String content, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ProfrateArticleEditRequest request = new ProfrateArticleEditRequest();
        request.setId(article_id);
        request.setTitle(title);
        request.setContent(content);
        return buildAPI(credential).articleEdit(request).execute().getValue();
    }

    static boolean article_delete(long article_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(article_id);
        return buildAPI(credential).articleDelete(request).execute().getValue();
    }

    static Article article_get(long article_id) throws IOException{
        ProfrateArticleMessage article = buildAPI(null).articleGet(article_id).execute().getArticle();
        return article==null? null: new Article(article);
    }

    static List<Comment> article_get_comments(long article_id) throws IOException{
        List<Comment> ret = new ArrayList<Comment>();
        List<ProfrateCommentMessage> comments = buildAPI(null).articleGetComments(article_id).execute().getComments();
        if(comments != null)
            for(ProfrateCommentMessage comment: comments)
                ret.add(new Comment(comment));
        return ret;
    }

}
