package com.example.mike.birdalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class Alarm implements Parcelable {

    private static final String LOG_TAG = Alarm.class.getSimpleName();
    private Context context = null;

    private long id;
    private String label;
    private boolean aMpM;
    private int hour;
    private int minute;
    private boolean alarmIsRepeating;
    private Days[] days = {Days.MONDAY, Days.TUESDAY, Days.WEDNESDAY,
            Days.THURSDAY, Days.FRIDAY, Days.SATURDAY, Days.SUNDAY};
    private boolean isExpanded;

    private AlarmManager alarmManager;
    private PendingIntent pendingAlarmIntent;

    Alarm (Context context, int hour, int minute, long alarmId){

        this.context = context;

        this.hour = Utility.getHourFor12HourClock(hour);
        this.minute = minute;
        this.aMpM = true;

        Log.v("Hours2", hour + " or " + Utility.getHourFor12HourClock(hour));


        //Needs to be set from the label in the database
        this.label = "Alarm3";

        this.id = alarmId;

        alarmIsRepeating = false;

        isExpanded = true;

        registerAlarm();

    }

    Alarm (Context context, int hour, int minute){

        this.context = context;

        this.id = Utility.getTimeStampFromHourAndMinute(hour, minute);

        this.hour = Utility.getHourFor12HourClock(Utility.getHourFromTimeStamp(id));
        this.minute = Utility.getMinuteFromTimeStamp(id);
        this.aMpM = setAmPm(hour);

        this.label = "Alarm4";

        alarmIsRepeating = false;

        isExpanded = true;

        registerAlarm();

        ContentResolver contentResolver = context.getContentResolver();

        ContentValues alarmValues = new ContentValues();

        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID, this.id);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME, this.id);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE, 1);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING, 1);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE, "robin");
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_LABEL, this.label);

        contentResolver.insert(UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI, alarmValues);

    }

    private Alarm(Parcel in){

        id = in.readLong();
        hour = in.readInt();
        minute = in.readInt();
        label = in.readString();
    }

    public void registerAlarm() {

        pendingAlarmIntent = null;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        alarmIntent.putExtra("alarmPassedInThroughIntent", this);

        pendingAlarmIntent = PendingIntent.getBroadcast(context, (int) id, alarmIntent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, id, pendingAlarmIntent);

        Log.v(LOG_TAG, "Alarm registered " + getLabel());

    }

    public void cancelAlarm(){

        alarmManager.cancel(pendingAlarmIntent);
        Log.v(LOG_TAG, "ALARM CANCELLED");

    }

    private boolean setAmPm(int hour){ return hour < 12; }

    public void setDayAlarmOnOrOff(Days day){
        day.alarmOn = !day.alarmOn;
    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>(){

        @Override
        public Alarm createFromParcel(Parcel in) {

            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int i) {
            return new Alarm[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeLong(id);
        out.writeInt(hour);
        out.writeInt(minute);
        out.writeString(label);

    }

    public void setId(int id) {
        this.id = id;
    }

    public long getId(){

        return this.id;
    }

    enum Days {

        MONDAY(true), TUESDAY(true), WEDNESDAY(true), THURSDAY(true),
        FRIDAY(true), SATURDAY(true), SUNDAY(true);

        boolean alarmOn;

        Days(boolean alarmOn){
            this.alarmOn = alarmOn;
        }

    }

    String getaMpM() { return aMpM ? "AM" : "PM"; }

    int getHour() { return hour; }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isAlarmIsRepeating() {
        return alarmIsRepeating;
    }

    public void setAlarmIsRepeating(boolean alarmIsRepeating) {
        this.alarmIsRepeating = alarmIsRepeating;
    }

    public boolean isExpanded(){
        return isExpanded;
    }

    public void setExpandedState(boolean activeOrNot){
        isExpanded = activeOrNot;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}