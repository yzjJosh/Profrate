package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.CommentReply;
import com.josh.profrate.elements.Credential;

public class GetReplyTest extends ApplicationTestCase<Application> {

    public GetReplyTest(){
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        Credential.login("yangzijiangjosh@gmail.com", getContext());
    }

    @Override
    public void tearDown() throws Exception {
        Credential.logout();
    }

    public void testGet() throws Exception{
        Comment comment = Comment.getComment(5144752345317376L);
        assertNotNull(comment);
        assertTrue(comment.getReplies().size() > 0);
        for(CommentReply reply: comment.getReplies()){
            CommentReply got = CommentReply.getCommentReply(reply.id);
            assertEquals(reply.id, got.id);
            assertEquals(reply.content, got.content);
        }
    }
}
