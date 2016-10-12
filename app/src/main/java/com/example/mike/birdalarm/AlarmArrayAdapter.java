package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

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

        if( convertView == null ) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        Alarm alarmItem = alarmList.get(position);

        TextView alarmListItem = (TextView) convertView.findViewById(R.id.alarm_list_item);

        alarmListItem.setText("Alarm set for: "
                + alarmItem.getHour() + ":"
                + alarmItem.getMinute());

        return convertView;

    }
}
