package com.josh.profrate.dataStructures;

import com.appspot.profrate_1148.profrateAPI.model.ProfrateArticleMessage;
import com.josh.profrate.elements.Credential;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Article {

    public final long id;
    public final String title;
    public final String content;
    public final String author_email;
    public final List<String> liked_by;
    public final List<String> disliked_by;
    public final long time;
    public final long comment_num;

    Article(ProfrateArticleMessage article){
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.author_email = article.getAuthorEmail();
        this.time = article.getTime();
        this.liked_by = article.getLikedBy()==null? new ArrayList<String>(): article.getLikedBy();
        this.disliked_by = article.getDislikedBy()==null? new ArrayList<String>(): article.getDislikedBy();
        this.comment_num = article.getCommentNum();
    }

    public static Article getArticle(long article_id) throws IOException{
        return BackendAPI.article_get(article_id);
    }

    public boolean comment(String content) throws IOException{
        return BackendAPI.article_comment(id, content, Credential.getCredential());
    }

    public boolean toggle_like() throws IOException{
        return BackendAPI.article_toggle_like(id, Credential.getCredential());
    }

    public boolean toggle_dislike() throws IOException{
        return BackendAPI.article_toggle_dislike(id, Credential.getCredential());
    }

    public boolean edit(String title, String content) throws IOException{
        return Credential.getCredential() != null && Credential.getCredential().getSelectedAccountName().equals(author_email)
                && BackendAPI.article_edit(id, title, content, Credential.getCredential());
    }

    public boolean delete() throws IOException{
        return Credential.getCredential() != null && Credential.getCredential().getSelectedAccountName().equals(author_email)
                && BackendAPI.article_delete(id, Credential.getCredential());
    }

    public List<Comment> getComments() throws IOException{
        return BackendAPI.article_get_comments(id);
    }

}
