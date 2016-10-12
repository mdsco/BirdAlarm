package com.example.mike.birdalarm;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class AlarmListFragment extends ListFragment {

//    ArrayList<String> items = new ArrayList<String>();
      ArrayList<Alarm> alarmItems = new ArrayList<Alarm>();
//    ArrayAdapter<String> adapter;
      AlarmArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

//        items = new ArrayList<String>();
//
//        adapter = new ArrayAdapter<String>(
//                getActivity(), android.R.layout.simple_list_item_1, items);
//
//        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment, null);

//        adapter = new ArrayAdapter<String>(
//                getActivity(), R.layout.item, items);



        adapter = new AlarmArrayAdapter(getActivity(), R.layout.list_fragment, alarmItems);

        setListAdapter(adapter);

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    public void addAlarm(String hour, String minute){

        Alarm alarm = new Alarm(new Integer(hour), new Integer(minute));
        alarmItems.add(alarm);
//        adapter = new ArrayAdapter<String>(
//                getActivity(), R.layout.item, items);

        adapter = new AlarmArrayAdapter(getActivity(), R.layout.list_fragment, alarmItems);

        setListAdapter(adapter);

    }

}
