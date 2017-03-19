package com.group15.apps.carendar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Handler mHandler;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseMessageReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    static final int RC_ADD_EVENT = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);


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

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.flContent, new CalendarFragment());
    // or ft.add(R.id.your_placeholder, new FooFragment());
    // Complete the changes added above
        ft.commit();



    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private Fragment getFragment(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_account_fragment:
                return AccountFragment.newInstance();
//                break;
            case R.id.nav_map_fragment:
                return MapShowingFragment.newInstance();
//                break;
            case R.id.nav_calendar_fragment:
                return CalendarFragment.newInstance();
//                break;
            default:
                return CalendarFragment.newInstance();
        }

    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        final MenuItem item = menuItem;
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
//                        android.R.anim.fade_out);
                Fragment fragment = getFragment(item);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
//                fragmentTransaction.commitAllowingStateLoss();
            }
        };
//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Insert the fragment by replacing any existing fragment

//            final Fragment f = fragment;


        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }



//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
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

//                int mStartYear = data.getIntExtra("mStartYear", 0);
//                int mStartMonth = data.getIntExtra("mStartMonth", 0);
//                int mStartDay = data.getIntExtra("mStartDay", 0);
//                int mStartHour = data.getIntExtra("mStartHour", 0);
//                int mStartMinute = data.getIntExtra("mEndMinute", 0);
////
//
//                int mEndYear = data.getIntExtra("mEndYear", 0);
//                int mEndMonth = data.getIntExtra("mEndMonth", 0);
//                int mEndDay = data.getIntExtra("mEndDay", 0);
//                int mEndHour = data.getIntExtra("mEndHour", 0);
//                int mEndMinute = data.getIntExtra("mEndMinute", 0);

//                Calendar startTime = Calendar.getInstance();
//                startTime.set(Calendar.YEAR, 2017);
//                startTime.set(Calendar.MONTH, 3);
//                startTime.set(Calendar.DATE, 18);
//                startTime.set(Calendar.HOUR_OF_DAY, 2);
//                startTime.set(Calendar.MINUTE, 1);
////
//                Calendar endTime = (Calendar) startTime.clone();
//                endTime.set(Calendar.YEAR, 2017);
//                endTime.set(Calendar.MONTH, 3);
//                endTime.set(Calendar.DATE, 18);
//                endTime.set(Calendar.HOUR_OF_DAY, 4);
//                endTime.set(Calendar.MINUTE, 40);
//                WeekViewEvent event = new WeekViewEvent(1, "kkk", startTime, endTime);
//                event.setColor(getResources().getColor(R.color.event_color_01));
//                            Calendar startTime = Calendar.getInstance();
//            Calendar startTime = Calendar.getInstance();
//            startTime.set(Calendar.HOUR_OF_DAY, 3);
//            startTime.set(Calendar.MINUTE, 0);
//            startTime.set(Calendar.MONTH, 3 - 1);
//            startTime.set(Calendar.YEAR, 2017);
//            Calendar endTime = (Calendar) startTime.clone();
//            endTime.add(Calendar.HOUR, 4);
//            endTime.set(Calendar.MONTH, 3 - 1);
//            WeekViewEvent event = new MyWeekViewEvent("1", "kkk", startTime, endTime);
//            event.setColor(getResources().getColor(R.color.event_color_01));

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
//                mNewEvents.add(event);

                // Refresh the week view. onMonthChange will be called again.
//                mWeekView.notifyDatasetChanged();
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
                case android.R.id.home:
                    mDrawer.openDrawer(GravityCompat.START);
                    return true;
            }

        }
        return super.onOptionsItemSelected(item);
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

//    };

}
