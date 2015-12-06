package com.josh.profrate.dataStructures;

import com.appspot.profrate_1148.profrateAPI.model.ProfrateArticleMessage;

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

    Article(ProfrateArticleMessage article){
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.author_email = article.getAuthorEmail();
        this.time = article.getTime();
        this.liked_by = article.getLikedBy()==null? new ArrayList<String>(): article.getLikedBy();
        this.disliked_by = article.getDislikedBy()==null? new ArrayList<String>(): article.getDislikedBy();
    }


}
