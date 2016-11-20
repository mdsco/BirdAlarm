package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

class AlarmArrayAdapter extends ArrayAdapter<Alarm> {


    private Context context;
    private int layoutResourceId;
    private List<Alarm> alarmList;


    AlarmArrayAdapter(Context context, List<Alarm> alarmList) {
        super(context, R.layout.alarm_list_item, alarmList);

        this.context = context;
        this.alarmList = alarmList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.alarm_list_item, parent, false);
        }

        Alarm alarmItem = alarmList.get(position);

        TextView alarmListItemTime = (TextView) convertView.findViewById(R.id.alarmTimeTextView);

        alarmListItemTime.setText(
                + alarmItem.getHour() + ":"
                + String.format("%02d", alarmItem.getMinute()));

        Button collapseButton = (Button) convertView.findViewById(R.id.collapseButton);
        final TextView placeholderView = (TextView) convertView.findViewById(R.id.placeholdertextView);

        collapseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                collapseAlarmItem(placeholderView);
            }

        });

        TextView alarmAmPm = (TextView) convertView.findViewById(R.id.aMpMTextView);
        alarmAmPm.setText(alarmItem.getaMpM());

        return convertView;

    }

    public void collapseAlarmItem(View view){

        if(view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else if(view.getVisibility() == View.GONE){
            view.setVisibility(View.VISIBLE);
        }

    }

}