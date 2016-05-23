package com.rinaldi.jesse.egather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

/**
 * Created by Jesse on 3/16/2016.
 */
public class signin extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private SignInButton btnSignIn;
    private AndroidApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        btnSignIn = (SignInButton) findViewById(R.id.btnSignIn);
        getSupportActionBar().hide();

        application = (AndroidApplication) getApplicationContext();

        Intent intent = getIntent();
        if (intent.hasExtra("PERFORM_LOGOUT")) {
            googleSignOut();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.serverClientID))
                .requestEmail()
                .requestProfile()
                .build();

        application.buildGAPI(signin.this, gso);

        btnSignIn.setOnClickListener(this);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("CONNECTED", "GoogleSignIn");
    }

    @Override
    public void onConnectionSuspended(int i) {
        application.gAPI.connect();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignIn) {
            signInWithGoogle();
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(application.gAPI);
        startActivityForResult(signInIntent, AndroidApplication.RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == AndroidApplication.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        //Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(this, acct.getDisplayName() + " Connected", Toast.LENGTH_LONG).show();
            application.authUser(acct);
            super.finish();
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(!connectionResult.hasResolution()) {
            Toast.makeText(this, "Connection Failed!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void googleSignOut() {
        if (application.gAPI.isConnected()) {
            application.user = null;
            Auth.GoogleSignInApi.revokeAccess(application.gAPI).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()){
                                Log.d("REVOKE ACCESS", "Successful");
                            }
                        }
                    });
            Auth.GoogleSignInApi.signOut(application.gAPI);
            application.user = null;
        }
        else {
            Log.e("CONNECTION ERROR", "GoogleSignIn wasn't connected");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (application.gAPI != null) application.gAPI.connect();
    }

    @Override
    public void onBackPressed() {
    }
}
