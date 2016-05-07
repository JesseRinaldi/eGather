package com.rinaldi.jesse.egather;


import android.support.v7.app.AppCompatActivity;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class EventEditor extends AppCompatActivity implements OnClickListener {

    private TextView txtDate;
    private Calendar cal;
    private int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);

        Spinner spState = (Spinner) findViewById(R.id.spState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(adapter);

        txtDate = (TextView) findViewById(R.id.txtDate);
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        txtDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        showDialog(0);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case 0:
                return new DatePickerDialog(this, datePickerListener, year, month, day);
            case 1:

                break;
        }
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    };

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            txtDate.setText((selectedMonth + 1) + " / " + selectedDay + " / "
                    + selectedYear);
        }
    };
}
