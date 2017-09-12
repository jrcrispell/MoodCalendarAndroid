package com.wordpress.jrcrispell.moodcalendar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jrcrispell on 5/26/17.
 */

public class CalendarEvent implements Parcelable {

    private double startTime;
    private double duration;
    private String description;
    private int moodScore;
    private String startDay;
    private int dbEventID;

    private boolean beingEdited;

    public CalendarEvent(double startTime, double duration, String description, int moodScore, String startDay) {
        this.startTime = startTime;
        this.duration = duration;
        this.description = description;
        this.moodScore = moodScore;
        this.startDay = startDay;
        beingEdited = false;
    }
    public CalendarEvent() {

    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMoodScore() {
        return moodScore;
    }

    public void setMoodScore(int moodScore) {
        this.moodScore = moodScore;
    }

    protected CalendarEvent(Parcel in) {
        startTime = in.readDouble();
        duration = in.readDouble();
        description = in.readString();
        moodScore = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(startTime);
        dest.writeDouble(duration);
        dest.writeString(description);
        dest.writeInt(moodScore);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CalendarEvent> CREATOR = new Parcelable.Creator<CalendarEvent>() {
        @Override
        public CalendarEvent createFromParcel(Parcel in) {
            return new CalendarEvent(in);
        }

        @Override
        public CalendarEvent[] newArray(int size) {
            return new CalendarEvent[size];
        }
    };

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public int getDbEventID() {
        return dbEventID;
    }

    public void setDbEventID(int dbEventID) {
        this.dbEventID = dbEventID;
    }

    public boolean isBeingEdited() {
        return beingEdited;
    }

    public void setBeingEdited(boolean beingEdited) {
        this.beingEdited = beingEdited;
    }
}
