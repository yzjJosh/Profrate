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
        this.photo_url = user.getPhoto();
    }

    public static User getUser(String email) throws IOException{
        return BackendAPI.user_get(email);
    }

    public static boolean createUser(String name) throws IOException{
        return BackendAPI.user_create(name, Credential.getCredential());
    }

    public boolean editName(String name) throws IOException{
        return Credential.getCredential() != null && Credential.getCredential().getSelectedAccountName().equals(email) &&
                BackendAPI.user_edit_name(name, Credential.getCredential());
    }

    public String getPhotoUploadUrl() throws IOException{
        if(Credential.getCredential() == null || !Credential.getCredential().getSelectedAccountName().equals(email))
            return null;
        return BackendAPI.user_get_photo_upload_url(Credential.getCredential());
    }
}
