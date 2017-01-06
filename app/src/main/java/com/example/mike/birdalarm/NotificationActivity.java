package com.example.mike.birdalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NotificationActivity extends Activity {

    public static final String NOTIFICATION_ID = "NotificationId";
    public static final String IS_SLEEPING = "IsSleeping";

    String alarmLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = getIntent();
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, -1);
        alarmLabel = intent.getStringExtra("Label");

        manager.cancel(notificationId);
        finish();

        if(intent.getBooleanExtra(IS_SLEEPING, false)){
            registerAlarm();
        }
    }

    public static PendingIntent getDismissIntent(int notificationId, Context context, String label) {

        return getPendingIntent(false, context,notificationId, label);
    }

    public static PendingIntent getSleepIntent(Context context, int notificationId, String label){

        return getPendingIntent(true, context, notificationId, label);
    }

    private static PendingIntent getPendingIntent(boolean isSleepIntent, Context context, int id, String label){

        Intent sleepIntent = new Intent(context, NotificationActivity.class);
        sleepIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sleepIntent.putExtra(NOTIFICATION_ID, id);
        sleepIntent.putExtra("Label", label);

        if(isSleepIntent) {
            sleepIntent.putExtra(IS_SLEEPING, true);
        }

        int requestCode = (int) System.currentTimeMillis();

        return PendingIntent.getActivity(
                context, requestCode, sleepIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void registerAlarm() {

        long timeStampForAlarmSleep = Utility.getTimeStampForAlarmSleep();

        Context context = getBaseContext();

        AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("Time", Utility.getFormattedTime(timeStampForAlarmSleep));
        alarmIntent.putExtra("Label", alarmLabel);

        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingAlarmIntent =
                PendingIntent.getBroadcast(context, requestCode, alarmIntent, 0);

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP, timeStampForAlarmSleep, pendingAlarmIntent);

    }
}
