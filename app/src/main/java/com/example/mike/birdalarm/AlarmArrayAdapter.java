package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 10/11/16.
 */

public class AlarmArrayAdapter extends ArrayAdapter<Alarm> {


    private Context context;
    private int layoutResourceId;
    private List<Alarm> alarmList;

    public AlarmArrayAdapter(Context context, int layoutResourceId, List<Alarm> alarmList){
        super(context, layoutResourceId, alarmList);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.alarmList = alarmList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        System.out.println("position: " + position + "   convertView: " + convertView + "   parent: " + parent);



        if( convertView == null ) {

            System.out.println("---------------------Got here2  ");
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        System.out.println("---------------------Got here");

        Alarm alarmItem = alarmList.get(position);

        TextView alarmListItem = (TextView) convertView.findViewById(R.id.alarm_list_item);
        alarmListItem.setText("Alarm set for: "
                + alarmItem.getHour() + ":"
                + alarmItem.getMinute());

        return convertView;

    }
}
