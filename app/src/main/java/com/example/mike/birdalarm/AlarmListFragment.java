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

            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE
    };

    private int COL_TIME = 0;
    private int COL_ACTIVE = 1;
    private int COL_REPEATING = 2;
    private int COL_TYPE = 3;



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

        if(cursor == null){

            Log.v("I gue: ", "This didn't work");
        }

        Log.v("I guess: ", "This did work!");

        fillAlarmItems(cursor);

        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems);
        setListAdapter(adapter);

        return View;

    }

    private void fillAlarmItems(Cursor cursor) {

        if(cursor.moveToFirst()){

            do {
                long timestamp = cursor.getLong(0);
                int active = cursor.getInt(1);
                int repeating = cursor.getInt(2);
                String type = cursor.getString(3);

                Calendar calendar = Calendar.getInstance().getInstance();
                calendar.setTime(new Date(timestamp));
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);

                alarmItems.add(new Alarm(getActivity(), hours, minutes));
            } while(cursor.moveToNext());
        }

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

    public void addAlarm(Context context, String hour, String minute) {

        Alarm alarm = new Alarm(context, Integer.valueOf(hour), Integer.valueOf(minute));

        inactivateAllOtherAlarms(alarmItems);

        alarmItems.add(alarm);

        sortAlarms(alarmItems);

        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems);
        setListAdapter(adapter);

        ListView listView = (ListView) getActivity().findViewById(android.R.id.list);
        listView.post(new Runnable() {

            @Override
            public void run() {
                ListView listView = getListView();
                listView.smoothScrollToPosition(Alarm.id);
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

    private void inactivateAllOtherAlarms(ArrayList<Alarm> alarmItems) {
        for (Alarm next : alarmItems) {
            next.setExpandedState(false);
        }
    }

    @Override
    public void deleteThisAlarm(Alarm alarm) {
        alarmItems.remove(alarm);
        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems);
        setListAdapter(adapter);
    }
}