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
    private int mType;
    private double mLongitude;
    private double mLatitude;


    public MyWeekViewEvent() {

    }
    public MyWeekViewEvent(String name, String location, Calendar startTime, Calendar endTime) {

        this.mStartTime = (Calendar) startTime.clone();
        this.mEndTime = (Calendar) endTime.clone();
        this.mStartTimeMills = startTime.getTimeInMillis();
        this.mEndTimeMills = endTime.getTimeInMillis();
        this.mName = name + "\n";
        this.mLocation = location;
    }

    @Exclude
    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }

    @Exclude
    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
    }

    public String getName() {
        return mName;
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
        return  this.mStartTimeMills;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeekViewEvent that = (WeekViewEvent) o;

        return this.getName() == that.getName() && this.getLocation() == that.getLocation();
    }
}
