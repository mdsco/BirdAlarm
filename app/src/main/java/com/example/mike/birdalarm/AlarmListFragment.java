package com.example.mike.birdalarm;

import android.app.ListFragment;
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

        View view = inflater.inflate(R.layout.list_fragment, container);

        adapter = new AlarmArrayAdapter(getActivity(), R.layout.item, alarmItems);
        setListAdapter(adapter);

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(new OnItemClickListenerListViewItem());

    }

    public void addAlarm(String hour, String minute) {

        Alarm alarm = new Alarm(Integer.valueOf(hour), Integer.valueOf(minute));
        alarmItems.add(alarm);

        adapter = new AlarmArrayAdapter(getActivity(), R.layout.item, alarmItems);
        setListAdapter(adapter);

    }

}
