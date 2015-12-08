package com.josh.profrate.elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.josh.profrate.dataStructures.User;

import java.io.IOException;

public class Credential {

    private static final String LOG_TAG = "Credential";
    private static final String WEB_CLIENT_ID = "1012711565395-2s9q7jujl7lqi3ea7o1j0ujghfp08cag.apps.googleusercontent.com";
    private static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;
    private static GoogleAccountCredential credential;
    private static User currentUser;
    private static Bitmap photo;

    public static GoogleAccountCredential getCredential(){
        return credential;
    }

    public static boolean isLoggedIn(){
        return credential != null;
    }

    public static void logout(){
        credential = null;
        currentUser = null;
        photo = null;
    }

    public static boolean login(String email, Context context){
        logout();
        Log.i(LOG_TAG, "Try to log in using " + email);
        if (!checkGooglePlayServicesAvailable(context)) return false;
        if(email == null || email.length() == 0) return false;
        try {
            // If the application has the appropriate access then a token will be retrieved, otherwise
            // an error will be thrown.
            GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, AUDIENCE);
            credential.setSelectedAccountName(email);
            credential.getToken();
            Credential.credential = credential;
            Log.d(LOG_TAG, "AccessToken retrieved");
            // Success.
            return true;
        } catch (GoogleAuthException unrecoverableException) {
            Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", unrecoverableException);
            // Failure.
            return false;
        } catch (IOException ioException) {
            Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", ioException);
            // Failure or cancel request.
            return false;
        }
    }

    private static boolean checkGooglePlayServicesAvailable(Context context) {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode))
            return false;
        return true;
    }


    public static boolean loadCurrentUser() throws IOException{
        if(credential == null) return false;
        currentUser = User.getUser(credential.getSelectedAccountName());
        if(currentUser != null)
            photo = BitmapFetcher.fetchBitmap(currentUser.photo_url);
        return true;
    }

    public static User getCurrentUser(){
        return currentUser;
    }

    public static Bitmap getCurrentUserPhoto(){
        return photo;
    }


}
