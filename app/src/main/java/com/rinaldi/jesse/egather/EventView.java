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


public class EventView extends AppCompatActivity {

    private TextView txtName, txtLocation, txtDateTime, txtDescription, txtWebsite, txtCategory, txtMod, txtInvitedBy;
    private ImageView ivPhoto;
    private Button btnAttendEvent;
    private boolean attending = false, invited = false;
    private Event event;
    AndroidApplication app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        app = (AndroidApplication) getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        event = app.activeEvent;
        bindWidgets();
        setWidgets();
        setAttending();
        setInvited();
    }

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

    private void setWidgets() {

        String name = event.getName();
        txtName.setText(name);
        setTitle(name);


        if (!event.getLocationAddress().contains(event.getLocationName())) {
            txtLocation.setText(event.getLocationName() + ", " + event.getLocationAddress());
        }
        else {
            txtLocation.setText(event.getLocationAddress());
        }


        StringBuilder dateTime = new StringBuilder(event.getMonth()  + " / " + event.getDay() + " / " + event.getYear());
        if (event.getStartHour() == -1) {}
        else if (event.getEndHour() == -1) {
            dateTime.append(" at " + resolveEventTime(event.getStartHour(), event.getStartMinute()));
        }
        else {
            dateTime.append(" from " + resolveEventTime(event.getStartHour(), event.getStartMinute()) + " to " + resolveEventTime(event.getEndHour(), event.getEndMinute()));
        }
        txtDateTime.setText(dateTime.toString());

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

        if (!event.getBody().isEmpty()) txtDescription.setText(event.getBody());
        else txtDescription.setVisibility(View.GONE);
        txtCategory.setText("Category: " + event.getCategory());

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

        txtInvitedBy.setVisibility(View.GONE);

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

    private void setInvited() {
        app.mFirebaseRef.child("invites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(app.user.getId())) {
                    if (dataSnapshot.child(app.user.getId()).hasChild(app.activeEventID)) {
                        invited = true;
                        setInvitedBy((String) dataSnapshot.child(app.user.getId()).child(app.activeEventID).getValue());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

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

    private String resolveEventTime(int hour, int minute) {
        return (hour % 12 == 0 ? 12 : hour % 12) +
                ":" + (minute < 10 ? "0" + minute: minute)
                + (hour >= 12 ? " PM" : " AM");
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_edit_event:
                Intent intent = new Intent(this, EventEditor.class);
                startActivity(intent);
                return true;
            case R.id.action_invite:
                return true;
            case R.id.action_ignore_invitation:
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
