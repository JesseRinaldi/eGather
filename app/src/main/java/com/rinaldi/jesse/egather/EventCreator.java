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
import com.google.android.gms.location.places.ui.PlacePicker;


public class EventCreator extends AppCompatActivity implements OnClickListener {

    private TextView txtLocation, txtDate, txtTimeStart, txtTimeEnd, activeTxt;
    private EditText txtName, txtWebsiteLink, txtWebsiteTitle, txtDescription, txtTags;
    private Spinner spCategory;
    private RadioButton rbtnPublic, rbtnOpen;
    private Button btnCreateEvent;
    private Calendar date, startTime, endTime, activeTime;
    private int month=-1, day=-1, year=-1, startHour=-1, startMinute=-1, endHour=-1, endMinute=-1;
    private Place location;
    private int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creator);

        setTitle("New Event");
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
        spCategory.setSelection(adapter.getPosition("Other"));
    }

    private void bindWidgets() {
        txtName = (EditText) findViewById(R.id.txtName);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtWebsiteLink = (EditText) findViewById(R.id.txtWebsiteLink);
        txtWebsiteTitle = (EditText) findViewById(R.id.txtWebsiteTitle);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtTags = (EditText) findViewById(R.id.txtTags);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTimeStart = (TextView) findViewById(R.id.txtTimeStart);
        txtTimeEnd = (TextView) findViewById(R.id.txtTimeEnd);
        btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);
        spCategory = (Spinner) findViewById(R.id.spCategory);
        rbtnPublic = (RadioButton) findViewById(R.id.rbtnPublic);
        rbtnOpen = (RadioButton) findViewById(R.id.rbtnOpen);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnCreateEvent:
                createEvent();
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

    private TextWatcher textWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void afterTextChanged(Editable s) {
            btnCreateEvent.setEnabled(txtName.getText().toString().trim().length() > 0 &&
                    txtDate.getText().toString().trim().length() > 0 &&
                    txtLocation.getText().toString().trim().length() > 0);
        }
    };

    protected void createEvent() {
        if (txtName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Error: Name Required", Toast.LENGTH_LONG);
            return;
        }

        AndroidApplication app = (AndroidApplication) getApplicationContext();
        Event event = new Event(txtName.getText().toString())
                .setLocation(location)
                .setDate(month, day, year)
                .setTime(startHour, startMinute, endHour, endMinute)
                .setWebsite(txtWebsiteLink.getText().toString().trim())
                .setWebsiteTitle(txtWebsiteTitle.getText().toString().trim())
                .setBody(txtDescription.getText().toString().trim())
                .setCategory(spCategory.getSelectedItem().toString())
               // .setTags(txtTags.getText().toString())
                .setInviteOnly(!rbtnPublic.isChecked())
                .setClosedInvites(!rbtnOpen.isChecked())
                .setMod(app.user.getId());
        Firebase newRef = app.mFirebaseRef.child("events").push();
        newRef.setValue(event);
        app.activeEvent = event;
        Intent i = new Intent(this, EventView.class);
        finish();  //Kill the activity from which you will go to next activity
        startActivity(i);
    }

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

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case 0:
                return new DatePickerDialog(this, datePickerListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            case 1:
                return new TimePickerDialog(this, timePickerListener, startTime.get(Calendar.HOUR), startTime.get(Calendar.MINUTE), false );
        }
        return null;
    };

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            activeTxt.setText((selectedMonth + 1) + " / " + selectedDay + " / " + selectedYear);
            month = selectedMonth+1;
            day = selectedDay;
            year = selectedYear;
        }
    };

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
            activeTime.set(Calendar.HOUR, selectedHour);
            activeTime.set(Calendar.MINUTE, selectedMinute);
            String AMPM = selectedHour >= 12 ? " PM" : " AM";
            selectedHour = selectedHour % 12 == 0 ? 12 : selectedHour % 12;
            activeTxt.setText(selectedHour + ":" + (selectedMinute<10?"0"+selectedMinute:selectedMinute) + AMPM);
        }
    };
}
