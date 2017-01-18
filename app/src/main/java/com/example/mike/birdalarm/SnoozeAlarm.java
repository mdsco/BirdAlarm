package com.example.mike.birdalarm;

import android.content.Context;
import android.util.Log;


public class SnoozeAlarm extends Alarm implements AlarmObserver {

    Alarm originalAlarm;

    private static final String LOG_TAG2 = SnoozeAlarm.class.getSimpleName();

    SnoozeAlarm(Context context, Alarm originalAlarm){
        super(context, originalAlarm.getId(), Utility.getTimeStampForAlarmSleep(context),
                                originalAlarm.getIsActive(),
                                Alarm.getDaysStringFromString(originalAlarm.getDays()), originalAlarm.isAlarmIsRepeating(),
                                originalAlarm.getLabel(), originalAlarm.getAlarmType(), originalAlarm.getVibrate());

        this.originalAlarm = originalAlarm;
        this.originalAlarm.registerObserver(this);

    }

    @Override
    public void update() {
        //TODO
        //Currently only is used for cancelling the registered snooze alarm
        //may need to add functionality for the case where a snooze is reregistered
        //if the snooze pref is updated after snooze button pressed but before the
        //alarm triggers

        cancelAlarm();

        this.originalAlarm.removeObserver(this);

    }
}
