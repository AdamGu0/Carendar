package com.group15.apps.carendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Neo on 3/11/17.
 */

public class AddEventActivity extends AppCompatActivity implements
        View.OnClickListener {

    private final static int REQUEST_CODE_SELECT_LOCATION = 1;
    Button btnStartDatePicker, btnStartTimePicker, btnEndDatePicker, btnEndTimePicker, btnIsGroupEvent, btnSave;
    EditText etStartDate, etStartTime, etEndDate, etEndTime, etLocation, etTitle, etGroupName;
    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private String mTitle, mLocation, mGroupName;
    private boolean mIsGroupEvent;
    private TextView tv_group_name;
    private int mEventType;
    private double mLongitude;
    private double mLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        btnStartDatePicker = (Button) findViewById(R.id.btn_start_date);
        btnStartTimePicker = (Button) findViewById(R.id.btn_start_time);
        btnSave = (Button) findViewById(R.id.btn_save);

        etStartDate = (EditText) findViewById(R.id.in_start_date);
        etStartTime = (EditText) findViewById(R.id.in_start_time);
        etTitle = (EditText) findViewById(R.id.et_title);
        etLocation = (EditText) findViewById(R.id.et_location);

        btnStartDatePicker.setOnClickListener(this);
        btnStartTimePicker.setOnClickListener(this);

        btnEndDatePicker = (Button) findViewById(R.id.btn_end_date);
        btnEndTimePicker = (Button) findViewById(R.id.btn_end_time);
        etEndDate = (EditText) findViewById(R.id.in_end_date);
        etEndTime = (EditText) findViewById(R.id.in_end_time);

        btnIsGroupEvent = (Button) findViewById(R.id.btn_group_event);
        etGroupName = (EditText) findViewById(R.id.et_group_name);

        tv_group_name = (TextView) findViewById(R.id.tv_group_name);

        btnEndDatePicker.setOnClickListener(this);
        btnEndTimePicker.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        Button btnSelectLoc = (Button) findViewById(R.id.btn_select_location);
        btnSelectLoc.setOnClickListener(this);
        setupSpinner();
    }

    private void setupSpinner(){
        Spinner spinner = (Spinner)findViewById(R.id.sp_event_type);
        String[] types = EventType.TYPES;
        MySpinnerAdapter spinnerAdapter = new MySpinnerAdapter(this, types);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // eg. 0 - Class, 1 - Meeting ...
                mEventType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == btnStartDatePicker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mStartYear = year;
                            mStartMonth = monthOfYear;
                            mStartDay = dayOfMonth;
                            etStartDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        } else if (v == btnEndDatePicker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mEndYear = year;
                            mEndMonth = monthOfYear;
                            mEndDay = dayOfMonth;
                            etEndDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        } else if (v == btnStartTimePicker) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mStartHour = hourOfDay;
                            mStartMinute = minute;
                            etStartTime.setText(hourOfDay + ":" + minute);
                        }
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        } else if (v == btnEndTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mEndHour = hourOfDay;
                            mEndMinute = minute;
                            etEndTime.setText(hourOfDay + ":" + minute);
                        }
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        } else if (v == btnSave) {
            Intent returnIntent = new Intent(AddEventActivity.this, MainActivity.class);
            mLocation = etLocation.getText().toString();
            mTitle = etTitle.getText().toString();
            mGroupName = etGroupName.getText().toString();

            putValues(returnIntent);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();

        } else if(v.getId() == R.id.btn_select_location){
            onSelectLocationClicked();
        }
    }

    private void onSelectLocationClicked(){
        Intent intent = new Intent(this, MapLocationActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SELECT_LOCATION){
            if(resultCode == Activity.RESULT_OK && data != null){
                mLongitude = data.getDoubleExtra("longitude", 0.0);
                mLatitude = data.getDoubleExtra("latitude", 0.0);
                String location = data.getStringExtra("name");
                if(etLocation != null){
                    etLocation.setText(location);
                }
            }else{
                // error
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_group:
                if (checked) {
                    etGroupName.setVisibility(View.VISIBLE);
                    tv_group_name.setVisibility(View.VISIBLE);
                    mIsGroupEvent = true;
                }
                break;
            case R.id.radio_personal:
                if (checked) {
                    etGroupName.setVisibility(View.INVISIBLE);
                    tv_group_name.setVisibility(View.INVISIBLE);
                    mIsGroupEvent = false;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:

                // User chose the "Settings" item, show the app settings UI...
                Intent returnIntent = new Intent(AddEventActivity.this, MainActivity.class);
                mLocation = etLocation.getText().toString();
                mTitle = etTitle.getText().toString();

                putValues(returnIntent);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void putValues(Intent intent){
        intent.putExtra("mStartYear", mStartYear);
        intent.putExtra("mStartMonth", mStartMonth);
        intent.putExtra("mStartDay", mStartDay);
        intent.putExtra("mStartHour", mStartHour);
        intent.putExtra("mStartMinute", mStartMinute);

        intent.putExtra("mEndYear", mEndYear);
        intent.putExtra("mEndMonth", mEndMonth);
        intent.putExtra("mEndDay", mEndDay);
        intent.putExtra("mEndHour", mEndHour);
        intent.putExtra("mEndMinute", mEndMinute);

        intent.putExtra("location", mLocation);
        intent.putExtra("title", mTitle);
        intent.putExtra("type", mEventType);
        intent.putExtra("isGroupEvent", mIsGroupEvent);
        intent.putExtra("groupName", mGroupName);
    }
}
