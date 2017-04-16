package com.group15.apps.carendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by AdamGu0 on 2017/4/9.
 */

public class ShowEventActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStartDatePicker, btnStartTimePicker, btnEndDatePicker, btnEndTimePicker, btnUpdate, btnDelete;
    EditText etStartDate, etStartTime, etEndDate, etEndTime, etLocation, etTitle;
    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private String mTitle, mLocation;
    private Calendar mStartTime,mEndTime;
    private long mStartTimeMills, mEndTimeMills;
    private FirebaseAuth mFirebaseAuth;
    private int mEventType;
    private String mEventKey;
    private String mUserId;
//    private DatabaseReference fb;
    private boolean startDateChanged;
    private boolean endDateChanged;
    private boolean endTimeChanged;
    private boolean startTimeChanged;
    private int oldStartYear, oldStartMonth, oldStartDay, oldStartHour, oldStartMinute,
                oldEndYear, oldEndMonth, oldEndDay, oldEndHour, oldEndMinute;

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        startDateChanged = false;
        endDateChanged = false;
        endTimeChanged = false;
        startTimeChanged = false;

        Intent intent = getIntent();
        mEventKey = intent.getStringExtra("EVENT_KEY");

        mStartTimeMills = intent.getLongExtra("mStartTimeMills",0);
        mEndTimeMills = intent.getLongExtra("mEndTimeMills",0);
        mTitle = intent.getStringExtra("mTitle");
        mLocation = intent.getStringExtra("mLocation");
        mStartTime = Calendar.getInstance();
        mStartTime.setTimeInMillis(mStartTimeMills);
        mEndTime = Calendar.getInstance();
        mEndTime.setTimeInMillis(mEndTimeMills);

        btnStartDatePicker = (Button) findViewById(R.id.btn_start_date);
        btnStartTimePicker = (Button) findViewById(R.id.btn_start_time);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnDelete = (Button) findViewById(R.id.btn_delete);

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

        btnEndDatePicker.setOnClickListener(this);
        btnEndTimePicker.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        etTitle.setText(mTitle);
        etLocation.setText(mLocation);

        etStartDate.setText(mStartTime.get(Calendar.DAY_OF_MONTH) + "-" + (mStartTime.get(Calendar.MONTH) + 1) + "-" + mStartTime.get(Calendar.YEAR));
        etEndDate.setText(mEndTime.get(Calendar.DAY_OF_MONTH) + "-" + (mEndTime.get(Calendar.MONTH) + 1)  + "-" + mEndTime.get(Calendar.YEAR));
        etStartTime.setText(mStartTime.get(Calendar.HOUR_OF_DAY) + ":" + mStartTime.get(Calendar.MINUTE));
        etEndTime.setText(mEndTime.get(Calendar.HOUR_OF_DAY) + ":" + mEndTime.get(Calendar.MINUTE));
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
            startDateChanged = true;
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
            endDateChanged = true;
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
            startTimeChanged = true;
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
            endTimeChanged = true;
        } else if (v == btnUpdate) {
            //TODO update event
            mLocation = etLocation.getText().toString();
            mTitle = etTitle.getText().toString();

            if (startDateChanged) {
//                mStartTime = Calendar.getInstance();
                mStartTime.set(Calendar.YEAR, mStartYear);
                mStartTime.set(Calendar.MONTH, mStartMonth);
                mStartTime.set(Calendar.DAY_OF_MONTH, mStartDay);
            }
//
            if (startTimeChanged) {
                mStartTime.set(Calendar.HOUR_OF_DAY, mStartHour);
                mStartTime.set(Calendar.MINUTE, mStartMinute);
            }

            if (endDateChanged) {
//                mEndTime = Calendar.getInstance();
                mEndTime.set(Calendar.YEAR, mEndYear);
                mEndTime.set(Calendar.MONTH, mEndMonth);
                mEndTime.set(Calendar.DAY_OF_MONTH, mEndDay);
            }

            if (endTimeChanged) {
                mEndTime.set(Calendar.HOUR_OF_DAY, mEndHour);
                mEndTime.set(Calendar.MINUTE, mEndMinute);
            }

            MyWeekViewEvent event = new MyWeekViewEvent(mTitle, mLocation, mStartTime, mEndTime, false, "", mEventKey);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("events").child(mEventKey).setValue(event);
            setResult(Activity.RESULT_OK);
            finish();

        } else if (v == btnDelete) {
            //TODO delete event
            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("events").child(mEventKey).removeValue();
            setResult(Activity.RESULT_OK);
            finish();
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
                Intent returnIntent = new Intent(this, MainActivity.class);
                mLocation = etLocation.getText().toString();
                mTitle = etTitle.getText().toString();

                returnIntent.putExtra("mStartYear", mStartYear);
                returnIntent.putExtra("mStartMonth", mStartMonth);
                returnIntent.putExtra("mStartDay", mStartDay);
                returnIntent.putExtra("mStartHour", mStartHour);
                returnIntent.putExtra("mStartMinute", mStartMinute);

                returnIntent.putExtra("mEndYear", mStartYear);
                returnIntent.putExtra("mEndMonth", mStartMonth);
                returnIntent.putExtra("mEndDay", mStartDay);
                returnIntent.putExtra("mEndHour", mStartHour);
                returnIntent.putExtra("mEndMinute", mEndMinute);

                returnIntent.putExtra("location", mLocation);
                returnIntent.putExtra("title", mTitle);
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

    private void updateValues(Intent intent){
        mStartTimeMills = intent.getLongExtra("mStartTimeMills",0);
        mEndTimeMills = intent.getLongExtra("mEndTimeMills",0);
        mTitle = intent.getStringExtra("mTitle");
        mLocation = intent.getStringExtra("mLocation");
        mStartTime = Calendar.getInstance();
        mStartTime.setTimeInMillis(mStartTimeMills);
        mEndTime = Calendar.getInstance();
        mEndTime.setTimeInMillis(mEndTimeMills);
    }
}
