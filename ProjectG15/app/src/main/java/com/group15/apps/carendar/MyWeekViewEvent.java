package com.group15.apps.carendar;

/**
 * Created by Neo on 3/10/17.
 */

import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.database.Exclude;

import java.util.Calendar;


public class MyWeekViewEvent extends WeekViewEvent {
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mName;
    private String mLocation;
    private int mColor;
    private long mStartTimeMills;
    private long mEndTimeMills;
    private boolean mIsGroupEvent;
    private String mGroupName;
    private int mType;
    private double mLongitude;
    private double mLatitude;
    private String mEventKey;
    public MyWeekViewEvent() {

    }
    public MyWeekViewEvent(String name, String location, Calendar startTime, Calendar endTime,
                           boolean isGroupEvent, String groupName, String eventKey) {

        this.mStartTime = (Calendar) startTime.clone();
        this.mEndTime = (Calendar) endTime.clone();
        this.mStartTimeMills = startTime.getTimeInMillis();
        this.mEndTimeMills = endTime.getTimeInMillis();
        this.mName = name;
        this.mLocation = location;
        this.mIsGroupEvent = isGroupEvent;
        this.mGroupName = groupName;
        this.mEventKey = eventKey;
    }

    public String getEventKey() {
        return mEventKey;
    }

    public void setEventKey(String eventKey) {
        this.mEventKey = eventKey;
    }

    public boolean getIsGroupEvent() {
        return mIsGroupEvent;
    }

    public void setIsGroupEvent(boolean isGroupEvent) {
        this.mIsGroupEvent = isGroupEvent;
    }


    public void setGroupName(String groupName) {
        this.mGroupName = groupName;
    }

    public String getGroupName() {
        return this.mGroupName;
    }


    @Exclude
    public Calendar getStartTime() {
        if (mStartTime == null) {
            mStartTime = Calendar.getInstance();
            mStartTime.setTimeInMillis(mStartTimeMills);
        }
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }

    @Exclude
    public Calendar getEndTime() {
        if (mEndTime == null) {
            mEndTime = Calendar.getInstance();
            mEndTime.setTimeInMillis(mEndTimeMills);
        }
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
    }

    public String getName() {
        return mName + "\n";
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public void setStartTimeMills(long startTimeMills) {

        this.mStartTimeMills = startTimeMills;
    }

    public void setEndTimeMills(long endTimeMills) {
        this.mEndTimeMills = endTimeMills;
    }

    public long getStartTimeMills() {
        return  mStartTimeMills;
    }

    public long getEndTimeMills() {
        return this.mEndTimeMills;
    }

    public void setEventType(int type){
        this.mType = type;
    }

    public int getEventType(){
        return mType;
    }

    public void setLongitude(double longitude){
        this.mLongitude = longitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public void setLatitude(double latitude){
        this.mLatitude = latitude;
    }

    public double getLatitude(){
        return mLatitude;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass() || getEventKey() == null){
            return false;
        }

        MyWeekViewEvent that = (MyWeekViewEvent) o;

        return this.getEventKey().equals(that.getEventKey());
    }
}
