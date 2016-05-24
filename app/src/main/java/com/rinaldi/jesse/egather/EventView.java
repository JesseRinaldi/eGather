package com.rinaldi.jesse.egather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * NAME
 *      EventView
 * DESCRIPTION
 *      This class is an activity which presents the information associated with
 *      an event. It uses activity_event_view.xml and inherits from AppCompatActivity.
 *      The event to use is stored in AndroidApplication.activeEvent.
 * AUTHOR
 *      @author Jesse Rinaldi
 * DATE
 *      3/30/2016
 */
public class EventView extends AppCompatActivity {

    private TextView txtName, txtLocation, txtDateTime, txtDescription, txtWebsite, txtCategory, txtMod, txtInvitedBy;
    private ImageView ivPhoto;
    private Button btnAttendEvent;
    private boolean attending = false, invited = false;
    private Event event;
    AndroidApplication app;

    /**
     * NAME
     *      EventView.onCreate
     * SYNOPSIS
     *      @param savedInstanceState - Used by Android to restore Activity to previous state
     * DESCRIPTION
     *      On create event for EventView. Sets the content to activity_event_view.xml.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        app = (AndroidApplication) getApplicationContext();
    }

    /**
     * NAME
     *      EventView.onResume
     * DESCRIPTION
     *      On resume event called when the activity is created or is
     *      returned to from another activity.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    protected void onResume() {
        super.onResume();
        event = app.activeEvent;
        bindWidgets();
        setWidgets();
        setAttending();
        setInvited();
    }

    /**
     * NAME
     *      EventView.bindWidgets
     * DESCRIPTION
     *      Binds each xml widget to a variable.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void bindWidgets() {
        txtName = (TextView) findViewById(R.id.txtNameView);
        txtLocation = (TextView) findViewById(R.id.txtLocationView);
        txtDateTime = (TextView) findViewById(R.id.txtDateTimeView);
        txtWebsite = (TextView) findViewById(R.id.txtWebsiteView);
        txtDescription = (TextView) findViewById(R.id.txtDescriptionView);
        txtCategory = (TextView) findViewById(R.id.txtCategoryView);
        txtMod = (TextView) findViewById(R.id.txtCreatedByView);
        txtInvitedBy = (TextView) findViewById(R.id.txtInvitedByView);
        ivPhoto = (ImageView) findViewById(R.id.ivPhotoView);
        btnAttendEvent = (Button) findViewById(R.id.btnAttendEvent);
    }

    /**
     * NAME
     *      EventView.setWidgets
     * DESCRIPTION
     *      Takes all the widgets bound by bindWidgets and populates
     *      them with the data from the event.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void setWidgets() {

        //Set name and title to name
        String name = event.getName();
        txtName.setText(name);
        setTitle(name);

        //Set the address
        //If the event was at a location with a proper name, display that as well
        if (!event.getLocationAddress().contains(event.getLocationName())) {
            txtLocation.setText(event.getLocationName() + ", " + event.getLocationAddress());
        }
        else {
            txtLocation.setText(event.getLocationAddress());
        }

        //Set the date and time
        StringBuilder dateTime = new StringBuilder(event.getMonth()  + " / " + event.getDay() + " / " + event.getYear());
        if (event.getStartHour() == -1) {}
        else if (event.getEndHour() == -1) {
            dateTime.append(" at " + resolveEventTime(event.getStartHour(), event.getStartMinute()));
        }
        else {
            dateTime.append(" from " + resolveEventTime(event.getStartHour(), event.getStartMinute()) + " to " + resolveEventTime(event.getEndHour(), event.getEndMinute()));
        }
        txtDateTime.setText(dateTime.toString());

        //Create a hyperlink for website and title
        String website = event.getWebsite();
        String websiteTitle = event.getWebsiteTitle();
        if(!website.isEmpty() && URLUtil.isValidUrl(website)) {
            if (websiteTitle.isEmpty()) websiteTitle = website;
            txtWebsite.setClickable(true);
            txtWebsite.setMovementMethod(LinkMovementMethod.getInstance());
            String link = "<a href='" + website + "'>" + websiteTitle + "</a>";
            txtWebsite.setText(Html.fromHtml(link));
        }
        else {
            txtWebsite.setVisibility(View.GONE);
        }

        //Set the description and category
        if (!event.getBody().isEmpty()) txtDescription.setText(event.getBody());
        else txtDescription.setVisibility(View.GONE);
        txtCategory.setText("Category: " + event.getCategory());

        //Get display name from mod ID through Firebase and show it
        txtMod.setVisibility(View.GONE);
        if (!event.getMod().isEmpty()) {
            app.mFirebaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(event.getMod()).exists()) {
                        txtMod.setVisibility(View.VISIBLE);
                        txtMod.setText("Created by: " + dataSnapshot.child(event.getMod()).child("name").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("MOD", "Couldn't resolve mod username");
                }
            });
        }

        //Show or hide invitedBy if the user was invited to this event (Text set later)
        txtInvitedBy.setVisibility(invited ? View.VISIBLE : View.GONE);

        //Display the event photo if possible
        String photoURL = event.getPhotoURL();
        if(photoURL != null && !photoURL.isEmpty() && URLUtil.isValidUrl(photoURL)) {
            Log.d("URL", "valid");
            Picasso.with(this)
                    .load(photoURL)
                    .into(ivPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("IMAGE", "Successfully loaded PhotoURL");

                        }

                        @Override
                        public void onError() {
                            Log.e("IMAGE", "PhotoURL load failed");
                            ivPhoto.setVisibility(View.GONE);
                        }
            });
        }
        else {
            ivPhoto.setVisibility(View.GONE);
        }

        //Set the Attend Event button to attend or unattend based on Firebase data
        btnAttendEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnAttendEvent.getText().toString().equals("Attend Event")) {
                    app.mFirebaseRef.child("attendance").child(app.user.getId()).child(app.activeEventID).setValue(true, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError == null) btnAttendEvent.setText("Unattend Event");
                        }
                    });
                }
                else if (btnAttendEvent.getText().toString().equals("Unattend Event")) {
                    app.mFirebaseRef.child("attendance").child(app.user.getId()).child(app.activeEventID).removeValue(new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError == null) btnAttendEvent.setText("Attend Event");
                        }
                    });
                }
            }
        });
    }

    /**
     * NAME
     *      EventView.setAttending
     * DESCRIPTION
     *      Finds if the user is listed as attending the event in the Firebase backend.
     *      If the user was, Attend Event button should say "Unattend Event"
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void setAttending() {
        app.mFirebaseRef.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(app.user.getId())) {
                    if (dataSnapshot.child(app.user.getId()).hasChild(app.activeEventID)) {
                        btnAttendEvent.setText("Unattend Event");
                        attending = true;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    /**
     * NAME
     *      EventView.setInvited
     * DESCRIPTION
     *      Finds if the user was invited to this event in the Firebase backend.
     *      If the user was, the "Ignore Invitation" menu option should be shown.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void setInvited() {
        app.mFirebaseRef.child("invites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(app.user.getId())) {
                    if (dataSnapshot.child(app.user.getId()).hasChild(app.activeEventID)) {
                        invited = true;
                        setInvitedBy((String) dataSnapshot.child(app.user.getId()).child(app.activeEventID).getValue());
                        invalidateOptionsMenu();
                    }
                    else {
                        invited = false;
                        txtInvitedBy.setVisibility(View.GONE);
                        txtInvitedBy.setText("Invited By: ");
                        invalidateOptionsMenu();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    /**
     * NAME
     *      EventView.setInvitedBy
     * SYNOPSIS
     *      @param userID - User who invited current user to event (final for use in child method)
     * DESCRIPTION
     *      Resolves the userID to a name in Firebase and sets the Invited By textview to it.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void setInvitedBy(final String userID) {
        app.mFirebaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)) {
                    String name = (String) dataSnapshot.child(userID).child("name").getValue();
                    txtInvitedBy.setVisibility(View.VISIBLE);
                    txtInvitedBy.setText("Invited By: " + name);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    /**
     * NAME
     *      EventView.resolveEventTime
     * SYNOPSIS
     *      @param hour - hour 0-23
     *      @param minute - minute 0-59
     * DESCRIPTION
     *      Takes "military time" data and resolves it to
     *      standard HH:MM AMPM format.
     * RETURNS
     *      @return string - Standard Time formatted time as string
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private String resolveEventTime(int hour, int minute) {
        return (hour % 12 == 0 ? 12 : hour % 12) +
                ":" + (minute < 10 ? "0" + minute: minute)
                + (hour >= 12 ? " PM" : " AM");
    }

    /**
     * NAME
     *      EventView.onCreateOptionsMenu
     * SYNOPSIS
     *      @param menu - The menu in the activity's action bar
     * DESCRIPTION
     *      Ran by system on create activity and invalidateOptionsMenu.
     *      Hides certain options based on the user's relationship with
     *      the event.
     * RETURNS
     *      return boolean - always true, unused
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_view, menu);
        Boolean isCreator = event.getMod().equals(app.user.getId());
        if (!isCreator) {
            //if user isn't this event's creator, hide "Edit Event" and "Delete Event"
            menu.getItem(0).setVisible(false);
            menu.getItem(3).setVisible(false);
        }
        if (event.getClosedInvites() && !isCreator) {
            //if there are closed invites and user is not the creator, hide "Invite"
            menu.getItem(1).setVisible(false);
        }
        if (!invited) {
            //if user is not invited to event, hid "Ignore Invitation"
            menu.getItem(2).setVisible(false);
        }
        return true;
    }

    /**
     * NAME
     *      EventView.onOptionsItemSelected
     * SYNOPSIS
     *      @param item - The option selected in the menu
     * DESCRIPTION
     *      Event for when an item in the three-dot menu is selected.
     *      Handles the appropriate response for each.
     * RETURNS
     *      @return boolean - true if valid item selected
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_edit_event:
                startActivity(new Intent(this, EventEditor.class));
                return true;
            case R.id.action_invite:
                startActivity(new Intent(this, EventInvite.class));
                return true;
            case R.id.action_ignore_invitation:
                if (attending == true) btnAttendEvent.callOnClick();
                app.mFirebaseRef.child("invites").child(app.user.getId()).child(app.activeEventID).removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        setInvited();
                    }
                });
                return true;
            case R.id.action_delete_event:
                app.mFirebaseRef.child("events").child(app.activeEventID).removeValue();
                super.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
