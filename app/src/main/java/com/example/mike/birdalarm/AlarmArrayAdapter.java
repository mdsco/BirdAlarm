package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

    private View currentView;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

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

        final TextView alarmAmPm = (TextView) convertView.findViewById(R.id.am_pm_text_view);
        alarmAmPm.setText(alarmItem.getaMpM());

        Switch alarmSwitch = (Switch) convertView.findViewById(R.id.alarm_active_switch);

        boolean isActive = false;
        if(alarmItem.getIsActive() == 1){ isActive = true; }

        alarmSwitch.setChecked(isActive);
        alarmSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch alarmSwitch = (Switch) view;
                if (!alarmSwitch.isChecked()) {

                    updateAlarmActiveStatus(false);
                    alarmItem.cancelAlarm();

                } else if (alarmSwitch.isChecked()) {

                    updateAlarmActiveStatus(true);
                    alarmItem.registerAlarm(alarmItem.getId());
                }
            }

            private void updateAlarmActiveStatus(boolean isActive) {

                int value = isActive ? 1 : 0;

                ContentValues values = new ContentValues();
                values.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE, value);

                String selection =
                        UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?";
                String[] selectionArgs = {String.valueOf((int) alarmItem.getId())};

                alarmItem.updateAlarmInDatabase(values, selection, selectionArgs, value);
            }
        });

        final RelativeLayout optionsSection =
                (RelativeLayout) convertView.findViewById(R.id.options_layout);

        if (!alarmItem.isExpanded()) {
            optionsSection.setVisibility(View.GONE);
        }

        View selectAlarmLayout = convertView.findViewById(R.id.alarm_type_layout);
        selectAlarmLayout.setOnClickListener(getAlarmTypeSelection(position, alarmItem));

        View selectAlarmButton = convertView.findViewById(R.id.change_alarm_button);
        selectAlarmButton.setOnClickListener(getAlarmTypeSelection(position, alarmItem));

        TextView alarmTypeTextView =
                        (TextView) convertView.findViewById(R.id.alarm_type_textview);
        alarmTypeTextView.setText(Defaults.DEFAULT_ALARM_TYPE_NAME);

        final TextView labelEditText = (EditText) convertView.findViewById(R.id.label_edit_text);
        labelEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String text = labelEditText.getText().toString();

                    alarmItem.setLabel(text);

                    alarmItem.cancelAlarm();

                    int id = alarmItem.getId();
                    alarmItem.setId(id);

                    alarmItem.registerAlarm(alarmItem.getId());

                }

                return true;
            }
        });

        Button deleteAlarmButton = (Button) convertView.findViewById(R.id.delete_alarm_button);

        deleteAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Deleter fragment = AlarmArrayAdapter.this.fragment;
                fragment.deleteThisAlarm(alarmItem);
            }
        });

        Button collapseButton = (Button) convertView.findViewById(R.id.collapse_button);

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

    @NonNull
    private View.OnClickListener getAlarmTypeSelection(final int position, final Alarm alarm) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, AlarmSelectionActivity.class);
                intent.putExtra("viewPosition", position);
                intent.putExtra("alarmType", alarm.getAlarmType());
                AlarmArrayAdapter.this.currentView = view;

                ((Activity) context).startActivityForResult(intent, 0);

            }
        };
    }
}