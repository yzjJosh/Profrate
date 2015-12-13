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
    public final long reply_num;

    Comment(ProfrateCommentMessage comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.time = comment.getTime();
        this.author_email = comment.getAuthorEmail();
        this.liked_by = comment.getLikedBy()==null? new ArrayList<String>(): comment.getLikedBy();
        this.disliked_by = comment.getDislikedBy()==null? new ArrayList<String>(): comment.getDislikedBy();
        this.reply_num = comment.getReplyNum();
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

    public boolean toggle_like() throws IOException{
        return BackendAPI.comment_toggle_like(id, Credential.getCredential());
    }

    public boolean toggle_dislike() throws IOException{
        return BackendAPI.comment_toggle_dislike(id, Credential.getCredential());
    }

    public long reply(String content) throws IOException{
        return BackendAPI.comment_reply(id, content, Credential.getCredential());
    }

}
