package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.Professor;
import com.josh.profrate.elements.Credential;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class GetProfessorTest extends ApplicationTestCase<Application> {
    public GetProfessorTest() {
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

    public void testGetProfessor() throws Exception{
        List<Professor> professors = Professor.getAllProfessors();
        assertNotNull(professors);
        assertTrue(professors.size() > 0);
        for(Professor prof: professors){
            Professor got = Professor.getProfessor(prof.id);
            assertEquals(got.id, prof.id);
            assertEquals(got.name, prof.name);
            assertEquals(got.title, prof.title);
        }
        Professor cannotFound = Professor.getProfessor(1798129157177344L);
        assertNull(cannotFound);
    }

}