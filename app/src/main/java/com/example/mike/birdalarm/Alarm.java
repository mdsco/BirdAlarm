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
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

class Alarm implements Parcelable, Subject {

    private static final String LOG_TAG = Alarm.class.getSimpleName();

    public Context context = null;

    private int id;
    private long timestamp;

    private int isActive;

    private String label;
    private String alarmType;

    private boolean alarmIsRepeating;
    private Days[] days = {Days.MONDAY, Days.TUESDAY, Days.WEDNESDAY,
            Days.THURSDAY, Days.FRIDAY, Days.SATURDAY, Days.SUNDAY};
    private boolean isExpanded;

    private AlarmManager alarmManager;
    private PendingIntent pendingAlarmIntent;
    private ArrayList<AlarmObserver> alarmObservers;

    Alarm (Context context, int alarmId, long timestamp, int active, String label, String alarmType){

        this.context = context;


        this.id = alarmId;
        this.timestamp = timestamp;

        this.isActive = active;

        this.label = label;
        this.alarmType = alarmType;

        alarmIsRepeating = false;

        isExpanded = true;

        registerAlarm(this.id);
    }

    Alarm (Context context, int hour, int minute){

        alarmObservers = new ArrayList<AlarmObserver>();

        this.context = context;

        this.timestamp = Utility.getTimeStampFromHourAndMinute(hour, minute);
        this.id = (int) this.timestamp;

        this.isActive = 1;

        this.alarmType = Defaults.DEFAULT_ALARM_TYPE;
        this.label = context.getString(R.string.default_label_name);

        alarmIsRepeating = false;

        isExpanded = true;

        registerAlarm(this.id);

        addAlarmToDatabase();

    }

    private Alarm(Parcel in){

        id = in.readInt();
        timestamp = in.readLong();
        isActive = in.readInt();
        label = in.readString();
        alarmType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeInt(id);
        out.writeLong(timestamp);
        out.writeInt(isActive);
        out.writeString(label);
        out.writeString(alarmType);

    }

    private void addAlarmToDatabase() {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues alarmValues = new ContentValues();

        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID, this.id);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME, this.timestamp);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE, isActive);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING, 1);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE, this.alarmType);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_LABEL, this.label);

        contentResolver.insert(UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI, alarmValues);
    }

    public int deleteAlarmFromDatabase(Alarm alarm) {

        ContentResolver contentResolver = context.getContentResolver();

        Uri contentUri = UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI;
        String selection = UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?";
        String[] selectionArgs = {String.valueOf((int) alarm.getId())};

        return contentResolver.delete(contentUri, selection, selectionArgs);

    }

    public int updateAlarmInDatabase(ContentValues values, String selection, String[] selectionArgs){

        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI;

        int numberRowsUpdated =
                    contentResolver.update(contentUri, values, selection, selectionArgs);

        return numberRowsUpdated;
    }

    public void registerAlarm(int id) {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        alarmIntent.putExtra("alarmPassedInThroughIntent", this);

        pendingAlarmIntent = PendingIntent.getBroadcast(context, id, alarmIntent, 0);

        long wakeUpTime = getCorrectWakeUpTimeStamp(timestamp);

//      Uncomment below line for immediate alarm trigger
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, this.timestamp, pendingAlarmIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingAlarmIntent);
        Log.v(LOG_TAG, "Alarm registered");
    }

    private long getCorrectWakeUpTimeStamp(long timestamp) {

        final long oneMinuteFromNow = getOneMinuteFromNow();

        Log.v(LOG_TAG, "Now " + Utility.getFormattedTime(timestamp));
        Log.v(LOG_TAG, "One minute from now " + Utility.getFormattedTime(oneMinuteFromNow));

        if(timestamp < oneMinuteFromNow){

            long in24HoursTimestamp = getTimestampFor24HoursBasedOnThisTimestamp(timestamp);
            Log.v(LOG_TAG, "'24 hours' from now; " + Utility.getFormattedTime(in24HoursTimestamp));

            return in24HoursTimestamp;

        } else {
            return timestamp;
        }
    }

    private long getTimestampFor24HoursBasedOnThisTimestamp(long timestamp) {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeZone(TimeZone.getDefault());
        calendar2.setTimeInMillis(timestamp);
        int dayOfYear = Utility.getDayOfYearFromTimeStamp(timestamp);
        calendar2.set(Calendar.DAY_OF_YEAR, dayOfYear + 1);

        return calendar2.getTimeInMillis();
    }

    private long getOneMinuteFromNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        long newTimestamp = System.currentTimeMillis();
        calendar.setTimeInMillis(newTimestamp);
        calendar.set(Calendar.MINUTE, Calendar.MINUTE + 1);
        return calendar.getTimeInMillis();
    }

    public void cancelAlarm(){ alarmManager.cancel(pendingAlarmIntent); }


    public void reregisterAlarm(){

        cancelAlarm();
        deleteAlarmFromDatabase(this);
        setId(getId()+1);
        registerAlarm(getId());
        addAlarmToDatabase();

    }

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
    public void registerObserver(AlarmObserver observer) {
        alarmObservers.add(observer);
        Log.v(LOG_TAG, "Snooze Alarm observer added to observer list");
    }

    @Override
    public void removeObserver(AlarmObserver observer) {
        alarmObservers.remove(observer);
        Log.v(LOG_TAG, "Snooze alarm observer removed from observer list");
    }

    @Override
    public void notifyObservers() {
        if(alarmObservers != null) {
            for (AlarmObserver observer : alarmObservers) {
                observer.update();
            }
        }
    }

    enum Days {

        MONDAY(true), TUESDAY(true), WEDNESDAY(true), THURSDAY(true),
        FRIDAY(true), SATURDAY(true), SUNDAY(true);

        boolean alarmOn;

        Days(boolean alarmOn){
            this.alarmOn = alarmOn;
        }

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public void setLabel(String label) { this.label = label; }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

}