package com.example.mike.birdalarm;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;


public class StoredAlarmProvider extends ContentProvider {

    DatabaseHelper alarmDBHelper;

    @Override
    public boolean onCreate() {

        alarmDBHelper = new DatabaseHelper(getContext());

        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                                        String[] selectionArgs, String sort) {

        SQLiteDatabase readableDatabase = alarmDBHelper.getReadableDatabase();
        Cursor alarmTableCursor = readableDatabase.query(
                UserCreatedAlarmContract.NewAlarmEntry.ALARM_TABLE_NAME,
                projection, selection, selectionArgs, sort, null, null);

        return alarmTableCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase writableDatabase = alarmDBHelper.getWritableDatabase();
        long insertedRow = writableDatabase.insert(
                UserCreatedAlarmContract.NewAlarmEntry.ALARM_TABLE_NAME, null, contentValues);

        Uri returnUri = UserCreatedAlarmContract.NewAlarmEntry.buildAlarmEntryUri(insertedRow);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {

        SQLiteDatabase writableDatabase = alarmDBHelper.getWritableDatabase();
        int deletedRow = writableDatabase.delete(
                UserCreatedAlarmContract.NewAlarmEntry.ALARM_TABLE_NAME, whereClause, whereArgs);

        return deletedRow;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues,
                                            String whereClause, String[] whereArgs) {

        SQLiteDatabase writableDatabase = alarmDBHelper.getWritableDatabase();
        int updatedRow = writableDatabase.update(
                UserCreatedAlarmContract.NewAlarmEntry.ALARM_TABLE_NAME,
                contentValues, whereClause, whereArgs);

        return updatedRow;
    }
}