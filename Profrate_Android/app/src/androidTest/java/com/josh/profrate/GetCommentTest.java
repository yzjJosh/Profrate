package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.Comment;
import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.elements.Credential;

import java.util.List;

public class GetCommentTest extends ApplicationTestCase<Application> {

    public GetCommentTest(){
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
        Professor professor = Professor.getProfessor(6245567495667712L);
        assertTrue(professor.comment("1"));
        assertTrue(professor.comment("2"));
        assertTrue(professor.comment("3"));
        assertTrue(professor.comment("4"));
        List<Comment> comments = professor.getComments();
        for(Comment comment: comments){
            Comment got = Comment.getComment(comment.id);
            assertNotNull(got);
            assertEquals(comment.id, got.id);
            assertEquals(comment.content, got.content);
        }
    }


}
