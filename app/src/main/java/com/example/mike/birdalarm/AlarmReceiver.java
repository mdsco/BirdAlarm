package com.example.mike.birdalarm;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private String LOG_TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Alarm alarmPassedInThroughIntent =
                intent.getExtras().getParcelable("alarmPassedInThroughIntent");

        long timestamp = alarmPassedInThroughIntent.getTimestamp();
        String time = Utility.getFormattedTime(timestamp);

        KeyguardManager keyguardManager =
                (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean locked = keyguardManager.inKeyguardRestrictedInputMode();

        PowerManager powerManager =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean screenOn;

        if (Build.VERSION.SDK_INT >= 20) {
            screenOn = powerManager.isInteractive();
        } else {
            screenOn = powerManager.isScreenOn();
        }

        if (locked || !screenOn) {

            Intent alarmIntent =
                    new Intent(context, AlarmLockScreenTextureViewVideoActivity.class);
            alarmIntent.putExtra("alarmPassedInThroughIntent", alarmPassedInThroughIntent);

            alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(alarmIntent);

        } else {
            createPopUpNotification(context, time, alarmPassedInThroughIntent);
//                playAlarmSound(context);

        }
    }

    private void createPopUpNotification(Context context, String time, Alarm alarm) {

        final int mNotificationId = (int) System.currentTimeMillis();

        PendingIntent dismissIntent =
                NotificationActivity.getDismissIntent(mNotificationId, context, alarm);

        PendingIntent sleepIntent =
                NotificationActivity.getSleepIntent(context, mNotificationId, alarm);

        //TODO - Decide what notification sound is going to be/Figure out how to make the alarm persist
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(alarm.getLabel())
                        .setContentText(time)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                        .setVibrate(VibrationUtility.getVibrationPattern(alarm.getVibrate()))
                        .setPriority(Notification.PRIORITY_MAX)
                        .addAction(0, "Cancel", dismissIntent)
                        .addAction(0, "Sleep", sleepIntent);

        NotificationManager mNotifyManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_INSISTENT;

        mNotifyManager.notify(mNotificationId, notification);

    }

    private void playAlarmSound(Context context) {

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();

    }
}