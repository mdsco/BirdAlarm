package com.example.mike.birdalarm;

import android.content.Intent;

public class Alarm {

    private boolean aMpM;
    private int hour;
    private int minute;
    private boolean alarmIsRepeating;
    private Days[] days = {Days.MONDAY, Days.TUESDAY, Days.WEDNESDAY, Days.THURSDAY, Days.FRIDAY, Days.SATURDAY, Days.SUNDAY};

    public Alarm (int hour, int minute){

        this.hour = getCorrectHour(hour);
        this.minute = minute;
        this.aMpM = setAmPm(hour);

        alarmIsRepeating = false;

    }

    public int getCorrectHour(int hour){

        if(hour == 0){
            return 12;
        } else if(hour > 12){
            return hour - 12;
        }

        return hour;
    }

    public boolean setAmPm(int hour){

        if(hour < 12 || hour == 24){
            return true;
        } else {
            return false;
        }

    }

    public void setDayAlarmOnOrOff(Days day){
        day.alarmOn = day.alarmOn ? false : true;
    }

    enum Days {

        MONDAY(true), TUESDAY(true), WEDNESDAY(true), THURSDAY(true), FRIDAY(true), SATURDAY(true), SUNDAY(true);

        boolean alarmOn;

        Days(boolean alarmOn){
            this.alarmOn = alarmOn;
        }

    }

    public String getaMpM() { return aMpM == true ? "AM" : "PM"; }

    public int getHour() { return hour; }

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
}