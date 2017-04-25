package com.group15.apps.carendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

import static java.security.AccessController.getContext;

/**
 * Created by Lei
 */

public class ReminderService extends Service {
//    private List<ReminderEvent> mInUseEvents = new ArrayList<>();
    private ReminderEvent mCurrentEvent = null;
    // The events happens one after another
    private List<ReminderEvent> mWaitingEvents = new ArrayList<>();
    private Handler mHandler = new Handler();
    private GPSTracker mGPSTracker = null;
    private final int LOCATION_INTERVAL = 5 * 60 * 1000;
    private int mLastLocationTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        new MonitorThread().start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        if(intent != null){
            List<ReminderEvent> eventList = intent.getParcelableArrayListExtra("events");
            addEvents(eventList);
        }
        return result;
    }

    private void addEvents(List<ReminderEvent> events){
        if(events != null && events.size() > 0){
            mWaitingEvents.clear();
            long now = System.currentTimeMillis() + 1000;
            for(ReminderEvent event : events){
                if(event != null && event.getStartTimeMills() > now){
                    mWaitingEvents.add(event);
                }
            }
            sortEventsByTime();
        }
    }

    private void sortEventsByTime(){
        if(mWaitingEvents != null && mWaitingEvents.size() > 0){
            Collections.sort(mWaitingEvents, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    ReminderEvent eventLeft = (ReminderEvent)o1;
                    ReminderEvent eventRight = (ReminderEvent)o2;
                    return (int)(eventLeft.getStartTimeMills() - eventRight.getStartTimeMills());
                }
            });
        }
    }

    private void setPhoneSilent() {
        AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        //For Silent mode
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        //For Vibrate mode
//        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    private void setPhoneNormal(){
        AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        //For Normal mode
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    private class MonitorThread extends Thread{
        @Override
        public void run() {
            try {
                while(true){
                    sleep(5000);
                    runOnUiThread();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void runOnUiThread(){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    checkEvents();
                }
            });
        }
    }

    private void checkEvents(){
        if(mCurrentEvent != null){
            processEndEvent(mCurrentEvent);
        }else if (mWaitingEvents != null && mWaitingEvents.size() > 0){
//            removePassedEvents();
            processTheComingEvent();
        }
    }

    private void processTheComingEvent(){
        if(mWaitingEvents != null && mWaitingEvents.size() > 0){
            updateLocation();
            ReminderEvent event = mWaitingEvents.get(0);
            boolean isInClassroom = false;
            if(isEventComing(event.getStartTimeMills(), 5)){
                isInClassroom = isInTheClassroom(event);
            }
            if(isTimeClose(event.getStartTimeMills()) && isInClassroom){
                // Silent the phone when the class is going to begin.
                setPhoneSilent();
                longToast(String.format("Class %s is going to begin, silent the phone.", event.getName()));
                mCurrentEvent = event;
                mWaitingEvents.remove(0);
                stopLocation();
            }
        }
    }

    private void updateLocation(){
        if(mGPSTracker == null){
            mGPSTracker = new GPSTracker(getApplicationContext());
        }else {
            final long now = System.currentTimeMillis();
            if( now - mLastLocationTime >= LOCATION_INTERVAL ){
                mGPSTracker.getLocation();
            }
        }
    }

    private void stopLocation(){
        if(mGPSTracker != null){
            mGPSTracker.stopUsingGPS();
        }
    }

    /**
     *
     * @param eventStartTimeMillis
     * @return true if the class will begin in 2 minutes, false otherwise
     */
    private boolean isTimeClose(long eventStartTimeMillis){
        final long now = System.currentTimeMillis();
        // The event is going to happen in 3 minutes.
        return eventStartTimeMillis <= now + 2 * 60 * 1000;
    }

    private boolean isEventComing(long eventStartTimeMillis, int minutes){
        final long now = System.currentTimeMillis();
        // The event is going to happen in 3 minutes.
        return eventStartTimeMillis <= now + minutes * 60 * 1000;
    }

    private boolean isInTheClassroom(ReminderEvent event){
        if(mGPSTracker == null){
            mGPSTracker = new GPSTracker(getApplicationContext());
        }
        mGPSTracker.getLocation();
        float[] result = new float[1];
        // get the distance in meters
        Location.distanceBetween(event.getLatitude(), event.getLongitude(),
                getCurrentLatitude(), getCurrentLongitude(), result);
        float distance = result[0];
        if(distance <= 50){
            return true;
        }
        return false;
//        return true;
    }

    private double getCurrentLongitude(){
        if(mGPSTracker != null){
            return mGPSTracker.getLongitude();
        }
        return 0.0;
    }

    private double getCurrentLatitude(){
        if(mGPSTracker != null){
            return mGPSTracker.getLatitude();
        }
        return 0.0;
    }

    private void removePassedEvents(){
        final long now = System.currentTimeMillis();
        Iterator<ReminderEvent> it = mWaitingEvents.iterator();
        while(it.hasNext()){
            ReminderEvent event = it.next();
            if(event.getStartTimeMills() < now){
                it.remove();
            }
        }
    }

    private void processEndEvent(ReminderEvent event){
        final long now = System.currentTimeMillis();
        if(now >= event.getEndTimeMills()){
            setPhoneNormal();
            longToast(String.format("Class %s is over, set the phone to normal mode.", event.getName()));
            mCurrentEvent = null;
        }
    }

    private void longToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

}
