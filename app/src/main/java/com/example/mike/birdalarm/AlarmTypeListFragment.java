package com.example.mike.birdalarm;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class AlarmTypeListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                                    ViewGroup container, Bundle savedInstanceState) {

        View alarmTypeList = inflater.inflate(R.layout.fragment_alarm_type_list, container);

        String[] alarms =
                new String[]{ "alarm one", "alarm two", "alarm three", "alarm four", "alarm five"};

        ArrayList<String> alarmTypes = new ArrayList<>();

        alarmTypes.addAll(Arrays.asList(alarms));

        AlarmTypeArrayAdapter adapter =
                new AlarmTypeArrayAdapter(getActivity(), R.layout.alarm_type_item, alarmTypes);

        setListAdapter(adapter);

        return alarmTypeList;
    }

}