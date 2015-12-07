package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.Credential;

public class UserTest extends ApplicationTestCase<Application> {

    public UserTest(){
        super(Application.class);
    }

    @Override
    public void setUp(){
        Credential.login("yangzijiangjosh@gmail.com", getContext());
    }

    @Override
    public void tearDown(){
        Credential.logout();
    }

    public void testUser() throws Exception{
        assertTrue(User.createUser("Zijiang Yang", null));
        User user = User.getUser("yangzijiangjosh@gmail.com");
        assertNotNull(user);
        assertEquals(user.name, "Zijiang Yang");
    }

}
