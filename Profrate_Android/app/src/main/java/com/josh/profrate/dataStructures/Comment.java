package com.josh.profrate.dataStructures;

import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentMessage;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    public final long id;
    public final String content;
    public final long time;
    public final String author_email;
    public final List<String> liked_by;
    public final List<String> disliked_by;

    Comment(ProfrateCommentMessage comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.time = comment.getTime();
        this.author_email = comment.getAuthorEmail();
        this.liked_by = comment.getLikedBy()==null? new ArrayList<String>(): comment.getLikedBy();
        this.disliked_by = comment.getDislikedBy()==null? new ArrayList<String>(): comment.getDislikedBy();
    }

}
