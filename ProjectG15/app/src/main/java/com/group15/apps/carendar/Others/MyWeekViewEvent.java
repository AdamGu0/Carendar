package com.group15.apps.carendar.Others;

/**
 * Created by Neo on 3/10/17.
 */

import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;


public class MyWeekViewEvent extends WeekViewEvent {
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mName;
    private String mLocation;
    private int mColor;

    public MyWeekViewEvent() {

    }
    public MyWeekViewEvent(String name, String location, Calendar startTime, Calendar endTime) {

        this.mStartTime = (Calendar) startTime.clone();
        this.mEndTime = (Calendar) endTime.clone();
        this.mName = name + "\n";
        this.mLocation = location;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }
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
}
