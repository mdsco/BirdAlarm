package com.example.mike.birdalarm;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AlarmTypeListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String[] alarms =
                new String[]{ "alarm one", "alarm two", "alarm three", "alarm four", "alarm five"};

        ListView alarmTypeList = (ListView) inflater.inflate(R.layout.fragment_alarm_type_list, container);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), R.layout.alarm_type_item, alarms);

        alarmTypeList.setAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
