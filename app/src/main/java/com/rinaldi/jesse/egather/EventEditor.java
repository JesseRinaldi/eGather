package com.rinaldi.jesse.egather;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

/**
 * NAME
 *      EventEditor
 * DESCRIPTION
 *      Activity used to edit an event after it is created. Inherits from AppCompatActivity.
 *      Largely similar to EventCreator.class except edits an event stored in Firebase
 *      instead of creates a new one. Even uses the same XML layout, activity_event_creator.xml
 * AUTHOR
 *      @author Jesse Rinaldi
 * DATE
 *      5/10/2016
 */
public class EventEditor extends AppCompatActivity implements OnClickListener {

    private TextView txtLocation, txtDate, txtTimeStart, txtTimeEnd, activeTxt;
    private EditText txtName, txtWebsiteLink, txtWebsiteTitle, txtDescription, txtTags, txtPhotoURL;
    private Spinner spCategory;
    private RadioButton rbtnPublic, rbtnInviteOnly, rbtnOpen, rbtnOwnerOnly;
    private Button btnCreateEvent;
    private Calendar date, startTime, endTime, activeTime;
    private int month=-1, day=-1, year=-1, startHour=-1, startMinute=-1, endHour=-1, endMinute=-1;
    private Place location;
    private String locationName, locationID, locationAddress;
    private double latitude, longitude;
    private int PLACE_PICKER_REQUEST = 1;
    private AndroidApplication app;

