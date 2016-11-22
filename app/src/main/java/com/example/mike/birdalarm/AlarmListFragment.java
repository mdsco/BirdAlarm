package com.example.mike.birdalarm;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AlarmListFragment extends ListFragment {

    ArrayList<Alarm> alarmItems = new ArrayList<>();
    AlarmArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

    public void addAlarm(Context context, String hour, String minute) {

        Alarm alarm = new Alarm(context, Integer.valueOf(hour), Integer.valueOf(minute));
        alarmItems.add(alarm);

        adapter = new AlarmArrayAdapter(getActivity(), alarmItems);
        setListAdapter(adapter);

    }

}
