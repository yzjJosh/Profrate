package com.josh.profrate.dataStructures;

import com.appspot.profrate_1148.profrateAPI.model.ProfrateCommentReplyMessage;

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

}
