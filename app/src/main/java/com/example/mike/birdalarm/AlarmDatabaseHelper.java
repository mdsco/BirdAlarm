package com.example.mike.birdalarm;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mike on 6/17/17.
 */

public class AlarmDatabaseHelper {

    static String[] projection = {

            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_DAYS_ACTIVE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_VIBRATE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_LABEL

    };

    private static int COL_ALARM_ID = 0;
    private static int COL_TIME = 1;
    private static int COL_ACTIVE = 2;
    private static int COL_REPEATING = 3;
    private static int COL_DAYS_ACTIVE = 4;
    private static int COL_TYPE = 5;
    private static int COL_VIBRATE = 6;
    private static int COL_LABEL = 7;

    public static ArrayList<Alarm> getAlarmItemsFromDatabase(Context context, boolean register) {

        ArrayList<Alarm> alarmItems = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(UserCreatedAlarmContract
                .NewAlarmEntry.CONTENT_URI, projection, null, null, null);

        if (cursor.moveToFirst()) {

            do {

                int alarmId = cursor.getInt(COL_ALARM_ID);
                long timestamp = cursor.getLong(COL_TIME);
                int active = cursor.getInt(COL_ACTIVE);
                int repeating = cursor.getInt(COL_REPEATING);
                String days = cursor.getString(COL_DAYS_ACTIVE);
                String alarmType = cursor.getString(COL_TYPE);
                int vibrate = cursor.getInt(COL_VIBRATE);
                String label = cursor.getString(COL_LABEL);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(timestamp));

                boolean alarmIsRepeating = repeating == 1;

                boolean vibrateBool = vibrate == 1;

                alarmItems.add(new Alarm(context, alarmId, timestamp, active, days,
                        alarmIsRepeating, label, alarmType, vibrateBool, register));


            } while (cursor.moveToNext());

        }

        cursor.close();

        return alarmItems;

    }

}
