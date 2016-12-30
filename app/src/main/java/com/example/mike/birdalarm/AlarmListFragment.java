package com.example.mike.birdalarm;

import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AlarmListFragment extends ListFragment implements AlarmArrayAdapter.Deleter{

    ArrayList<Alarm> alarmItems = new ArrayList<>();
    AlarmArrayAdapter adapter;

    String[] projection = {

            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_LABEL
    };

    private int COL_ALARM_ID = 0;
    private int COL_TIME = 1;
    private int COL_ACTIVE = 2;
    private int COL_REPEATING = 3;
    private int COL_TYPE = 4;
    private int COL_LABEL = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View View = inflater.inflate(R.layout.alarm_list_fragment, container);

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(UserCreatedAlarmContract
                .NewAlarmEntry.CONTENT_URI, projection, null, null, null);

        if(cursor.getCount() > 0){

            fillAlarmItems(cursor);
            sortAlarms(alarmItems);
            setExpandedStateOfAlarmsToFalse(alarmItems);
        }

        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems);
        setListAdapter(adapter);

        cursor.close();

        return View;

    }

    private void fillAlarmItems(Cursor cursor) {

        int count = cursor.getCount();

        if(cursor.moveToFirst()){

            do {

                int alarmId =  cursor.getInt(COL_ALARM_ID);
                long timestamp = cursor.getLong(COL_TIME);
                int active = cursor.getInt(COL_ACTIVE);
                int repeating = cursor.getInt(COL_REPEATING);
                String type = cursor.getString(COL_TYPE);
                String label = cursor.getString(COL_LABEL);

                Calendar calendar = Calendar.getInstance().getInstance();
                calendar.setTime(new Date(timestamp));
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);

                alarmItems.add(new Alarm(getActivity(), hours, minutes, alarmId, timestamp, label));

            } while(cursor.moveToNext());

        }

        cursor.close();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(new OnItemClickListenerListViewItem());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("alarms", alarmItems);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null) {
            alarmItems = savedInstanceState.getParcelableArrayList("alarms");
        }

        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems);
        setListAdapter(adapter);

    }

    public void addAlarm(Context context, int hour, int minute) {

        Alarm alarm = new Alarm(context, hour, minute);

        setExpandedStateOfAlarmsToFalse(alarmItems);

        alarmItems.add(alarm);

        sortAlarms(alarmItems);

        final int alarmPosition = alarmItems.indexOf(alarm);

        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems);
        setListAdapter(adapter);

        ListView listView = (ListView) getActivity().findViewById(android.R.id.list);
        listView.post(new Runnable() {

            @Override
            public void run() {
                ListView listView = getListView();
                listView.smoothScrollToPosition(alarmPosition);
            }

        });

    }

    private void sortAlarms(ArrayList<Alarm> alarmItems) {

        Collections.sort(alarmItems, new Comparator<Alarm>() {
            @Override
            public int compare(Alarm alarmOne, Alarm alarmTwo) {

                if(alarmOne.getHour() < alarmTwo.getHour()){
                    return -1;
                }

                if(alarmOne.getHour() == alarmTwo.getHour()){
                    if(alarmOne.getMinute() < alarmTwo.getMinute()){
                        return -1;
                    }
                }

                return 1;
            }
        });

    }

    private void setExpandedStateOfAlarmsToFalse(ArrayList<Alarm> alarmItems) {
        for (Alarm next : alarmItems) {
            next.setExpandedState(false);
        }
    }

    @Override
    public void deleteThisAlarm(Alarm alarm) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        int deleted = contentResolver.delete(UserCreatedAlarmContract.NewAlarmEntry.CONTENT_URI,
                UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?",
                new String[]{String.valueOf((int) alarm.getId())});

        alarmItems.remove(alarm);
        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems);
        setListAdapter(adapter);

    }
}