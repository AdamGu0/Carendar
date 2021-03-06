package com.group15.apps.carendar;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * the code to implement navigation drawer is adapted from
 * http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/
 */

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Handler mHandler;
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    private FloatingActionButton mFloatingActionButton;
    private Spinner mSpinner;

    private DatabaseReference fb;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    static final int RC_ADD_EVENT = 2;
    static final int RC_MAP_FINISH = 3;
    static final int RC_SELECT_ICS = 4;
    private static final String TAG_CALENDAR = "calendar";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_MAP = "map";
    public static String CURRENT_TAG = TAG_CALENDAR;
    private String[] activityTitles;

    private Map<Integer, List<MyWeekViewEvent>> mPersonalEventsMap = new HashMap<>();

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;

    private String mUserID;
    DatabaseReference eventsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (fb != null) FirebaseDatabase.getInstance().setPersistenceEnabled(true);

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
        mSpinner = (Spinner) findViewById(R.id.sp_filter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onFilterItemSelected(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        // Find our drawer view
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        mDrawer.addDrawerListener(new drawerListener());
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_CALENDAR;
            loadHomeFragment();
        }
        fb = FirebaseDatabase.getInstance().getReference();
        fb.keepSynced(true);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "You're are now signed in. Welcome to Calendar.", Toast.LENGTH_SHORT).show();
                    retrieveEvents();
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
        ft.replace(R.id.flContent, calendarFragment, TAG_CALENDAR);
        // Complete the changes added above
        ft.commit();


        for (int i = 0; i < 12; i++) {
            mPersonalEventsMap.put(i, new ArrayList<MyWeekViewEvent>());
        }

    }



    private void onFilterItemSelected(int position) {
        CalendarFragment c = (CalendarFragment) getSupportFragmentManager().findFragmentByTag(TAG_CALENDAR);
        if (c == null) return;
        c.setEventType(position);
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
        requestPermit();
    }

    private void requestPermit(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                CalendarFragment calendarFragment = new CalendarFragment();
                calendarFragment.updatePersonalEventMap(mPersonalEventsMap);
                calendarFragment.setEventType(mSpinner.getSelectedItemPosition());
                return calendarFragment;
            case 1:
                AccountFragment accountFragment = new AccountFragment();
                return accountFragment;
            default:
                return null;
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
        switch (menuItem.getItemId()) {
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
                //navItemIndex = 2;
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
    }


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
            retrieveEvents();
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
                boolean isGroupEvnet = data.getBooleanExtra("isGroupEvent", false);
                String groupName = data.getStringExtra("groupName");
                int eventType = data.getIntExtra("type", 0);
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.YEAR, data.getIntExtra("mStartYear", 0));
                startTime.set(Calendar.MONTH, data.getIntExtra("mStartMonth", 0));
                startTime.set(Calendar.DAY_OF_MONTH, data.getIntExtra("mStartDay", 0));
                startTime.set(Calendar.HOUR_OF_DAY, data.getIntExtra("mStartHour", 0));
                startTime.set(Calendar.MINUTE, data.getIntExtra("mStartMinute", 0));

                Calendar endTime = Calendar.getInstance();
                endTime.set(Calendar.YEAR, data.getIntExtra("mEndYear", 0));
                endTime.set(Calendar.MONTH, data.getIntExtra("mEndMonth", 0));
                endTime.set(Calendar.DAY_OF_MONTH, data.getIntExtra("mEndDay", 0));
                endTime.set(Calendar.HOUR_OF_DAY, data.getIntExtra("mEndHour", 0));
                endTime.set(Calendar.MINUTE, data.getIntExtra("mEndMinute", 0));

                String key = fb.push().getKey();

                MyWeekViewEvent event = new MyWeekViewEvent(title, location, startTime, endTime, isGroupEvnet, groupName, key);
                event.setEventType(eventType);
                event.setLongitude(data.getDoubleExtra("longitude", 0));
                event.setLatitude(data.getDoubleExtra("latitude", 0));

                fb.child("users").child(mUserID).child("events").child(key).setValue(event);
                addEventToList(data.getIntExtra("mStartMonth", 0), event);


            }
        } else if (requestCode == RC_SELECT_ICS) {
            if (data != null && data.getData() != null) {
                parseIcsData(data.getData());
            }
        } else if (requestCode == RC_MAP_FINISH) {
            toggleFab();
        }
    }

    private void parseIcsData(Uri uri) {
        new ParseCalendarTask(uri, this, new ParseCalendarTask.OnParseFinishListener() {
            @Override
            public void onParseFinish(List<MyWeekViewEvent> eventList) {
                if(isDestroyed()){
                    return;
                }
                if(eventList == null || eventList.size() == 0){
                    return;
                }
                updateEvents(eventList);
            }
        }).execute();
    }

    private void updateEvents(List<MyWeekViewEvent> eventList){
        Set<String> keys = getEventKeys();
        for(MyWeekViewEvent event : eventList){
            String key = event.getEventKey();
            if(!keys.contains(key)){
                // save to firebase
                fb.child("users").child(mUserID).child("events").child(key).setValue(event);
            }
        }
    }

    private Set<String> getEventKeys(){
        HashSet<String> set = new HashSet<>();
        for(Map.Entry<Integer, List<MyWeekViewEvent>> entry : mPersonalEventsMap.entrySet()){
            List<MyWeekViewEvent> events = entry.getValue();
            for(MyWeekViewEvent event : events){
                set.add(event.getEventKey());
            }
        }
        return set;
    }


    /**
     * Put the classes events into reminder service. Silent the phone before class.
     * call this function after updating the events.
     */
    private void onEventsUpdate(){
        ArrayList<ReminderEvent> classEvents = getClassesEvents();
        if(classEvents != null && classEvents.size() > 0){
            Intent intent = new Intent(this, ReminderService.class);
            intent.putParcelableArrayListExtra("events", classEvents);
            startService(intent);
        }
    }

    private ArrayList<ReminderEvent> getClassesEvents(){
        if(mPersonalEventsMap != null && mPersonalEventsMap.size() > 0){
            ArrayList<ReminderEvent> events = new ArrayList<>();
            final long now = System.currentTimeMillis();
            for(Map.Entry<Integer, List<MyWeekViewEvent>> entry : mPersonalEventsMap.entrySet()){
                List<MyWeekViewEvent> eventList = entry.getValue();
                if(eventList != null && eventList.size() > 0){
                    for (MyWeekViewEvent event : eventList){
                        if(event.getStartTimeMills() > now && event.getEventType() == EventType.CLASS){
                            events.add(new ReminderEvent(event));
                        }
                    }
                }
            }
            return events;
        }
        return new ArrayList<>(0);
    }

    private void addEventToList(int index, MyWeekViewEvent event) {
        List<MyWeekViewEvent> list = mPersonalEventsMap.get(index);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(event);
        mPersonalEventsMap.put(index, list);
        onEventsUpdate();
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
                case R.id.import_menu:
                    showFileChooser();
                    return true;
                case R.id.export_menu:
                    onExportClicked();
                    return true;
            }

        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shortToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void onExportClicked(){
        new ExportCalendarTask(this, mPersonalEventsMap, new ExportCalendarTask.OnExportFinishListener() {
            @Override
            public void onExportFinish() {
                shortToast("Export finished.");
            }
        }).execute();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    RC_SELECT_ICS);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0) {
            mFloatingActionButton.show();
            mSpinner.setVisibility(View.VISIBLE);
        } else {
            mFloatingActionButton.hide();
            mSpinner.setVisibility(View.INVISIBLE);
        }
    }

    public void retrieveEvents() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null) return;
        mUserID = u.getUid();
        eventsReference = fb.child("users").child(mUserID).child("events");

        eventsReference.addChildEventListener(new ChildEventListener(){

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String key) {
                if (dataSnapshot.exists()) {
                    MyWeekViewEvent event = dataSnapshot.getValue(MyWeekViewEvent.class);
                    Calendar startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(event.getStartTimeMills());
                    Calendar endTime = Calendar.getInstance();
                    endTime.setTimeInMillis(event.getEndTimeMills());
                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                    List<MyWeekViewEvent> list = mPersonalEventsMap.get(startTime.get(Calendar.MONTH));

                    if (!list.contains(event)) {
                        list.add(event);
                        // call this function to notify the class reminder
                        onEventsUpdate();
                    }
                }
                CreateCalendarFragment();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MyWeekViewEvent event = dataSnapshot.getValue(MyWeekViewEvent.class);
                List<MyWeekViewEvent> list = mPersonalEventsMap.get(event.getStartTime().get(Calendar.MONTH));
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getEventKey().equals(event.getEventKey())) {
                        list.remove(i);
                        // call this function to notify the class reminder
                        onEventsUpdate();
                    }
                }
                CreateCalendarFragment();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                MyWeekViewEvent event = dataSnapshot.getValue(MyWeekViewEvent.class);
                List<MyWeekViewEvent> list = mPersonalEventsMap.get(event.getStartTime().get(Calendar.MONTH));

                if (event == null || event.getEventKey() == null) return;

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getEventKey().equals(event.getEventKey())) {
                        list.remove(i);
                        list.add(i, event);
                        // call this function to notify the class reminder
                        onEventsUpdate();
                    }
                }

                CreateCalendarFragment();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
    }


    private void CreateCalendarFragment() {
        CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag(TAG_CALENDAR);
        if (calendarFragment != null) {
            calendarFragment.updatePersonalEventMap(mPersonalEventsMap);
            calendarFragment.notifyChange();
        }

    }

    private class drawerListener implements DrawerLayout.DrawerListener{
        @Override
        public void onDrawerOpened(View drawerView) {
            mSpinner.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            if (navItemIndex == 0) {
                mSpinner.setVisibility(View.VISIBLE);
                mFloatingActionButton.show();
            }
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mSpinner.setVisibility(View.INVISIBLE);
            mFloatingActionButton.hide();

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    }
}
