package com.rinaldi.jesse.egather;


import android.support.v7.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Date;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
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


public class EventEditor extends AppCompatActivity implements OnClickListener {

    private TextView txtDate, txtTimeStart, txtTimeEnd, activeTxt;
    private EditText txtName, txtAddress, txtCity, txtZip, txtWebsiteLink, txtWebsiteTitle, txtDescription, txtTags;
    private Spinner spState;
    private RadioButton rbtnPublic, rbtnOpen;
    private Button btnCreateEvent;
    private Calendar date, startTime, endTime, activeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);

        setTitle("New Event");
        bindWidgets();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(adapter);

        txtDate.setOnClickListener(this);
        txtTimeStart.setOnClickListener(this);
        txtTimeEnd.setOnClickListener(this);
        btnCreateEvent.setOnClickListener(this);

        date = Calendar.getInstance();
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
    }

    private void bindWidgets() {
        txtName = (EditText) findViewById(R.id.txtName);
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        txtCity = (EditText) findViewById(R.id.txtCity);
        txtZip = (EditText) findViewById(R.id.txtZip);
        txtWebsiteLink = (EditText) findViewById(R.id.txtWebsiteLink);
        txtWebsiteTitle = (EditText) findViewById(R.id.txtWebsiteTitle);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtTags = (EditText) findViewById(R.id.txtTags);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTimeStart = (TextView) findViewById(R.id.txtTimeStart);
        txtTimeEnd = (TextView) findViewById(R.id.txtTimeEnd);
        btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);
        spState = (Spinner) findViewById(R.id.spState);
        rbtnPublic = (RadioButton) findViewById(R.id.rbtnPublic);
        rbtnOpen = (RadioButton) findViewById(R.id.rbtnOpen);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnCreateEvent:
                createEvent();
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

    protected void createEvent() {
        if (txtName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Error: Name Required", Toast.LENGTH_LONG);
            return;
        }
        Event event = new Event();
        event.setName(txtName.getText().toString())
                .setAddress(txtAddress.getText().toString().trim())
                .setCity(txtCity.getText().toString().trim())
                .setState(spState.getSelectedItem().toString())
                .setZip(txtZip.getText().toString().trim())
                .setWebsite(txtWebsiteLink.getText().toString().trim())
                .setBody(txtDescription.getText().toString().trim());
        AndroidApplication app = (AndroidApplication) getApplicationContext();
        app.mFirebaseRef.child("events").push().setValue(event);

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
            activeTxt.setText((selectedMonth + 1) + " / " + selectedDay + " / "
                    + selectedYear);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour,
                              int selectedMinute) {
            activeTime.set(Calendar.HOUR, selectedHour);
            activeTime.set(Calendar.MINUTE, selectedMinute);
            String AMPM = selectedHour >= 12 ? " PM" : " AM";
            selectedHour = selectedHour % 12 == 0 ? 12 : selectedHour % 12;
            activeTxt.setText(selectedHour + ":" + (selectedMinute<10?"0"+selectedMinute:selectedMinute) + AMPM);
        }
    };
}
