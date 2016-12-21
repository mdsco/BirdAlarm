package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

class AlarmArrayAdapter extends ArrayAdapter<Alarm> {

    interface Deleter {

        void deleteThisAlarm(Alarm alarm);

    }

    private Context context;
    private List<Alarm> alarmList;
    private AlarmListFragment fragment;

    AlarmArrayAdapter(Context context, AlarmListFragment fragment, List<Alarm> alarmList) {
        super(context, R.layout.alarm_list_item, alarmList);

        this.context = context;
        this.fragment = fragment;
        this.alarmList = alarmList;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.alarm_list_item, parent, false);
        }

        final Alarm alarmItem = alarmList.get(position);

        TextView alarmListItemTime = (TextView) convertView.findViewById(R.id.alarmTimeTextView);

        alarmListItemTime.setText(
                +alarmItem.getHour() + ":"
                        + String.format("%02d", alarmItem.getMinute()));

        TextView alarmAmPm = (TextView) convertView.findViewById(R.id.aMpMTextView);
        alarmAmPm.setText(alarmItem.getaMpM());


        final TextView placeholderView =
                (TextView) convertView.findViewById(R.id.placeholdertextView);

        if (!alarmItem.isExpanded()) {
            collapseAlarmItem(placeholderView);
        }


        Button deleteButton = (Button) convertView.findViewById(R.id.deleteAlarmButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Deleter fragment = AlarmArrayAdapter.this.fragment;
                fragment.deleteThisAlarm(alarmItem);
            }
        });


        Button collapseButton = (Button) convertView.findViewById(R.id.collapseButton);

        collapseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (placeholderView.getVisibility() == View.VISIBLE) {
                    collapseAlarmItem(placeholderView);
                } else if (placeholderView.getVisibility() == View.GONE) {
                    expandAlarmItem(placeholderView);
                }
            }

        });

        return convertView;

    }

    private static void collapseAlarmItem(View view) {

        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    private static void expandAlarmItem(View view) {

        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        }
    }
}