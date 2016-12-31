package com.example.mike.birdalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class Alarm implements Parcelable {

    private static final String LOG_TAG = Alarm.class.getSimpleName();

    private long timestamp;
    public Context context = null;

    private int id;
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

    Alarm (Context context, int hour, int minute, int alarmId, long timestamp, String label){

        this.context = context;

        this.id = alarmId;
        this.timestamp = timestamp;

        int hourFromTimeStamp = Utility.getHourFromTimeStamp(this.timestamp);

        this.hour = Utility.getHourFor12HourClock(hourFromTimeStamp);
        this.minute = Utility.getMinuteFromTimeStamp(this.timestamp);
        this.aMpM = Utility.determineIfAmOrPm(this.timestamp);

        this.label = label;

        alarmIsRepeating = false;

        isExpanded = true;

        registerAlarm(this.id);
    }

    Alarm (Context context, int hour, int minute){

        this.context = context;

        this.timestamp = Utility.getTimeStampFromHourAndMinute(hour, minute);
        this.id = (int) this.timestamp;

        this.hour = Utility.getHourFor12HourClock(Utility.getHourFromTimeStamp(timestamp));
        this.minute = Utility.getMinuteFromTimeStamp(timestamp);
        this.aMpM = setAmPm(timestamp);

        this.label = context.getString(R.string.default_label_name);

        alarmIsRepeating = false;

        isExpanded = true;

        registerAlarm(this.id);

        ContentResolver contentResolver = context.getContentResolver();

        ContentValues alarmValues = new ContentValues();

        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID, this.id);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME, this.timestamp);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE, 1);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING, 1);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE, "robin");
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_LABEL, this.label);

        contentResolver.insert(UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI, alarmValues);

    }

    private Alarm(Parcel in){

        id = in.readInt();
        timestamp = in.readLong();
        hour = in.readInt();
        minute = in.readInt();
        label = in.readString();
    }

    public void registerAlarm(int id) {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        alarmIntent.putExtra("alarmPassedInThroughIntent", this);

        pendingAlarmIntent = PendingIntent.getBroadcast(context, id, alarmIntent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, this.timestamp, pendingAlarmIntent);

    }

    public void cancelAlarm(){
        alarmManager.cancel(pendingAlarmIntent);
    }

    private boolean setAmPm(long timestamp){ return Utility.determineIfAmOrPm(timestamp); }

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

        out.writeInt(id);
        out.writeLong(timestamp);
        out.writeInt(hour);
        out.writeInt(minute);
        out.writeString(label);

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