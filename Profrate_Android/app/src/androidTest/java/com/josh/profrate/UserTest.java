package com.josh.profrate;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.Credential;

public class UserTest extends ApplicationTestCase<Application> {

    private final String account = "yangzijiangjosh@gmail.com";

    public UserTest(){
        super(Application.class);
    }

    @Override
    public void setUp(){
        Credential.login(account, getContext());
    }

    @Override
    public void tearDown(){
        Credential.logout();
    }

    public void testUser() throws Exception{
        User user = User.getUser(account);
        assertNotNull(user);
        assertTrue(user.editName("Fuck!!!"));
    }

}
