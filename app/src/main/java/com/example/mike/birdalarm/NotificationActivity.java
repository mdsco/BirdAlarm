package com.example.mike.birdalarm;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationActivity extends Activity {


    private static String LOG_TAG = NotificationActivity.class.getSimpleName();

    public static final String NOTIFICATION_ID = "NotificationId";
    public static final String IS_SLEEPING = "IsSleeping";

//    String alarmLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = getIntent();
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, -1);
        boolean isSleeping = intent.getBooleanExtra(IS_SLEEPING, false);
        Alarm alarmPassedInThroughIntent = intent.getParcelableExtra("alarmPassedInThroughIntent");

        GlobalState applicationContext = (GlobalState) getApplicationContext();
        Alarm originalAlarm = applicationContext
                                        .getOriginalAlarm(alarmPassedInThroughIntent.getId());

        manager.cancel(notificationId);
        finish();

        if(isSleeping){
            new SnoozeAlarm(getBaseContext(), originalAlarm);
        }
    }

    public static PendingIntent getDismissIntent(int notificationId,
                                                            Context context, Alarm alarm) {

        return getPendingIntent(false, context, notificationId, alarm);
    }

    public static PendingIntent getSleepIntent(Context context, int notificationId, Alarm alarm){

        return getPendingIntent(true, context, notificationId, alarm);
    }

    private static PendingIntent getPendingIntent(boolean isSleepIntent,
                                           Context context, int notificationId, Alarm alarm){

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        intent.putExtra("alarmPassedInThroughIntent", alarm);

        if(isSleepIntent) {
            intent.putExtra(IS_SLEEPING, true);
        }

        int requestCode = (int) System.currentTimeMillis();

        return PendingIntent.getActivity(
                context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    }
}
