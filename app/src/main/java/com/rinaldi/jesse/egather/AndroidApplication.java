package com.rinaldi.jesse.egather;

import android.app.Application;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.common.api.GoogleApiClient;

public class AndroidApplication extends Application {
    public static final String FIREBASE_URL = "https://egather.firebaseio.com/";
    public GoogleApiClient gAPI;
    public static final int RC_SIGN_IN = 0;
    public Firebase mFirebaseRef;

    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(FIREBASE_URL);
    }
}
