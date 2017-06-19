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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

class Alarm implements Parcelable, Subject, OnSizeChangedListener {

    private static final String LOG_TAG = Alarm.class.getSimpleName();
    private final int CELL_DEFAULT_HEIGHT = 375;

    public Context context = null;

    private int id;
    private long timestamp;

    private int isActive;

    private String label;
    private String alarmType;
    private boolean vibrate;

    private boolean alarmIsRepeating;
    private Days[] days = {};
    private boolean isExpanded;

    private AlarmManager alarmManager;
    private PendingIntent pendingAlarmIntent;
    private ArrayList<AlarmObserver> alarmObservers;

    private int mCollapsedHeight = CELL_DEFAULT_HEIGHT;
    private int mExpandedHeight = -1;

    Alarm(Context context, int alarmId, long timestamp, int active,
          String days, boolean repeating, String label, String alarmType, boolean vibrate) {

        this.context = context;

        this.id = alarmId;
        this.timestamp = timestamp;

        this.isActive = active;

        this.days = getDaysArrayFromDaysString(days);

        this.label = label;
        this.alarmType = alarmType;
        this.vibrate = vibrate;

        alarmIsRepeating = repeating;

        isExpanded = true;

        registerAlarm(this.id);

    }

    Alarm(Context context, int hour, int minute) {

        alarmObservers = new ArrayList<>();

        this.context = context;

        this.timestamp = Utility.getTimeStampFromHourAndMinute(hour, minute);

        long wakeUpTime = getCorrectWakeUpTimeStamp(timestamp);
        setTimestamp(wakeUpTime);

        this.id = (int) this.timestamp;

        this.isActive = 1;

        days = new Days[1];

        String dayOfWeekString = Utility.getDayOfWeekFromTimeStampAsString(timestamp);

        days[0] = Days.getDaysEnumBasedOnString(dayOfWeekString);

        this.alarmType = Defaults.DEFAULT_ALARM_TYPE;
        this.vibrate = false;
        this.label = context.getString(R.string.default_label_name);

        alarmIsRepeating = false;

        isExpanded = true;

        registerAlarm(this.id);

        addAlarmToDatabase();

    }