    /**
     * NAME
     *      EventEditor.onCreate
     * SYNOPSIS
     *      @param savedInstanceState - Used by Android to restore Activity to previous state
     * DESCRIPTION
     *      On create event for EventEditor activity. Handles the initial setup of widgets like
     *      setting listeners and populating the category spinner.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creator);

        setTitle("Edit Event");
        bindWidgets();

        txtDate.setOnClickListener(this);
        txtTimeStart.setOnClickListener(this);
        txtTimeEnd.setOnClickListener(this);
        txtLocation.setOnClickListener(this);
        btnCreateEvent.setOnClickListener(this);
        txtName.addTextChangedListener(this.textWatcher);
        txtLocation.addTextChangedListener(this.textWatcher);
        txtDate.addTextChangedListener(this.textWatcher);
        btnCreateEvent.setEnabled(false);

        date = Calendar.getInstance();
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        app = (AndroidApplication) getApplicationContext();
        setWidgetsToActiveEvent();
    }

    /**
     * NAME
     *      EventEditor.bindWidgets
     * DESCRIPTION
     *      Binds each xml widget to a variable
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void bindWidgets() {
        txtName = (EditText) findViewById(R.id.txtName);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtWebsiteLink = (EditText) findViewById(R.id.txtWebsiteLink);
        txtWebsiteTitle = (EditText) findViewById(R.id.txtWebsiteTitle);
	    txtPhotoURL = (EditText) findViewById(R.id.txtPhotoURL);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtTags = (EditText) findViewById(R.id.txtTags);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTimeStart = (TextView) findViewById(R.id.txtTimeStart);
        txtTimeEnd = (TextView) findViewById(R.id.txtTimeEnd);
        btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);
        spCategory = (Spinner) findViewById(R.id.spCategory);
        rbtnPublic = (RadioButton) findViewById(R.id.rbtnPublic);
        rbtnInviteOnly = (RadioButton) findViewById(R.id.rbtnInviteOnly);
        rbtnOpen = (RadioButton) findViewById(R.id.rbtnOpen);
        rbtnOwnerOnly = (RadioButton) findViewById(R.id.rbtnOwnerOnly);
    }

    /**
     * NAME
     *      EventEditor.setWidgetsToActiveEvent
     * DESCRIPTION
     *      Before the event is edited, its previous data must be displayed in the
     *      widgets. Handled here.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void setWidgetsToActiveEvent() {

        Event event = app.activeEvent;
        txtName.setText(event.getName());
        txtLocation.setText(event.getLocationAddress());
        locationID = event.getLocationId();
        locationAddress = event.getLocationAddress();
        locationName = event.getLocationName();
        latitude = event.getLatitude();
        longitude = event.getLongitude();
        txtWebsiteLink.setText(event.getWebsite());
        txtWebsiteTitle.setText(event.getWebsiteTitle());
        txtDescription.setText(event.getBody());
        txtTags.setText(event.getTags());

        ArrayAdapter<CharSequence> categories = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        spCategory.setSelection(categories.getPosition(event.getCategory()));

        if (event.getInviteOnly()) {
            rbtnPublic.setChecked(false);
            rbtnInviteOnly.setChecked(true);
        }
        if (event.getClosedInvites()) {
            rbtnOpen.setChecked(false);
            rbtnOwnerOnly.setChecked(true);
        }

        month = event.getMonth();
        day = event.getDay();
        year = event.getYear();
        txtDate.setText(month + " / " + day + " / " + year);
        date.set(year, month-1, day);
        startHour = event.getStartHour();
        startMinute = event.getStartMinute();
        endHour = event.getEndHour();
        endMinute = event.getEndMinute();
        if (startHour != -1) {
            startTime.set(Calendar.HOUR, startHour);
            startTime.set(Calendar.MINUTE, startMinute);
            txtTimeStart.setText((startHour % 12 == 0 ? 12 : startHour % 12) + ":" + (startMinute < 10 ? "0" + startMinute : startMinute) + (startHour >= 12 ? " PM" : " AM"));
        }
        if (endHour != -1) {
            endTime.set(Calendar.HOUR, endHour);
            endTime.set(Calendar.MINUTE, endMinute);
            txtTimeEnd.setText((endHour % 12 == 0 ? 12 : endHour % 12) + ":" + (endMinute < 10 ? "0" + endMinute : endMinute) + (endHour >= 12 ? " PM" : " AM"));
        }
        btnCreateEvent.setText("Edit Event");
    }

    /**
     * NAME
     *      EventEditor.onClick
     * SYNOPSIS
     *      @param v - The view that was clicked
     * DESCRIPTION
     *      Handles the click events for various widgets.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnCreateEvent:
                editEvent();
                break;
            case R.id.txtLocation:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.txtDate:
                activeTxt = txtDate;
                activeTime = date;
                showDialog(0);
                break;
            case R.id.txtTimeStart:
                activeTxt = txtTimeStart;
                activeTime = startTime;
                showDialog(1);
                break;
            case R.id.txtTimeEnd:
                activeTxt = txtTimeEnd;
                activeTime = endTime;
                showDialog(1);
                break;
        }
    }

    /**
     * NAME
     *      EventEditor.textWatcher
     * DESCRIPTION
     *      TextWatcher object used to enable/disable the Edit Event button
     *      based on if name, location, and date are filled out.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private TextWatcher textWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void afterTextChanged(Editable s) {
            btnCreateEvent.setEnabled(txtName.getText().toString().trim().length() > 0 &&
                    txtDate.getText().toString().trim().length() > 0 &&
                    txtLocation.getText().toString().trim().length() > 0);
        }
    };

    /**
     * NAME
     *      EventEditor.editEvent
     * DESCRIPTION
     *      Resends the current event to Firebase with the new data.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    protected void editEvent() {
        if (txtName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Error: Name Required", Toast.LENGTH_LONG);
            return;
        }

        if (location != null) {
            locationAddress = location.getAddress().toString();
            locationID = location.getId().toString();
            locationName = location.getName().toString();
            latitude = location.getLatLng().latitude;
            longitude = location.getLatLng().longitude;
        }

        app.activeEvent.setName(txtName.getText().toString())
                .setLocationId(locationID)
                .setLocationAddress(locationAddress)
                .setLocationName(locationName)
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setDate(month, day, year)
                .setTime(startHour, startMinute, endHour, endMinute)
                .setWebsite(txtWebsiteLink.getText().toString().trim())
                .setWebsiteTitle(txtWebsiteTitle.getText().toString().trim())
		.setPhotoURL(txtPhotoURL.getText().toString().trim())
                .setBody(txtDescription.getText().toString().trim())
                .setCategory(spCategory.getSelectedItem().toString())
                .setTags(txtTags.getText().toString())
                .setInviteOnly(!rbtnPublic.isChecked())
                .setClosedInvites(!rbtnOpen.isChecked())
                .setMod(app.user.getId());
        Firebase newRef = app.mFirebaseRef.child("events").child(app.activeEventID);
        newRef.setValue(app.activeEvent);
        finish();
    }

    /**
     * NAME
     *      EventEditor.onActivityResult
     * SYNOPSIS
     *      @param requestCode
     *      @param resultCode
     *      @param data
     * DESCRIPTION
     *      Handles the result from the PlacePicker fragment activity.
     *      Sets the location data.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("PlacePicker", "Activity Result");
        if (requestCode == PLACE_PICKER_REQUEST){
            Log.d("PlacePicker", "requestCode == PLACE_PICKER_REQUEST");
            if (resultCode == RESULT_OK) {
                location = PlacePicker.getPlace(this, data);
                Log.d("PlacePicker", location.getAddress().toString());
                txtLocation.setText(location.getAddress().toString());
            }
            else {
                Log.e("PlacePicker Error", "resultCode = " + resultCode);
            }
        }
    }

    /**
     * NAME
     *      EventEditor.onCreateDialog
     * SYNOPSIS
     *      @param id - 0 or 1 for which dialog to open
     * DESCRIPTION
     *      Opens either a DatePicker or TimePicker dialog
     * RETURNS
     *      @return Dialog - The dialog which was created. Null if invalid id.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case 0:
                return new DatePickerDialog(this, datePickerListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            case 1:
                int hour, minute;
                hour = (txtTimeEnd.getText().length() > 0 ? activeTime.get(Calendar.HOUR_OF_DAY) : startTime.get(Calendar.HOUR_OF_DAY));
                minute = (txtTimeEnd.getText().length() > 0 ? activeTime.get(Calendar.MINUTE) : startTime.get(Calendar.MINUTE));
                return new TimePickerDialog(this, timePickerListener, hour, minute, false );
        }
        return null;
    };

    /**
     * NAME
     *      EventEditor.datePickerListener
     * DESCRIPTION
     *      datePickerListener object. Sets the month, day, and year after selection in the class
     *      and widgets.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            activeTxt.setText((selectedMonth + 1) + " / " + selectedDay + " / " + selectedYear);
            month = selectedMonth+1;
            day = selectedDay;
            year = selectedYear;
        }
    };

    /**
     * NAME
     *      EventEditor.timePickerListener
     * DESCRIPTION
     *      timePickerListener object. Sets the start and end times after selection in the class
     *      and widgets
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour,
                              int selectedMinute) {
            if (activeTxt.getId() == R.id.txtTimeStart) {
                startHour = selectedHour;
                startMinute = selectedMinute;
            }
            else if (activeTxt.getId() == R.id.txtTimeEnd) {
                endHour = selectedHour;
                endMinute = selectedMinute;
            }
            activeTime.set(Calendar.HOUR_OF_DAY, selectedHour);
            activeTime.set(Calendar.MINUTE, selectedMinute);
            String AMPM = selectedHour >= 12 ? " PM" : " AM";
            selectedHour = selectedHour % 12 == 0 ? 12 : selectedHour % 12;
            activeTxt.setText(selectedHour + ":" + (selectedMinute<10?"0"+selectedMinute:selectedMinute) + AMPM);
        }
    };
}
