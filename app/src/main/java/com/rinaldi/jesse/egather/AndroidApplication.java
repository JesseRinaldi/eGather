package com.rinaldi.jesse.egather;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;

public class AndroidApplication extends Application {
    public static final String FIREBASE_URL = "https://egather.firebaseio.com/";
    public GoogleApiClient gAPI;
    public static final int RC_SIGN_IN = 0;
    public Firebase mFirebaseRef;
    Firebase.AuthResultHandler authResultHandler;
    public GoogleSignInAccount user;

    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(FIREBASE_URL);

    }

    public void authUser(GoogleSignInAccount acct) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", acct.getDisplayName());
        map.put("email", acct.getEmail());
        map.put("photoURL", acct.getPhotoUrl().toString());
        mFirebaseRef.child("users").child(acct.getId()).setValue(map);
        user = acct;
    }
}
