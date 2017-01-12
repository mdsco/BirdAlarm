package com.example.mike.birdalarm;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class UserCreatedAlarmContract {


    private String LOG_TAG = UserCreatedAlarmContract.class.getSimpleName();

    private static final String AUTHORITY = "com.example.mike.birdalarm";
    private static final String PATH_ALARM = "alarms";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class NewAlarmEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALARM).build();

        public final static String ALARM_TABLE_NAME = "user_alarm";
        public final static String COLUMN_ALARM_ID = "alarm_id";
        public final static String COLUMN_ALARM_TIME = "alarm_time";
        public final static String COLUMN_ACTIVE = "is_active";
        public final static String COLUMN_REPEATING = "is_repeating";
        public final static String COLUMN_DAYS_ACTIVE = "days_active";
        public final static String COLUMN_ALARM_TYPE = "alarm_type";
        public final static String COLUMN_LABEL = "alarm_label";

        public static Uri buildAlarmEntryUri(long id){

            Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);

            return uri;
        }

    }
}
