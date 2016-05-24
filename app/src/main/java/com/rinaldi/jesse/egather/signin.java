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
 * NAME
 *      signin
 * DESCRIPTION
 *      Activity to handle user signing in and out of their Google account.
 *      Inherits from AppCompatActivity
 * AUTHOR
 *      @author Jesse Rinaldi
 * DATE
 *      3/16/2016
 */
public class signin extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private SignInButton btnSignIn;
    private AndroidApplication application;

    /**
     * NAME
     *      signin.onCreate
     * SYNOPSIS
     *      @param savedInstanceState - used by system
     * DESCRIPTION
     *      On Create event for signin activity. If the intent used to open
     *      this activity use has the extra PERFORM_LOGOUT, sign out of Google account.
     *      (The activity will have that extra if the signin activity was opened from
     *      the user clicking MainActivity's Logout button)
     * AUTHOR
     *      @author Jesse Rinaldi
     */
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


    /**
     * NAME
     *      signin.onConnected
     * SYNOPSIS
     *      @param bundle - unused
     * DESCRIPTION
     *      On Connected to GoogleSignIn event
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("CONNECTED", "GoogleSignIn");
    }

    /**
     * NAME
     *      signin.onConnectionSuspended
     * SYNOPSIS
     *      @param i - unused
     * DESCRIPTION
     *      On Connection Suspended event for GoogleSignIn api. Just reconnect.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public void onConnectionSuspended(int i) {
        application.gAPI.connect();
    }

    /**
     * NAME
     *      signin.onClick
     * SYNOPSIS
     *      @param v - the view that was clicked
     * DESCRIPTION
     *      On Click event. If the sign in button was clicked, connect with GoogleSignInAPI
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignIn) {
            signInWithGoogle();
        }
    }

    /**
     * NAME
     *      signin.signInWithGoogle
     * DESCRIPTION
     *      Start the process of signing a user in through their Google account
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(application.gAPI);
        startActivityForResult(signInIntent, AndroidApplication.RC_SIGN_IN);
    }

    /**
     * NAME
     *      signin.onActivityResult
     * SYNOPSIS
     *      @param requestCode
     *      @param resultCode
     *      @param data
     * DESCRIPTION
     *      On Activity Result event upon return from the GoogleSignIn dialog.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == AndroidApplication.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * NAME
     *      signin.handleSignInResult
     * SYNOPSIS
     *      @param result - Result from the GoogleSignIn dialog
     * DESCRIPTION
     *      Handles the result. Authenticates the user upon success and stores the information.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
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

    /**
     * NAME
     *      signin.onConnectionFailed
     * SYNOPSIS
     *      @param connectionResult
     * DESCRIPTION
     *      Connection Failed event for the GoogleSignIn API
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(!connectionResult.hasResolution()) {
            Toast.makeText(this, "Connection Failed!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    /**
     * NAME
     *      signin.googleSignOut
     * DESCRIPTION
     *      Signs the user out of their Google account and deleted the user data
     *      so that the user will have to choose another account upon clicking
     *      the sign in button.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
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

    /**
     * NAME
     *      signin.onStart
     * DESCRIPTION
     *      On Start event. Reconnects to GoogleSignIn.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (application.gAPI != null) application.gAPI.connect();
    }

    /**
     * NAME
     *      signin.onBackPressed
     * DESCRIPTION
     *      On Back Pressed event for activity. Because super.OnBackPressed() is not called,
     *      the back button is disabled. This prevents the user from going back to MainActivity
     *      without signing in.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public void onBackPressed() {
    }
}
