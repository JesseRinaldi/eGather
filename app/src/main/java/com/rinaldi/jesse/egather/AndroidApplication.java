package com.rinaldi.jesse.egather;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;

/**
 * NAME
 *      AndroidApplication
 * DESCRIPTION
 *      Class which represents this entire application. Inherits from Application.
 *      Mainly used for global variables which must be used across the app.
 * AUTHOR
 *      @author Jesse Rinaldi
 * DATE
 *      3/10/2016
 */
public class AndroidApplication extends Application  {
    public static final String FIREBASE_URL = "https://egather.firebaseio.com/";
    public GoogleApiClient gAPI;
    public static final int RC_SIGN_IN = 0;
    public Firebase mFirebaseRef;
    Firebase.AuthResultHandler authResultHandler;
    public GoogleSignInAccount user;
    public Event activeEvent;
    public String activeEventID;

    /**
     * NAME
     *      AndroidApplication.onCreate
     * DESCRIPTION
     *      On Create event for the application. Executed when the app is first opened.
     *      Sets the connection to Firebase.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(FIREBASE_URL);

    }

    /**
     * NAME
     *      AndroidApplication.buildGAPI
     * SYNOPSIS
     *      @param msignin - The signin activity instance connecting
     *      @param gso - The options for our GoogleSignIn
     * DESCRIPTION
     *      This is used to create the GoogleSignIn instance and store
     *      it globally.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    public void buildGAPI(signin msignin, GoogleSignInOptions gso) {
        gAPI = new GoogleApiClient.Builder(msignin)
                .addConnectionCallbacks(msignin)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * NAME
     *      AndroidApplication.authUser
     * SYNOPSIS
     *      @param acct - The Google user account that was signed in
     * DESCRIPTION
     *      Stores the user's account globally and sends the user info to Firebase.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    public void authUser(GoogleSignInAccount acct) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", acct.getDisplayName());
        map.put("email", acct.getEmail());
        if (acct.getPhotoUrl() == null) {
            map.put("photoURL", "");
        }
        else {
            map.put("photoURL", acct.getPhotoUrl().toString());
        }
        mFirebaseRef.child("users").child(acct.getId()).setValue(map);
        user = acct;
    }
}