    private Alarm(Parcel in) {

        id = in.readInt();
        timestamp = in.readLong();
        isActive = in.readInt();
        label = in.readString();
        alarmType = in.readString();
        vibrate = (Boolean) in.readValue(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeInt(id);
        out.writeLong(timestamp);
        out.writeInt(isActive);
        out.writeString(label);
        out.writeString(alarmType);
        out.writeValue(vibrate);

    }

    private void addAlarmToDatabase() {

        ContentResolver contentResolver = context.getContentResolver();

        ContentValues alarmValues = new ContentValues();

        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID, this.id);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME, this.timestamp);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE, isActive);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING, alarmIsRepeating ? 1 : 0);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_DAYS_ACTIVE, getDaysStringFromString(days));
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE, this.alarmType);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_VIBRATE, this.vibrate ? 1 : 0);
        alarmValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_LABEL, this.label);

        contentResolver.insert(UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI, alarmValues);
    }

    static String getDaysStringFromString(Days[] days) {

        String daysString = "";

        for (Days day : days) {
            daysString += day.toString() + ",";
        }

        return daysString;
    }

    int deleteAlarmFromDatabase(Alarm alarm) {

        ContentResolver contentResolver = context.getContentResolver();

        Uri contentUri = UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI;
        String selection = UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?";
        String[] selectionArgs = {String.valueOf(alarm.getId())};

        return contentResolver.delete(contentUri, selection, selectionArgs);

    }

    int updateAlarmInDatabase(ContentValues values, String selection, String[] selectionArgs) {

        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI;

        return contentResolver.update(contentUri, values, selection, selectionArgs);
    }

    private void registerAlarm(int id) {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        alarmIntent.putExtra("alarmPassedInThroughIntent", this);

        pendingAlarmIntent = PendingIntent.getBroadcast(context, id, alarmIntent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingAlarmIntent);
    }


    void setTimestampBasedOnNextViableDay() {

        long newTimestamp = NewAlarmTimeStampProvider
                                .getNextAlarmTimestamp(timestamp, days, alarmIsRepeating);

        if(newTimestamp != -1) {

            setTimestamp(newTimestamp);
            reregisterAlarm();

            Log.v(LOG_TAG, "New timestamp registered for: " + Utility.getFormattedTime(timestamp));

        }

        if(newTimestamp == -1) {

            cancelAlarm();
            Log.v(LOG_TAG, "Alarm cancelled ");
        }

    }

    private long getCorrectWakeUpTimeStamp(long timestamp) {

        final long oneMinuteFromNow = System.currentTimeMillis();

        if (timestamp <= oneMinuteFromNow) {
            return getTimestampFor24HoursBasedOnThisTimestamp(timestamp);
        } else {
            return timestamp;
        }
    }

    private long getTimestampFor24HoursBasedOnThisTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(timestamp);
        int dayOfYear = Utility.getDayOfYearFromTimeStamp(timestamp);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear + 1);

        return calendar.getTimeInMillis();
    }

    void cancelAlarm() {
        alarmManager.cancel(pendingAlarmIntent);
    }

    void reregisterAlarm() {

        cancelAlarm();
        deleteAlarmFromDatabase(this);
        int newId = getId() + 1;
        setId(newId);
        registerAlarm(getId());
        addAlarmToDatabase();

    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {

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
    }

    @Override
    public void removeObserver(AlarmObserver observer) {
        alarmObservers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        if (alarmObservers != null) {
            for (AlarmObserver observer : alarmObservers) {
                observer.update();
            }
        }
    }

    Days[] getDays() {

        return days;
    }

    void setDays(Days[] days) {
        this.days = days;
    }

    boolean getVibrate() {
        return vibrate;
    }

    void setVibrateToOpposite() {
        vibrate = !vibrate;
    }

    enum Days {

        SUNDAY(false, "Sunday"), MONDAY(true, "Monday"), TUESDAY(false, "Tuesday"),
        WEDNESDAY(false, "Wednesday"), THURSDAY(false, "Thursday"),
        FRIDAY(false, "Friday"), SATURDAY(false, "Saturday");

        boolean alarmOn;

        private final String dayName;

        Days(boolean alarmOn, String dayName) {

            this.alarmOn = alarmOn;
            this.dayName = dayName;

        }

        public static Days getDaysEnumBasedOnString(String dayString) {

            switch (dayString) {

                case "Sunday":
                    return SUNDAY;
                case "Monday":
                    return MONDAY;
                case "Tuesday":
                    return TUESDAY;
                case "Wednesday":
                    return WEDNESDAY;
                case "Thursday":
                    return THURSDAY;
                case "Friday":
                    return FRIDAY;
                case "Saturday":
                    return SATURDAY;
                default:
                    return SUNDAY;
            }
        }

        public String getDayName() {
            return dayName;
        }

        public boolean getAlarmOn() {
            return alarmOn;
        }

        public void setAlarmOn(boolean alarmOn) {
            this.alarmOn = alarmOn;
        }

        public void setDayAlarmOnOrOff(Days day) {
            day.alarmOn = !day.alarmOn;
        }

        @Override
        public String toString() {
            return this.dayName;
        }

    }

    private Days[] getDaysArrayFromDaysString(String days) {

        String[] daysStringArray = days.split(",");

        Days[] daysEnumArray = new Days[daysStringArray.length];

        for (int i = 0; i < daysStringArray.length; i++) {

            switch (daysStringArray[i]) {

                case "Monday":
                    daysEnumArray[i] = Days.MONDAY;
                    break;
                case "Tuesday":
                    daysEnumArray[i] = Days.TUESDAY;
                    break;
                case "Wednesday":
                    daysEnumArray[i] = Days.WEDNESDAY;
                    break;
                case "Thursday":
                    daysEnumArray[i] = Days.THURSDAY;
                    break;
                case "Friday":
                    daysEnumArray[i] = Days.FRIDAY;
                    break;
                case "Saturday":
                    daysEnumArray[i] = Days.SATURDAY;
                    break;
                case "Sunday":
                    daysEnumArray[i] = Days.SUNDAY;
                    break;
                default:
                    break;

            }

        }

        return daysEnumArray;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    long getTimestamp() {
        return timestamp;
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    boolean isAlarmIsRepeating() {
        return alarmIsRepeating;
    }

    void setAlarmIsRepeating(boolean alarmIsRepeating) {
        this.alarmIsRepeating = alarmIsRepeating;
    }

    boolean isExpanded() {
        return isExpanded;
    }

    void setExpandedState(boolean activeOrNot) {
        isExpanded = activeOrNot;
    }

    String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    String getAlarmType() {
        return alarmType;
    }

    void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    int getIsActive() {
        return isActive;
    }

    void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getCollapsedHeight() {
        return mCollapsedHeight;
    }

    public void setCollapsedHeight(int collapsedHeight) {
        mCollapsedHeight = collapsedHeight;
    }

    public int getExpandedHeight() {
        return mExpandedHeight;
    }

    public void setExpandedHeight(int expandedHeight) {
        mExpandedHeight = expandedHeight;
    }

    @Override
    public void onSizeChanged(int newHeight) {
        setExpandedHeight(newHeight);
    }
}