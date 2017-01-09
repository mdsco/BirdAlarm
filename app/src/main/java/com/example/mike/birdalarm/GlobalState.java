package com.example.mike.birdalarm;

import android.app.Application;

import java.util.ArrayList;


public class GlobalState extends Application {

    ArrayList<Alarm> alarmList = new ArrayList<Alarm>();

    public ArrayList<Alarm> getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(ArrayList<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    public Alarm getOriginalAlarm(int id) {

        Alarm originalAlarm = null;

        for (Alarm alarmInList: alarmList) {
            if(alarmInList.getId() == id){
                originalAlarm = alarmInList;
            }
        }

        return originalAlarm;
    }

}