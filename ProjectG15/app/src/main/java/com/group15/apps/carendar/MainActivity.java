package com.group15.apps.carendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader.MonthChangeListener;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements MonthChangeListener {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseMessageReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    static final int RC_ADD_EVENT = 2;
    private WeekView mWeekView;
    private ArrayList<WeekViewEvent> mNewEvents;
    private MyWeekViewEvent myWeekViewEvent;
//    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private ImageButton mFloatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "You're are now signed in. Welcome to FriendlyChat.", Toast.LENGTH_SHORT).show();
                } else {
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(AuthUI.EMAIL_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER)
                            .build(), RC_SIGN_IN);
                }
            }
        };

        mFloatingActionButton = (ImageButton) findViewById(R.id.fab_add);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                startActivityForResult(intent, RC_ADD_EVENT);

            }
        });

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
//        mWeekView.setOnEventClickListener(mEventClickListener);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);
        mNewEvents = new ArrayList<WeekViewEvent>();


        // Set long press listener for events.
//        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
//        mWeekView.setEmptyViewLongPressListener(this);
        setupDateTimeInterpreter(true);

        mWeekView.setNumberOfVisibleDays(7);
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Sign in!", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        if (requestCode == RC_ADD_EVENT) {
            if (resultCode == RESULT_OK) {
                String location = data.getStringExtra("location");
                String title = data.getStringExtra("title");
//                int[] startAndDateTime = data.getIntArrayExtra("startDateAndTime");
//                int[] endDateAndTime = data.getIntArrayExtra("endDateAndTime");
//                Bundle bundle = this.getIntent().getExtras();
//                int[] startAndDateTime = bundle.getIntArray("startDateAndTime");
//                int[] endDateAndTime= bundle.getIntArray("endDateAndTime");

                int mStartYear = data.getIntExtra("mStartYear", 0);
                int mStartMonth = data.getIntExtra("mStartMonth", 0);
                int mStartDay = data.getIntExtra("mStartDay", 0);
                int mStartHour = data.getIntExtra("mStartHour", 0);
                int mStartMinute = data.getIntExtra("mEndMinute", 0);
//

                int mEndYear = data.getIntExtra("mEndYear", 0);
                int mEndMonth = data.getIntExtra("mEndMonth", 0);
                int mEndDay = data.getIntExtra("mEndDay", 0);
                int mEndHour = data.getIntExtra("mEndHour", 0);
                int mEndMinute = data.getIntExtra("mEndMinute", 0);

//                Calendar startTime = Calendar.getInstance();
//                startTime.set(Calendar.YEAR, 2017);
//                startTime.set(Calendar.MONTH, 3);
//                startTime.set(Calendar.DATE, 13);
//                startTime.set(Calendar.HOUR_OF_DAY, 2);
//                startTime.set(Calendar.MINUTE, 1);
////
//                Calendar endTime = (Calendar) startTime.clone();
//                endTime.set(Calendar.YEAR, 2018);
//                endTime.set(Calendar.MONTH, 3);
//                endTime.set(Calendar.DATE, 13);
//                endTime.set(Calendar.HOUR_OF_DAY, 4);
//                endTime.set(Calendar.MINUTE, 3);
//                WeekViewEvent event = new MyWeekViewEvent("1", "kkk", startTime, endTime);
//                event.setColor(getResources().getColor(R.color.event_color_01));

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, 3 - 1);
            startTime.set(Calendar.YEAR, 2017);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, 4);
            endTime.set(Calendar.MONTH, 3 - 1);
            WeekViewEvent event = new MyWeekViewEvent("1", "kkk", startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_01));

//                Calendar startTime = Calendar.getInstance();
//
//                startTime.set(data.getIntExtra("mStartYear", 0), data.getIntExtra("mStartMonth", 0),
//                        data.getIntExtra("mStartDay", 0), data.getIntExtra("mStartHour", 0),
//                        data.getIntExtra("mEndMinute", 0));
//
//                Calendar endTime = Calendar.getInstance();
//                endTime.set(data.getIntExtra("mEndYear", 0), data.getIntExtra("mEndMonth", 0), data.getIntExtra("mEndDay", 0),
//                        data.getIntExtra("mEndHour", 0), data.getIntExtra("mEndMinute", 0));
//                WeekViewEvent event = new MyWeekViewEvent(title, location, startTime, endTime);
//                event.setColor(getResources().getColor(R.color.event_color_01));
                mNewEvents.add(event);
                // Refresh the week view. onMonthChange will be called again.
                mWeekView.notifyDatasetChanged();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.sign_out_menu:
                    AuthUI.getInstance().signOut(this);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }

        }
        return true;
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

//    WeekView.EventClickListener mEventClickListener = new WeekView.EventClickListener () {
//        @Override
//        public void onEventClick(WeekViewEvent event, RectF eventRect) {
//            MyWeekViewEvent myWeekViewEvent = (MyWeekViewEvent) event;
//            String s = myWeekViewEvent.getTitle();
//
//            Toast.makeText(MainActivity.class, "Clicked " + s, Toast.LENGTH_SHORT).show();
//        }
//    };




//    MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener() {
//        @Override
        public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
            // Populate the week view with some events.
            List<WeekViewEvent> events = new ArrayList<>();

//            Calendar startTime = Calendar.getInstance();
//            startTime.set(Calendar.HOUR_OF_DAY, 3);
//            startTime.set(Calendar.MINUTE, 0);
//            startTime.set(Calendar.MONTH, newMonth - 1);
//            startTime.set(Calendar.YEAR, newYear);
//            Calendar endTime = (Calendar) startTime.clone();
//            endTime.add(Calendar.HOUR, 1);
//            endTime.set(Calendar.MONTH, newMonth - 1);
//            WeekViewEvent event = new MyWeekViewEvent("1", "kkk", startTime, endTime);
//            event.setColor(getResources().getColor(R.color.event_color_01));
//
//            Log.v("Test", "before: " + events.size());
//            if (myWeekViewEvent != null) {
            events.addAll(mNewEvents);
//                Log.v("Test", "size: " + events.size());


            return events;
        }
//    };

}
