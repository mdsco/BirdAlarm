package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
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

        TextView alarmListItemTime =
                (TextView) convertView.findViewById(R.id.alarm_time_text_view);

        alarmListItemTime.setText(
                alarmItem.getHour() + ":"
                        + String.format("%02d", alarmItem.getMinute()));

        final TextView alarmAmPm = (TextView) convertView.findViewById(R.id.aMpMTextView);
        alarmAmPm.setText(alarmItem.getaMpM());

        Switch alarmSwitch = (Switch) convertView.findViewById(R.id.alarmActiveSwitch);
        alarmSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch alarmSwitch = (Switch) view;
                if(!alarmSwitch.isChecked()){
                    alarmItem.cancelAlarm();
                } else if(alarmSwitch.isChecked()){
                    alarmItem.registerAlarm();
                }
            }
        });

        Button deleteButton = (Button) convertView.findViewById(R.id.deleteAlarmButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Deleter fragment = AlarmArrayAdapter.this.fragment;
                fragment.deleteThisAlarm(alarmItem);
            }
        });

        final RelativeLayout optionsSection =
                (RelativeLayout) convertView.findViewById(R.id.options_layout);

        if(!alarmItem.isExpanded()){
            optionsSection.setVisibility(View.GONE);
        }

        final TextView labelEditText = (EditText) convertView.findViewById(R.id.label_edit_text);
        labelEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if(actionId == EditorInfo.IME_ACTION_DONE){

                    String text = labelEditText.getText().toString();
                    Log.v("Before Alarm cancel ", alarmItem.getLabel());

                    alarmItem.cancelAlarm();
                    alarmItem.setLabel(text);
                    alarmItem.registerAlarm();

                    Log.v("After alarm cancel", alarmItem.getLabel());

                }

                return true;
            }
        });

        Button collapseButton = (Button) convertView.findViewById(R.id.collapseButton);

        collapseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            if (optionsSection.getVisibility() == View.VISIBLE) {
                Fx.toggleContents(getContext(), optionsSection);
            } else if (optionsSection.getVisibility() == View.GONE) {
                Fx.toggleContents(getContext(), optionsSection);
            }
            }

        });

        return convertView;
    }
}