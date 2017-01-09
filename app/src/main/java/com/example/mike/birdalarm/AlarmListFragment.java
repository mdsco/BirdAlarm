package com.example.mike.birdalarm;

import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AlarmListFragment extends ListFragment implements AlarmArrayAdapter.Deleter{


    private String LOG_TAG = AlarmListFragment.class.getSimpleName();

    private ArrayList<Alarm> alarmItems = new ArrayList<>();
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

        View view = inflater.inflate(R.layout.alarm_list_fragment, container);

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(UserCreatedAlarmContract
                .NewAlarmEntry.CONTENT_URI, projection, null, null, null);

        if(cursor != null && cursor.getCount() > 0){

            fillAlarmItems(cursor);
            sortAlarms(alarmItems);
            setExpandedStateOfAlarmsToFalse(alarmItems);
        }

        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems);
        setListAdapter(adapter);

        cursor.close();

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            for(Alarm alarm : alarmItems) {
                if(resultCode == alarm.getId()) {
                    alarm.setAlarmType(data.getStringExtra("NewBirdAlarm"));
                }
            }
        } else if(resultCode == RESULT_CANCELED){
            return;
        }

    }

    private void fillAlarmItems(Cursor cursor) {

        if(cursor.moveToFirst()){

            do {

                int alarmId =  cursor.getInt(COL_ALARM_ID);
                long timestamp = cursor.getLong(COL_TIME);
                int active = cursor.getInt(COL_ACTIVE);
                int repeating = cursor.getInt(COL_REPEATING);
                String alarmType = cursor.getString(COL_TYPE);
                String label = cursor.getString(COL_LABEL);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(timestamp));

                alarmItems.add(new Alarm(getActivity(),
                                            alarmId, timestamp, active, label, alarmType));



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

        GlobalState applicationContext = (GlobalState) context.getApplicationContext();
        applicationContext.setAlarmList(alarmItems);

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

                long alarmOneTimestamp = alarmOne.getTimestamp();
                long alarmTwoTimestamp = alarmTwo.getTimestamp();

                int alarmOneHourFromTimeStamp = Utility.getHourFromTimeStamp(alarmOneTimestamp);
                int alarmTwoHourFromTimeStamp = Utility.getHourFromTimeStamp(alarmTwoTimestamp);

                int alarmOneMinuteFromTimeStamp = Utility.getMinuteFromTimeStamp(alarmOneTimestamp);
                int alarmTwoMinuteFromTimeStamp = Utility.getMinuteFromTimeStamp(alarmTwoTimestamp);


                if(alarmOneHourFromTimeStamp < alarmTwoHourFromTimeStamp){
                    return -1;
                }

                if(alarmOneHourFromTimeStamp == alarmTwoHourFromTimeStamp){
                    if(alarmOneMinuteFromTimeStamp < alarmTwoMinuteFromTimeStamp){
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

        //!!!determine if vertical offset needs to exist, toast currently overlaps add alarm button
        float toastVerticalOffset = 0.05f;


        alarm.cancelAlarm();
        //remove alarm from database
        alarm.notifyObservers();
        alarm.deleteAlarmFromDatabase(alarm);
        //remove from list of alarms to be added to listview
        alarmItems.remove(alarm);
        GlobalState applicationContext = (GlobalState) getActivity().getApplicationContext();
        applicationContext.setAlarmList(alarmItems);

        //reload list view
        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems);
        setListAdapter(adapter);

        Toast toast = Toast.makeText(getActivity(), Utility.getFormattedTime(alarm.getTimestamp())
                + Utility.getAmOrPm(alarm.getTimestamp()).toLowerCase()
                + " Alarm Removed", Toast.LENGTH_SHORT);
        toast.setMargin(toast.getHorizontalMargin(), toast.getVerticalMargin() + toastVerticalOffset);
        toast.show();

    }

    public ArrayList<Alarm> getAlarmItems() {
        return alarmItems;
    }

    public void setAlarmItems(ArrayList<Alarm> alarmItems) {
        this.alarmItems = alarmItems;
    }
}