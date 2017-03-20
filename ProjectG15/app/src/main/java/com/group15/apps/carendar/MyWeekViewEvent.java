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
//    private int mStartYear;
//    private int mStartMonth;
//    private int mStartDay;
//    private int mStartHour;
//    private int mStartMinute;
//
//    private int mEndYear;
//    private int mEndMonth;
//    private int mEndDay;
//    private int mEndHour;
//    private int mEndMinute;

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
//    public MyWeekViewEvent(String title, String location, int startYear, int startMonth, int startDay, int startHour,
//                           int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
//        //this.mId = id;
//        this.mStartYear = startYear;
//        this.mEndMonth = startMonth;
//        this.mStartHour = startHour;
//        this.mStartDay = startDay;
//        this.mStartMinute = startMinute;
//        this.mEndYear = endYear;
//        this.mEndMonth = endMonth;
//        this.mEndDay = endDay;
//        this.mEndHour = endHour;
//        this.mEndMinute = endMinute;

//        this.mStartTime = (Calendar) startTime.clone();
//
//        this.mEndTime = (Calendar) endTime.clone();

//        this.mTitle = title;
//        this.mLocation = location;
//    }

//    public void setmStartYear(int mStartYear) {
//        this.mStartYear = mStartYear;
//    }
//
//    public void setmStartMonth(int mStartMonth) {
//        this.mStartMonth = mStartMonth;
//    }
//
//    public void setmStartDay(int mStartDay) {
//        this.mStartDay = mStartDay;
//    }
//
//    public void setmStartHour(int mStartHour) {
//        this.mStartHour = mStartHour;
//    }
//
//    public void setmStartMinute(int mStartMinute) {
//        this.mStartMinute = mStartMinute;
//    }
//
//    public void setmEndYear(int mEndYear) {
//        this.mEndYear = mEndYear;
//    }
//
//    public void setmEndMonth(int mEndMonth) {
//        this.mEndMonth = mEndMonth;
//    }
//
//    public void setmEndDay(int mEndDay) {
//        this.mEndDay = mEndDay;
//    }
//
//    public void setmEndHour(int mEndHour) {
//        this.mEndHour = mEndHour;
//    }
//
//    public void setmEndMinute(int mEndMinute) {
//        this.mEndMinute = mEndMinute;
//    }
//
//    public int getmStartYear() {
//        return mStartYear;
//    }
//
//    public int getmStartMonth() {
//        return mStartMonth;
//    }
//
//    public int getmStartDay() {
//        return mStartDay;
//    }
//
//    public int getmStartHour() {
//        return mStartHour;
//    }
//
//    public int getmStartMinute() {
//        return mStartMinute;
//    }
//
//    public int getmEndYear() {
//        return mEndYear;
//    }
//
//    public int getmEndMonth() {
//        return mEndMonth;
//    }
//
//    public int getmEndDay() {
//        return mEndDay;
//    }
//
//    public int getmEndHour() {
//        return mEndHour;
//    }
//
//    public int getmEndMinute() {
//        return mEndMinute;
//    }
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


}
