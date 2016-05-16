package com.rinaldi.jesse.egather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class EventView extends AppCompatActivity {

    private TextView txtName, txtLocation, txtDateTime, txtDescription, txtWebsite, txtCategory, txtMod;
    private ImageView ivPhoto;
    private Button btnAttendEvent;
    private Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        event = ((AndroidApplication) getApplicationContext()).activeEvent;
        bindWidgets();
        setWidgets();
    }

    private void bindWidgets() {
        txtName = (TextView) findViewById(R.id.txtNameView);
        txtLocation = (TextView) findViewById(R.id.txtLocationView);
        txtDateTime = (TextView) findViewById(R.id.txtDateTimeView);
        txtWebsite = (TextView) findViewById(R.id.txtWebsiteView);
        txtDescription = (TextView) findViewById(R.id.txtDescriptionView);
        txtCategory = (TextView) findViewById(R.id.txtCategoryView);
        txtMod = (TextView) findViewById(R.id.txtCreatedByView);
        ivPhoto = (ImageView) findViewById(R.id.ivPhotoView);
        btnAttendEvent = (Button) findViewById(R.id.btnCreateEvent);
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
            AndroidApplication app = (AndroidApplication) getApplicationContext();
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
    }

    private String resolveEventTime(int hour, int minute) {
        return (hour % 12 == 0 ? 12 : hour % 12) +
                ":" + (minute < 10 ? "0" + minute: minute)
                + (hour >= 12 ? " PM" : " AM");
    }
}
