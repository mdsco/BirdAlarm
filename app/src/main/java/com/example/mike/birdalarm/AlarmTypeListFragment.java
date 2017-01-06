package com.example.mike.birdalarm;

import android.app.ListFragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlarmTypeListFragment extends ListFragment {


    private String LOG_TAG = AlarmTypeListFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater,
                                    ViewGroup container, Bundle savedInstanceState) {

        View alarmTypeList = inflater.inflate(R.layout.fragment_alarm_type_list, container);

        ArrayList<String> alarmTypes = new ArrayList<>();

        alarmTypes.addAll(getVideosInAssets());

        AlarmTypeArrayAdapter adapter =
                new AlarmTypeArrayAdapter(getActivity(), R.layout.alarm_type_item, alarmTypes);

        setListAdapter(adapter);
        adapter.setList(this);

        return alarmTypeList;
    }

    public List<String> getVideosInAssets(){

        AssetManager assets = getActivity().getResources().getAssets();
        List<String> fileNames;
        List<String> returnedFileNames = new ArrayList<>();

        try {

            String[] list = assets.list("");
            fileNames = Arrays.asList(list);

            for(String file : fileNames){
                if(file.endsWith(".mp4")){
                    returnedFileNames.add(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnedFileNames;

    }

}