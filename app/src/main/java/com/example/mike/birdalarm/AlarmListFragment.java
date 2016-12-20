package com.example.mike.birdalarm;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class AlarmListFragment extends ListFragment {

    ArrayList<Alarm> alarmItems = new ArrayList<>();
    AlarmArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View View = inflater.inflate(R.layout.alarm_list_fragment, container);

        adapter = new AlarmArrayAdapter(getActivity(), alarmItems);
        setListAdapter(adapter);

        return View;

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

        adapter = new AlarmArrayAdapter(getActivity(), alarmItems);
        setListAdapter(adapter);

    }

    public void addAlarm(Context context, String hour, String minute) {

        Alarm alarm = new Alarm(context, Integer.valueOf(hour), Integer.valueOf(minute));

        inactivateAllOtherAlarms(alarmItems);

        alarmItems.add(alarm);

        sortAlarms(alarmItems);

        adapter = new AlarmArrayAdapter(getActivity(), alarmItems);
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

        Iterator<Alarm> iterator = alarmItems.iterator();
        while(iterator.hasNext()) {

            Alarm next = iterator.next();
            next.setExpandedState(false);
        }
    }
}