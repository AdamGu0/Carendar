package com.group15.apps.carendar;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Handler mHandler;
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    private FloatingActionButton mFloatingActionButton;
    private DatabaseReference fb;
//    private FirebaseDatabase mFirebaseDatabase;
//    private DatabaseReference mEventDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    static final int RC_ADD_EVENT = 2;
    static final int RC_MAP_FINISH = 3;

    private static final String TAG_CALENDAR = "calendar";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_MAP = "map";
    public static String CURRENT_TAG = TAG_CALENDAR;
    private String[] activityTitles;

    private Map<Integer, List<MyWeekViewEvent>> mPersonalEventsMap = new HashMap<>();

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

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

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                MainActivity.this.startActivityForResult(intent, RC_ADD_EVENT);

            }
        });
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        // Find our drawer view
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_CALENDAR;
            loadHomeFragment();
        }
        fb = FirebaseDatabase.getInstance().getReference();
        fb.keepSynced(true);
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mEventDatabaseReference = mFirebaseDatabase.getReference().child("events");

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "You're are now signed in. Welcome to Calendar.", Toast.LENGTH_SHORT).show();
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
        CalendarFragment calendarFragment = new CalendarFragment();
        calendarFragment.updatePersonalEventMap(mPersonalEventsMap);
        ft.replace(R.id.flContent, calendarFragment, TAG_CALENDAR);
    // Complete the changes added above
        ft.commit();
        retrieveEvents();

        for (int i = 0; i < 12; i++) {
            mPersonalEventsMap.put(i, new ArrayList<MyWeekViewEvent>());
        }

    }

    private void loadHomeFragment() {
        nvDrawer.getMenu().getItem(navItemIndex).setChecked(true);
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            mDrawer.closeDrawers();
            // show or hide the fab button
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.flContent, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        mDrawer.closeDrawers();
        toggleFab();
        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                CalendarFragment calendarFragment = new CalendarFragment();
                return calendarFragment;
            case 1:
                AccountFragment accountFragment = new AccountFragment();
                return accountFragment;
            default:
                return new CalendarFragment();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
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


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            toggleFab();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_CALENDAR;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    public boolean selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch(menuItem.getItemId()) {
            case R.id.nav_calendar_fragment:
                CURRENT_TAG = TAG_CALENDAR;
                navItemIndex = 0;
                break;
            case R.id.nav_account_fragment:
                CURRENT_TAG = TAG_ACCOUNT;
                navItemIndex = 1;
                break;
            case R.id.nav_map_fragment:
                CURRENT_TAG = TAG_MAP;
                navItemIndex = 2;
                Intent intent = new Intent(MainActivity.this, MapShowingActivity.class);
                startActivityForResult(intent, RC_MAP_FINISH);
                mDrawer.closeDrawers();
                return true;
            default:
                navItemIndex = 0;
        }

        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        } else {
            menuItem.setChecked(true);
        }
        menuItem.setChecked(true);

        loadHomeFragment();

        return true;
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        retrieveEvents();
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
        } else if (requestCode == RC_ADD_EVENT) {
            if (resultCode == RESULT_OK) {
                String location = data.getStringExtra("location");
                String title = data.getStringExtra("title");
                int eventType = data.getIntExtra("type", 0);
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.YEAR, data.getIntExtra("mStartYear", 0));
                startTime.set(Calendar.MONTH, data.getIntExtra("mStartMonth", 0));
                startTime.set(Calendar.DAY_OF_MONTH,data.getIntExtra("mStartDay", 0));
                startTime.set(Calendar.HOUR_OF_DAY, data.getIntExtra("mStartHour", 0));
                startTime.set(Calendar.MINUTE, data.getIntExtra("mStartMinute", 0));

                Calendar endTime = Calendar.getInstance();
                endTime.set(Calendar.YEAR, data.getIntExtra("mEndYear", 0));
                endTime.set(Calendar.MONTH, data.getIntExtra("mEndMonth", 0));
                endTime.set(Calendar.DAY_OF_MONTH, data.getIntExtra("mEndDay", 0));
                endTime.set(Calendar.HOUR_OF_DAY, data.getIntExtra("mEndHour", 0));
                endTime.set(Calendar.MINUTE, data.getIntExtra("mEndMinute", 0));

                MyWeekViewEvent event = new MyWeekViewEvent(title, location, startTime, endTime);
                event.setEventType(eventType);
                event.setColor(getResources().getColor(R.color.event_color_01));
                fb.child("events").push().setValue(event);

                addEventToList(data.getIntExtra("mStartMonth", 0), event);

                CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag(TAG_CALENDAR);

                calendarFragment.updatePersonalEventMap(mPersonalEventsMap);
                calendarFragment.notifyChange();
            }
        }
    }


    private void addEventToList(int index, MyWeekViewEvent event) {
        List<MyWeekViewEvent> list = mPersonalEventsMap.get(index);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(event);
        mPersonalEventsMap.put(index, list);
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
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
             mFloatingActionButton.show();
        else
            mFloatingActionButton.hide();
    }

    public void retrieveEvents() {
        DatabaseReference events = fb.child("events/");
        events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        MyWeekViewEvent event = data.getValue(MyWeekViewEvent.class);
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTimeInMillis(event.getStartTimeMills());
                        Calendar endTime = Calendar.getInstance();
                        endTime.setTimeInMillis(event.getEndTimeMills());
                        event.setStartTime(startTime);
                        event.setEndTime(endTime);
//                        if (mPersonalEventsMap.get(month) == null) {
//                            mPersonalEventsMap.put();
//                        }
                        List<MyWeekViewEvent> list = mPersonalEventsMap.get(startTime.get(Calendar.MONTH));

                        if (!list.contains(event)) {
                            list.add(event);
                        }
                    }
                }
                CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag(TAG_CALENDAR);
                calendarFragment.updatePersonalEventMap(mPersonalEventsMap);
                calendarFragment.notifyChange();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
