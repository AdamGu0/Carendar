package com.group15.apps.carendar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lei
 *
 */

public class ReminderEvent implements Parcelable {
    private String mEventKey;
    private String mName;
    private String mLocation;
    private long mStartTimeMills;
    private long mEndTimeMills;
    private double mLongitude;
    private double mLatitude;
    private int mType;

    public ReminderEvent(MyWeekViewEvent event){
        mEventKey = event.getEventKey();
        mName = event.getName();
        mLocation = event.getLocation();
        mStartTimeMills = event.getStartTimeMills();
        mEndTimeMills = event.getEndTimeMills();
        mLongitude = event.getLongitude();
        mLatitude = event.getLatitude();
        mType = event.getEventType();
    }

    protected ReminderEvent(Parcel in) {
        mEventKey = in.readString();
        mName = in.readString();
        mLocation = in.readString();
        mStartTimeMills = in.readLong();
        mEndTimeMills = in.readLong();
        mLongitude = in.readDouble();
        mLatitude = in.readDouble();
        mType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEventKey);
        dest.writeString(mName);
        dest.writeString(mLocation);
        dest.writeLong(mStartTimeMills);
        dest.writeLong(mEndTimeMills);
        dest.writeDouble(mLongitude);
        dest.writeDouble(mLatitude);
        dest.writeInt(mType);
    }

    public static final Creator<ReminderEvent> CREATOR = new Creator<ReminderEvent>() {
        @Override
        public ReminderEvent createFromParcel(Parcel in) {
            return new ReminderEvent(in);
        }

        @Override
        public ReminderEvent[] newArray(int size) {
            return new ReminderEvent[size];
        }
    };

    public String getEventKey() {
        return mEventKey;
    }

    public void setEventKey(String eventKey) {
        this.mEventKey = eventKey;
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

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public long getStartTimeMills() {
        return mStartTimeMills;
    }

    public void setStartTimeMills(long mStartTimeMills) {
        this.mStartTimeMills = mStartTimeMills;
    }

    public long getEndTimeMills() {
        return mEndTimeMills;
    }

    public void setEndTimeMills(long mEndTimeMills) {
        this.mEndTimeMills = mEndTimeMills;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public int getType() {
        return mType;
    }

    public void setmType(int ype) {
        this.mType = ype;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null || getClass() != obj.getClass() || getEventKey() == null){
            return false;
        }
        ReminderEvent that = (ReminderEvent)obj;
        return getEventKey().equals(that.getEventKey());
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
