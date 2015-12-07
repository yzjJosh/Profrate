package com.josh.profrate.dataStructures;

import com.appspot.profrate_1148.profrateAPI.model.ProfrateUserMessage;
import com.josh.profrate.elements.Credential;

import java.io.IOException;

public class User {

    public final String email;
    public final String name;
    public final String photo_url;

    User(ProfrateUserMessage user){
        this.email = user.getEmail();
        this.name = user.getName();
        this.photo_url = "http://www.profrate-1148.appspot.com"+user.getPhoto();
    }

    public static User getUser(String email) throws IOException{
        return BackendAPI.user_get(email);
    }

    public static boolean createUser(String name, String photo_path) throws IOException{
        return BackendAPI.user_create(name, Credential.getCredential());
    }

    public boolean editName(String name) throws IOException{
        return BackendAPI.user_edit_name(name, Credential.getCredential());
    }
}
