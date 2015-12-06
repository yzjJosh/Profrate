package com.josh.profrate.dataStructures;

import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentMessage;
import com.josh.profrate.elements.Credential;

import java.io.IOException;
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

    public static Comment getComment(long comment_id) throws IOException {
        return BackendAPI.comment_get(comment_id);
    }

    public boolean edit(String content) throws IOException{
        return Credential.getCredential() != null && author_email.equals(Credential.getCredential().getSelectedAccountName())
             && BackendAPI.comment_edit(id, content, Credential.getCredential());
    }

    public boolean delete() throws IOException{
        return Credential.getCredential() != null && author_email.equals(Credential.getCredential().getSelectedAccountName())
                && BackendAPI.comment_delete(id, Credential.getCredential());
    }

    public List<CommentReply> getReplies() throws IOException{
        return BackendAPI.comment_get_replies(id);
    }

    public boolean like() throws IOException{
        return BackendAPI.comment_like(id, Credential.getCredential());
    }

    public boolean dislike() throws IOException{
        return BackendAPI.comment_dislike(id, Credential.getCredential());
    }

    public boolean reply(String content) throws IOException{
        return BackendAPI.comment_reply(id, content, Credential.getCredential());
    }

}
