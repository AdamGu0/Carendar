package com.group15.apps.carendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by AdamGu0 on 2017/4/9.
 */

public class ShowEventActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_CODE_SELECT_LOCATION = 1;
    private Button btnStartDatePicker, btnStartTimePicker, btnEndDatePicker, btnEndTimePicker, btnUpdate, btnDelete;
    private EditText etStartDate, etStartTime, etEndDate, etEndTime, etLocation, etTitle, etGroupName;
    private TextView tv_group_name;
    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private String mTitle, mLocation;
    private Calendar mStartTime,mEndTime;
    private long mStartTimeMills, mEndTimeMills;
    private FirebaseAuth mFirebaseAuth;
    private int mEventType;
    private boolean mIsGroupEvent;
    private String mEventKey, mGroupName;
    private double mLongitude,mLatitude;
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
        mEventType = intent.getIntExtra("type",0);
        mIsGroupEvent =intent.getBooleanExtra("isGroupEvent", false);
        mGroupName = intent.getStringExtra("groupName");
        mLongitude = intent.getDoubleExtra("longitude", 0.0);
        mLatitude = intent.getDoubleExtra("latitude", 0.0);

        btnStartDatePicker = (Button) findViewById(R.id.btn_start_date_show);
        btnStartTimePicker = (Button) findViewById(R.id.btn_start_time_show);
        btnUpdate = (Button) findViewById(R.id.btn_update_show);
        btnDelete = (Button) findViewById(R.id.btn_delete_show);
        etStartDate = (EditText) findViewById(R.id.in_start_date_show);
        etStartTime = (EditText) findViewById(R.id.in_start_time_show);
        etTitle = (EditText) findViewById(R.id.et_title_show);
        etLocation = (EditText) findViewById(R.id.et_location_show);
        btnEndDatePicker = (Button) findViewById(R.id.btn_end_date_show);
        btnEndTimePicker = (Button) findViewById(R.id.btn_end_time_show);
        etEndDate = (EditText) findViewById(R.id.in_end_date_show);
        etEndTime = (EditText) findViewById(R.id.in_end_time_show);
        etGroupName = (EditText) findViewById(R.id.et_group_name_show);
        tv_group_name = (TextView) findViewById(R.id.tv_group_name_show);

        Button btnSelectLoc = (Button) findViewById(R.id.btn_select_location_show);
        btnSelectLoc.setOnClickListener(this);
        btnStartDatePicker.setOnClickListener(this);
        btnStartTimePicker.setOnClickListener(this);
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
        etGroupName.setText(mGroupName);

        setupSpinner();
        int id;
        if (mIsGroupEvent) id = R.id.radio_group_show;
        else id = R.id.radio_personal_show;
        RadioButton r = (RadioButton) findViewById(id);
        r.setChecked(true);
        onRadioButtonClicked(r);
    }

    private void setupSpinner(){
        Spinner spinner = (Spinner)findViewById(R.id.sp_event_type_show);
        String[] types = EventType.TYPES;
        MySpinnerAdapter spinnerAdapter = new MySpinnerAdapter(this, types);
        spinner.setAdapter(spinnerAdapter);

        spinner.setSelection(mEventType, false);

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
                mStartTime.set(Calendar.YEAR, mStartYear);
                mStartTime.set(Calendar.MONTH, mStartMonth);
                mStartTime.set(Calendar.DAY_OF_MONTH, mStartDay);
            }

            if (startTimeChanged) {
                mStartTime.set(Calendar.HOUR_OF_DAY, mStartHour);
                mStartTime.set(Calendar.MINUTE, mStartMinute);
            }

            if (endDateChanged) {
                mEndTime.set(Calendar.YEAR, mEndYear);
                mEndTime.set(Calendar.MONTH, mEndMonth);
                mEndTime.set(Calendar.DAY_OF_MONTH, mEndDay);
            }

            if (endTimeChanged) {
                mEndTime.set(Calendar.HOUR_OF_DAY, mEndHour);
                mEndTime.set(Calendar.MINUTE, mEndMinute);
            }

            mGroupName = etGroupName.getText().toString();
            MyWeekViewEvent event = new MyWeekViewEvent(mTitle, mLocation, mStartTime, mEndTime, mIsGroupEvent, mGroupName, mEventKey);
            event.setEventType(mEventType);
            event.setLongitude(mLongitude);
            event.setLatitude(mLatitude);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("events").child(mEventKey).setValue(event);
            setResult(Activity.RESULT_OK);
            finish();

        } else if (v == btnDelete) {
            //TODO delete event
            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("events").child(mEventKey).removeValue();
            setResult(Activity.RESULT_OK);
            finish();
        } else if(v.getId() == R.id.btn_select_location_show){
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
        switch (view.getId()) {
            case R.id.radio_group_show:
                etGroupName.setVisibility(View.VISIBLE);
                tv_group_name.setVisibility(View.VISIBLE);
                mIsGroupEvent = true;
                break;
            case R.id.radio_personal_show:
                etGroupName.setVisibility(View.INVISIBLE);
                tv_group_name.setVisibility(View.INVISIBLE);
                mIsGroupEvent = false;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_event_menu, menu);
        return true;
    }
}
