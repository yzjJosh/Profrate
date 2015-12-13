package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.elements.Credential;

public class CommentMethodTest extends ApplicationTestCase<Application> {

    private final String user_account = "yangzijiangjosh@gmail.com";
    private Comment comment;

    public CommentMethodTest(){
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        Credential.login(user_account, getContext());
        comment = Comment.getComment(5697982787747840L);
    }

    @Override
    public void tearDown() throws Exception {
        Credential.logout();
        comment = null;
    }

    public void testEdit() throws Exception{
        assertTrue(comment.edit("I fuck you!"));
        comment = Comment.getComment(comment.id);
        assertNotNull(comment);
        assertEquals(comment.content, "I fuck you!");
    }

    public void testReply() throws Exception{
        assertEquals(comment.getReplies().size(), 0);
        assertTrue(comment.reply("1") != -1);
        assertEquals(comment.getReplies().size(), 1);
        assertTrue(comment.reply("2") != -1);
        assertEquals(comment.getReplies().size(), 2);
        assertTrue(comment.reply("3") != -1);
        assertEquals(comment.getReplies().size(), 3);
        assertTrue(comment.reply("4") != -1);
        assertEquals(comment.getReplies().size(), 4);
        assertTrue(comment.reply("5") != -1);
        assertEquals(comment.getReplies().size(), 5);
    }

    public void testLike() throws Exception{
        assertTrue(comment.toggle_like());
        comment = Comment.getComment(comment.id);
        assertNotNull(comment);
        assertTrue(comment.liked_by.contains(user_account));
        assertFalse(comment.disliked_by.contains(user_account));
        assertTrue(comment.toggle_dislike());
        comment = Comment.getComment(comment.id);
        assertNotNull(comment);
        assertTrue(comment.disliked_by.contains(user_account));
        assertFalse(comment.liked_by.contains(user_account));
    }


}
