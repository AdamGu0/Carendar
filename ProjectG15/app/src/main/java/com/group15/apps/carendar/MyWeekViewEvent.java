package com.group15.apps.carendar;

/**
 * Created by Neo on 3/10/17.
 */

import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;


public class MyWeekViewEvent extends WeekViewEvent {
    //private long mId;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mTitle;
    private String mLocation;
    private int mColor;

    public MyWeekViewEvent() {

    }

    public MyWeekViewEvent(String title, String location, Calendar startTime, Calendar endTime) {
        //this.mId = id;

        this.mStartTime = (Calendar) startTime.clone();

        this.mEndTime = (Calendar) endTime.clone();

        this.mTitle = title;
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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
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

//    public long getId() {
//        return mId;
//    }
//
//    public void setId(long id) {
//        this.mId = id;
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        WeekViewEvent that = (WeekViewEvent) o;
//
//        return mTitle == that.mTitle;
//
//    }

//    @Override
//    public int hashCode() {
//        return (int) (mId ^ (mId >>> 32));
//    }
}
