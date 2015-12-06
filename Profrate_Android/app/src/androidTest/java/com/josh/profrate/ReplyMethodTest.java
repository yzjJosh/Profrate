package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.CommentReply;
import com.josh.profrate.elements.Credential;

public class ReplyMethodTest extends ApplicationTestCase<Application> {

    private final String user_email = "yangzijiangjosh@gmail.com";
    private CommentReply reply;

    public ReplyMethodTest(){
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        Credential.login("yangzijiangjosh@gmail.com", getContext());
        reply = CommentReply.getCommentReply(5629499534213120L);
    }

    @Override
    public void tearDown() throws Exception {
        Credential.logout();
        reply = null;
    }

    public void testEdit() throws Exception{
        assertTrue(reply.edit("Josh"));
        reply = CommentReply.getCommentReply(reply.id);
        assertNotNull(reply);
        assertEquals(reply.content, "Josh");
        assertTrue(reply.edit("Jim"));
        reply = CommentReply.getCommentReply(reply.id);
        assertNotNull(reply);
        assertEquals(reply.content, "Jim");
    }

    public void testDelete() throws Exception{
        reply = CommentReply.getCommentReply(5741031244955648L);
        assertNotNull(reply);
        assertTrue(reply.delete());
        assertNull(CommentReply.getCommentReply(5741031244955648L));
        assertFalse(reply.delete());
        assertFalse(reply.edit("sdfdg"));
    }

}
