package com.example.mike.birdalarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int databaseVersion = 1;

    private static final String ALARM_DATABASE = "alarms.db";

    public DatabaseHelper(Context context){
        super(context, ALARM_DATABASE, null, databaseVersion);



    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_USER_ALARM_TABLE = "CREATE TABLE " +
                UserCreatedAlarmContract.NewAlarmEntry.ALARM_TABLE_NAME + "(" +
                UserCreatedAlarmContract.NewAlarmEntry._ID + " INTEGER PRIMARY KEY," +
                UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME + " REAL NOT NULL," +
                UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE + " INTEGER NOT NULL," +
                UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING + " INTEGER NOT NULL," +
                UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE + " TEXT NOT NULL);";
        System.out.println(CREATE_USER_ALARM_TABLE);

        sqLiteDatabase.execSQL(CREATE_USER_ALARM_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {



    }
}
