package com.josh.profrate.dataStructures;

import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentReplyMessage;
import com.josh.profrate.elements.Credential;

import java.io.IOException;

public class CommentReply {

    public final long id;
    public final String content;
    public final long time;
    public final String author_email;

    CommentReply(ProfrateCommentReplyMessage reply){
        this.id = reply.getId();
        this.content = reply.getContent();
        this.time = reply.getTime();
        this.author_email = reply.getAuthorEmail();
    }

    public static CommentReply getCommentReply(long reply_id) throws IOException{
        return BackendAPI.comment_reply_get(reply_id);
    }

    public boolean edit(String content) throws IOException{
        return Credential.getCredential()!=null && Credential.getCredential().getSelectedAccountName().equals(author_email)
                && BackendAPI.comment_reply_edit(id, content, Credential.getCredential());
    }

    public boolean delete() throws IOException{
        return Credential.getCredential()!=null && Credential.getCredential().getSelectedAccountName().equals(author_email)
                && BackendAPI.comment_reply_delete(id, Credential.getCredential());
    }

}
