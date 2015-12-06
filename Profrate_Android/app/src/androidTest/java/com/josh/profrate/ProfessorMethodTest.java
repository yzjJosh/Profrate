package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.dataStructures.Rating;
import com.josh.profrate.elements.Credential;

public class ProfessorMethodTest extends ApplicationTestCase<Application> {

    private Professor professor;
    private  final String user_email = "yangzijiangjosh@gmail.com";


    public ProfessorMethodTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        Credential.login(user_email, getContext());
        professor = Professor.getProfessor(6245567495667712L);
    }

    @Override
    public void tearDown() throws Exception {
        Credential.logout();
        professor = null;
    }

    public void testRate() throws Exception{
        assertNull(professor.getRating());
        Rating rating = new Rating(4.5, 3.2, 4.6, 5.0);
        assertTrue(professor.rate(rating));
        Rating got = professor.getRating();
        assertNotNull(got);
        assertEquals(rating.personality, got.personality, 0.01);
        assertEquals(rating.teaching_skill, got.teaching_skill, 0.01);
        assertEquals(rating.research_skill, got.research_skill, 0.01);
        assertEquals(rating.knowledge_level, got.knowledge_level, 0.01);
    }

    public void testComment() throws Exception{
        assertEquals(professor.getComments().size(), 0);
        assertTrue(professor.comment("I love him!"));
        assertEquals(professor.getComments().size(), 1);
        assertTrue(professor.comment("Hahaha!"));
        assertEquals(professor.getComments().size(), 2);
    }

    public void testArticle() throws Exception{
        assertEquals(professor.getArticles().size(), 0);
        assertTrue(professor.writeArticle("f", ""));
        assertEquals(professor.getArticles().size(), 1);
        assertTrue(professor.writeArticle("sfdsd", "sdfsdg"));
        assertEquals(professor.getArticles().size(), 2);
    }

    public void testLike() throws Exception{
        assertTrue(professor.like());
        professor = Professor.getProfessor(professor.id);
        assertNotNull(professor);
        assertTrue(professor.liked_by.contains(user_email));
        assertFalse(professor.disliked_by.contains(user_email));
        assertTrue(professor.dislike());
        professor = Professor.getProfessor(professor.id);
        assertNotNull(professor);
        assertTrue(professor.disliked_by.contains(user_email));
        assertFalse(professor.liked_by.contains(user_email));
    }
}
