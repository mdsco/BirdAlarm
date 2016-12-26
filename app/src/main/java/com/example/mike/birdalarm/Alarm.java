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

class Alarm implements Parcelable {

    private int id;

    private boolean aMpM;
    private int hour;
    private int minute;
    private boolean alarmIsRepeating;
    private Days[] days = {Days.MONDAY, Days.TUESDAY, Days.WEDNESDAY,
            Days.THURSDAY, Days.FRIDAY, Days.SATURDAY, Days.SUNDAY};
    private boolean isExpanded;

    private AlarmManager alarmManager;
    private PendingIntent pendingAlarmIntent;

    Alarm (Context context, int hour, int minute, int alarmId){

        this.hour = getCorrectHour(hour);
        this.minute = minute;
        this.aMpM = true;

        this.id = alarmId;

        alarmIsRepeating = false;

        isExpanded = true;

        registerAlarm(context, hour, this.id);


    }

    Alarm (Context context, int hour, int minute){

        this.hour = getCorrectHour(hour);
        this.minute = minute;
        this.aMpM = setAmPm(hour);

        long currentTime = System.currentTimeMillis();
        this.id = (int) currentTime;

        alarmIsRepeating = false;

        isExpanded = true;

        registerAlarm(context, hour, this.id);

        ContentResolver contentResolver = context.getContentResolver();

        ContentValues alarmValues = new ContentValues();

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, this.minute);
        calendar.set(Calendar.SECOND, 0);

        long timestamp = calendar.getTimeInMillis();

        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID, this.id);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME, timestamp);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE, 1);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING, 1);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE, "robin");

        Uri uri = contentResolver.insert(UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI, alarmValues);

    }

    private Alarm(Parcel in){

        hour = in.readInt();
        minute = in.readInt();
    }

    private void registerAlarm(Context context, int hour, int id) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, this.minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("Time", getCorrectHour(hour) + ":" +
                                        String.format("%02d", minute));

        pendingAlarmIntent = PendingIntent.getBroadcast(context, id, alarmIntent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingAlarmIntent);

    }

    public void cancelAlarm(){

        alarmManager.cancel(pendingAlarmIntent);


    }

    private int getCorrectHour(int hour){

        if(hour == 0){
            return 12;
        } else if(hour > 12){
            return hour - 12;
        }

        return hour;
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

        out.writeInt(hour);
        out.writeInt(minute);

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){

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

}