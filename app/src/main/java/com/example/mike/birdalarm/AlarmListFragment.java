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

public class AlarmListFragment extends ListFragment implements AlarmArrayAdapter.Deleter,
        AlarmArrayAdapter.ExpandCollapseListener {

    private String LOG_TAG = AlarmListFragment.class.getSimpleName();

    private MainActivity activity;
    private ArrayList<Alarm> alarmItems = new ArrayList<>();

    AlarmArrayAdapter adapter;

    String[] projection = {

            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TIME,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_DAYS_ACTIVE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_VIBRATE,
            UserCreatedAlarmContract.NewAlarmEntry.COLUMN_LABEL

    };

    private int COL_ALARM_ID = 0;
    private int COL_TIME = 1;
    private int COL_ACTIVE = 2;
    private int COL_REPEATING = 3;
    private int COL_DAYS_ACTIVE = 4;
    private int COL_TYPE = 5;
    private int COL_VIBRATE = 6;
    private int COL_LABEL = 7;

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

        if (cursor != null && cursor.getCount() > 0) {

            fillAlarmItems(cursor);
            sortAlarms(alarmItems);
            setExpandedStateOfAlarmsToFalse(alarmItems);
            cursor.close();
        }

        createAdapterAndSetOnListFragment();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            for (Alarm alarm : alarmItems) {
                if (resultCode == alarm.getId()) {
                    alarm.setAlarmType(data.getStringExtra("NewBirdAlarm"));
                }
            }
        } else if (resultCode == RESULT_CANCELED) {}

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ExpandingListView listView = (ExpandingListView) getListView();
//        getListView().setOnItemClickListener(new OnItemClickListenerListViewItem());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("alarms", alarmItems);


    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            alarmItems = savedInstanceState.getParcelableArrayList("alarms");
        }

        createAdapterAndSetOnListFragment();

    }

    private void fillAlarmItems(Cursor cursor) {

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

                alarmItems.add(new Alarm(getActivity(), alarmId, timestamp, active, days,
                        alarmIsRepeating, label, alarmType, vibrateBool));

                updateAlarmListInGlobalSpace(getActivity());

            } while (cursor.moveToNext());

        }

        cursor.close();

    }

    private void createAdapterAndSetOnListFragment() {

        adapter = new AlarmArrayAdapter(getActivity(), this, alarmItems, activity);

        setListAdapter(adapter);

//        ListView listView = getListView();
//        ((ExpandingListView)this.getListView()).setAdapter(adapter);

    }

    public void addAlarm(Context context, int hour, int minute) {

        Alarm alarm = new Alarm(context, hour, minute);

        setExpandedStateOfAlarmsToFalse(alarmItems);

        alarmItems.add(alarm);

        updateAlarmListInGlobalSpace(context);

        sortAlarms(alarmItems);

        final int alarmPosition = alarmItems.indexOf(alarm);

        createAdapterAndSetOnListFragment();

        ListView listView = (ListView) getActivity().findViewById(android.R.id.list);
        listView.post(new Runnable() {

            @Override
            public void run() {
                ListView listView = getListView();
                listView.smoothScrollToPosition(alarmPosition);
            }

        });

    }

    private void updateAlarmListInGlobalSpace(Context context) {
        GlobalState applicationContext = (GlobalState) context.getApplicationContext();
        applicationContext.setAlarmList(alarmItems);
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


                    if (alarmOneHourFromTimeStamp < alarmTwoHourFromTimeStamp) {
                        return -1;
                    }

                    if (alarmOneHourFromTimeStamp == alarmTwoHourFromTimeStamp) {
                        if (alarmOneMinuteFromTimeStamp < alarmTwoMinuteFromTimeStamp) {
                            return -1;
                        }
                    }

                    return 1;

            }
        });

    }

    public static void setExpandedStateOfAlarmsToFalse(ArrayList<Alarm> alarmItems) {
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
        updateAlarmListInGlobalSpace(getActivity());

        //reload list view
        createAdapterAndSetOnListFragment();

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

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void listItemCollapsed(Alarm alarm) {

        setExpandedStateOfAlarmsToFalse(alarmItems);

        alarm.setExpandedState(true);

        createAdapterAndSetOnListFragment();

    }
}
