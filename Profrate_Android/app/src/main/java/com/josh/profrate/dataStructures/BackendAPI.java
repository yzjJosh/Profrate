package com.josh.profrate.dataStructures;

import android.support.annotation.Nullable;

import com.appspot.profrate_1148.profrateAPI.ProfrateAPI;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateArticleMessage;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentEditRequest;
import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentMessage;
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

    static boolean professor_like(long prof_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(prof_id);
        return buildAPI(credential).professorLike(request).execute().getValue();
    }

    static boolean professor_dislike(long prof_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(prof_id);
        return buildAPI(credential).professorDislike(request).execute().getValue();
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

    static boolean comment_like(long comment_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(comment_id);
        return buildAPI(credential).commentLike(request).execute().getValue();
    }

    static boolean comment_dislike(long comment_id, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        SourceBackendAPIIntegerMessage request = new SourceBackendAPIIntegerMessage();
        request.setValue(comment_id);
        return buildAPI(credential).commentDislike(request).execute().getValue();
    }

    static boolean comment_reply(long comment_id, String content, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ProfrateReplyRequest request = new ProfrateReplyRequest();
        request.setId(comment_id);
        request.setContent(content);
        return buildAPI(credential).commentReply(request).execute().getValue();
    }

}
