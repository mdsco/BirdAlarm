package com.example.mike.birdalarm;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class AlarmRecyclerViewFragment extends Fragment implements AlarmRecyclerViewAdapter.Deleter {

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final String LOG_TAG = AlarmRecyclerViewFragment.class.getSimpleName();
    public final static String TAG = "RecyclerViewFragment";

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

    private AlarmRecyclerViewAdapter alarmRecyclerViewAdapter;
    private ArrayList<Alarm> alarmItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private MainActivity activity;
    private RecyclerView.LayoutManager layoutManager;
    private LayoutManagerType currentLayoutManagerType;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataset();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.alarm_recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());

        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            currentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }

        Alarm alarm = new Alarm(getActivity(), 6, 25);
        alarmItems.add(alarm);

        setRecyclerViewLayoutManager(currentLayoutManagerType);
        alarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(activity, alarmItems);
        recyclerView.setAdapter(alarmRecyclerViewAdapter);


//        createAdapterAndSetOnListFragment();

        return rootView;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            alarmItems = savedInstanceState.getParcelableArrayList("alarms");
        }

        createAdapterAndSetOnListFragment();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_LAYOUT_MANAGER, currentLayoutManagerType);
        outState.putParcelableArrayList("alarms", alarmItems);

    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                layoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                layoutManager = new LinearLayoutManager(getActivity());
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                layoutManager = new LinearLayoutManager(getActivity());
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    public void addAlarm(Context context, int hour, int minute) {

        Alarm alarm = new Alarm(context, hour, minute);

        setExpandedStateOfAlarmsToFalse(alarmItems);

        alarmItems.add(alarm);

        updateAlarmListInGlobalSpace(context);

        sortAlarms(alarmItems);

        final int alarmPosition = alarmItems.indexOf(alarm);

        createAdapterAndSetOnListFragment();

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(alarmPosition);
            }
        });

    }

    private void createAdapterAndSetOnListFragment() {

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = new RecyclerView(getActivity());
        alarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(activity, alarmItems);
        recyclerView.setAdapter(alarmRecyclerViewAdapter);
        setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);

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

    private void initDataset() {

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(UserCreatedAlarmContract
                .NewAlarmEntry.CONTENT_URI, projection, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {

            fillAlarmItems(cursor);
            sortAlarms(alarmItems);
            setExpandedStateOfAlarmsToFalse(alarmItems);
            cursor.close();

        }
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

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    private static void setExpandedStateOfAlarmsToFalse(ArrayList<Alarm> alarmItems) {
        for (Alarm next : alarmItems) {
            next.setExpandedState(false);
        }
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

    private void updateAlarmListInGlobalSpace(Context context) {
        GlobalState applicationContext = (GlobalState) context.getApplicationContext();
        applicationContext.setAlarmList(alarmItems);
    }


    public ArrayList<Alarm> getAlarmItems() {
        return alarmItems;
    }
    public void setAlarmItems(ArrayList<Alarm> alarmItems) {
        this.alarmItems = alarmItems;
    }

}