package com.rinaldi.jesse.egather;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

public class EventView extends Activity {

    private TextView txtName, txtLocation, txtDateTime, txtDescription, txtWebsite, txtCategory;
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
        btnAttendEvent = (Button) findViewById(R.id.btnCreateEvent);
    }

    private void setWidgets() {

        txtName.setText(event.getName());


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
    }

    private String resolveEventTime(int hour, int minute) {
        return (hour % 12 == 0 ? 12 : hour % 12) +
                ":" + (minute < 10 ? "0" + minute: minute)
                + (hour >= 12 ? " PM" : " AM");
    }
}
