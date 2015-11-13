package com.josh.profrate.elements;

import android.content.Context;

public class Credential {

    private static boolean login = false;

    public static boolean isLoggedIn(){
        return login;
    }

    public static boolean login(String email, Context context){
        return login = true;
    }

    public static void logout(){
        login = false;
    }

}
