package com.rinaldi.jesse.egather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * NAME
 *      EventInvite
 * DESCRIPTION
 *      EventInvite Activity. Uses activity_event_invite.xml. Inherits from AppCompatActivity.
 *      Users can invite users to an event from this activity.
 * AUTHOR
 *      @author Jesse Rinaldi
 * DATE
 *      5/23/2016
 */
public class EventInvite extends AppCompatActivity {

    private ListView lstUsers;
    private SearchView svUsers;
    private AndroidApplication app;
    private ArrayList<Map<String, String>> users;

    /**
     * NAME
     *      EventInvite.onCreate
     * SYNOPSIS
     *      @param savedInstanceState - used by system
     * DESCRIPTION
     *      On Create event for EventInvite activity. Sets up the widgets on the page.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_invite);
        getSupportActionBar().hide();
        app = (AndroidApplication) getApplicationContext();
        users = new ArrayList<Map<String,String>>();
        lstUsers = (ListView) findViewById(R.id.lstUsers);
        setLstListener();
        svUsers = (SearchView)findViewById(R.id.svUsers);
        setSVListener();
     }

    /**
     * NAME
     *      signin.setLstListener
     * DESCRIPTION
     *      On Item Click event for the listView of users. The user is displayed a yes/no dialog
     *      to invite the user they selected.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void setLstListener() {
        lstUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> paret, final View viewClicked, int position, long id) {
                final String userID = ((TextView) viewClicked.findViewById(R.id.txtUserID)).getText().toString();
                final String username = ((TextView) viewClicked.findViewById(R.id.txtDisplayName)).getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(EventInvite.this);
                builder.setTitle("Invite user " + username + "?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        app.mFirebaseRef.child("invites").child(userID).child(app.activeEventID).setValue(app.user.getId());
                        viewClicked.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    /**
     * NAME
     *      signin.setSVListener
     * DESCRIPTION
     *      On Query Text Listener for the searchview. Searches for users
     *      which have the searchview contents in their name or email from Firebase
     *      and puts them in the listView. User data is stored in Maps.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void setSVListener() {
        svUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                users.clear();
                app.mFirebaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsUser : dataSnapshot.getChildren()) {
                            if ((dsUser.child("name").getValue(String.class).toLowerCase().contains(svUsers.getQuery().toString().toLowerCase())
                                    || dsUser.child("email").getValue(String.class).toLowerCase().contains(svUsers.getQuery().toString().toLowerCase()))
                                    && !app.user.getEmail().equals(dsUser.child("email").getValue(String.class))){
                                Map<String, String> user = new HashMap<String,String>();
                                user.put("ID", dsUser.getKey());
                                user.put("name", (String) dsUser.child("name").getValue());
                                user.put("email", (String) dsUser.child("email").getValue());
                                user.put("photoURL", (String) dsUser.child("photoURL").getValue());
                                users.add(user);
                            }
                        }
                        populateUserList();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.e("FIREBASE ERROR", firebaseError.getMessage());
                        populateUserList();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    users.clear();
                    populateUserList();
                }
                return false;
            }
        });
    }

    /**
     * NAME
     *      signin.populateUserList
     * DESCRIPTION
     *      Populates the listView of users with a UserAdapter from the users
     *      found from the search query.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void populateUserList() {
        UserAdapter userAdapter = new UserAdapter(this, users.toArray((Map<String, String>[]) new Map[users.size()]));
        lstUsers.setAdapter(userAdapter);
    }

}
