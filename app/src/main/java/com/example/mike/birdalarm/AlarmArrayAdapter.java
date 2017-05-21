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
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

class AlarmArrayAdapter extends ArrayAdapter<Alarm> {

    private final MainActivity activity;
    private String LOG_TAG = AlarmArrayAdapter.class.getSimpleName();
    private TextView tomorrowView;
    public int position;

    interface Deleter {
        void deleteThisAlarm(Alarm alarm);
    }

    interface ExpandCollapseListener {
        void listItemCollapsed(Alarm alarm);
    }

    private Context context;
    private List<Alarm> alarmList;
    private AlarmListFragment fragment;

    AlarmArrayAdapter(Context context, AlarmListFragment fragment,
                                        List<Alarm> alarmList, MainActivity activity) {

        super(context, R.layout.alarm_list_item, alarmList);

        this.context = context;
        this.fragment = fragment;
        this.alarmList = alarmList;

        this.activity = activity;

    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.alarm_list_item, parent, false);

        }

        final Alarm alarm = alarmList.get(position);

        final View convertView2 = convertView;

        LinearLayout linearLayout = (LinearLayout) (convertView.findViewById(
                R.id.item_linear_layout));
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams
                (AbsListView.LayoutParams.MATCH_PARENT, alarm.getCollapsedHeight());
        linearLayout.setLayoutParams(linearLayoutParams);

        final ExpandingListView listView = (ExpandingListView) fragment.getListView();

        ExpandingLayout expandingLayout = (ExpandingLayout) convertView.findViewById(R.id.options_layout);
        expandingLayout.setExpandedHeight(alarm.getExpandedHeight());
        expandingLayout.setSizeChangedListener(alarm);

        if (!alarm.isExpanded()) {
            expandingLayout.setVisibility(View.GONE);
        } else {
            expandingLayout.setVisibility(View.VISIBLE);
        }

        final Alarm alarmItem = alarm;

        setTimeTextViews(convertView, alarmItem);

        tomorrowView = (TextView) convertView.findViewById(R.id.tomorrowTextView);
//        TomorrowViewUpdater tomorrowViewUpdater =
//                                      new TomorrowViewUpdater(tomorrowView, timestamp, context);

        setAlarmSwitch(convertView, alarmItem);

        LinearLayout weekdaySection = (LinearLayout) convertView.findViewById(R.id.day_layout);

        setDayButtons(alarmItem, weekdaySection);

        setRepeatCheckbox(convertView, alarmItem);

        View selectAlarmLayout = convertView.findViewById(R.id.alarm_type_layout);
        selectAlarmLayout.setOnClickListener(getAlarmTypeSelection(position, alarmItem));

        View selectAlarmButton = convertView.findViewById(R.id.change_alarm_button);
        selectAlarmButton.setOnClickListener(getAlarmTypeSelection(position, alarmItem));

        TextView alarmTypeTextView =
                (TextView) convertView.findViewById(R.id.alarm_type_textview);

        String alarmType = alarmItem.getAlarmType();

        String formattedName = Utility.getFormattedNameFromFilename(alarmType);

        alarmTypeTextView.setText(formattedName);

        setVibrateCheckbox(convertView, alarmItem);

        setAlarmLabel(convertView, alarmItem);

        setDeleteAlarmButton(convertView, alarmItem);

        convertView.setLayoutParams(new ListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));

        ImageButton collapseButton = (ImageButton) convertView.findViewById(R.id.collapse_button);
        collapseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!alarmItem.isExpanded()) {
                    listView.expandView(convertView2);
                } else {
                    listView.collapseView(convertView2);
                }

            }
        });

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    private void setDeleteAlarmButton(View convertView, final Alarm alarmItem) {
        Button deleteAlarmButton = (Button) convertView.findViewById(R.id.delete_alarm_button);

        deleteAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Deleter fragment = AlarmArrayAdapter.this.fragment;
                fragment.deleteThisAlarm(alarmItem);
            }
        });
    }

    private void setAlarmLabel(View convertView, final Alarm alarmItem) {
        String label = alarmItem.getLabel();

        final TextView labelEditText = (EditText) convertView.findViewById(R.id.label_edit_text);

        String defaultLabel = context.getString(R.string.default_label_name);

        if (!label.equals(defaultLabel)) {
            labelEditText.setText(label);
        }

        labelEditText.setOnEditorActionListener(getLabelditorActionListener(alarmItem, labelEditText));
    }

    @NonNull
    private TextView.OnEditorActionListener getLabelditorActionListener(final Alarm alarmItem, final TextView labelEditText) {
        return new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager inputMethodManager =
                            (InputMethodManager)
                                    context.getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputMethodManager.hideSoftInputFromWindow(labelEditText.getWindowToken(), 0);

                    labelEditText.clearFocus();

                    String text = labelEditText.getText().toString();

                    alarmItem.setLabel(text);

                    alarmItem.reregisterAlarm();

                }

                return true;
            }
        };
    }

    private void setVibrateCheckbox(View convertView, final Alarm alarmItem) {
        CheckBox vibrateCheckbox = (CheckBox) convertView.findViewById(R.id.vibrateCheckBox);
        if (alarmItem.getVibrate()) {
            vibrateCheckbox.setChecked(true);
        }

        vibrateCheckbox.setOnClickListener(getVibrateButtonClickListener(alarmItem));
    }

    @NonNull
    private View.OnClickListener getVibrateButtonClickListener(final Alarm alarmItem) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alarmItem.setVibrateToOpposite();
                ContentValues values = new ContentValues();
                boolean vibrate = alarmItem.getVibrate();
                values.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_VIBRATE,
                        vibrate ? 1 : 0);
                String selection =
                        UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?";
                String[] selectionArgs = {String.valueOf(alarmItem.getId())};

                alarmItem.updateAlarmInDatabase(values, selection, selectionArgs);
                alarmItem.reregisterAlarm();
            }
        };
    }

    private void setRepeatCheckbox(View convertView, final Alarm alarmItem) {

        CheckBox repeatCheckBox = (CheckBox) convertView.findViewById(R.id.repeat_days_checkbox);
        if (alarmItem.isAlarmIsRepeating()) {
            repeatCheckBox.setChecked(true);
        }

        repeatCheckBox.setOnClickListener(getRepeatCheckboxClickListener(alarmItem));
    }

    @NonNull
    private View.OnClickListener getRepeatCheckboxClickListener(final Alarm alarmItem) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alarmItem.setAlarmIsRepeating(!alarmItem.isAlarmIsRepeating());

                ContentValues values = new ContentValues();
                boolean repeating = alarmItem.isAlarmIsRepeating();
                values.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_REPEATING,
                        repeating ? 1 : 0);
                String selection =
                        UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?";
                String[] selectionArgs = {String.valueOf(alarmItem.getId())};
                alarmItem.updateAlarmInDatabase(values, selection, selectionArgs);
                alarmItem.setTimestampBasedOnNextViableDay();

            }
        };
    }

    private void setDayButtons(Alarm alarmItem, LinearLayout weekdaySection) {
        DayButtonUtility dayButtonUtility = new DayButtonUtility();

        dayButtonUtility.setListenerOnDayButtons(context, weekdaySection, alarmItem, tomorrowView);
        dayButtonUtility.setCorrectDayButtonState(context, weekdaySection, alarmItem);
    }

    private void setAlarmSwitch(View convertView, final Alarm alarmItem) {
        Switch alarmSwitch = (Switch) convertView.findViewById(R.id.alarm_active_switch);

        boolean isActive = false;
        if (alarmItem.getIsActive() == 1) {
            isActive = true;
        }

        alarmSwitch.setChecked(isActive);
        alarmSwitch.setOnClickListener(getAlarmSwitchclickListener(alarmItem));
    }

    @NonNull
    private View.OnClickListener getAlarmSwitchclickListener(final Alarm alarmItem) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch alarmSwitch = (Switch) view;
                if (!alarmSwitch.isChecked()) {

                    updateAlarmActiveStatus(alarmItem, false);
                    alarmItem.cancelAlarm();

                } else if (alarmSwitch.isChecked()) {

                    updateAlarmActiveStatus(alarmItem, true);
                    alarmItem.reregisterAlarm();
                }
            }


        };
    }

    private void updateAlarmActiveStatus(Alarm alarmItem, boolean isActive) {

        int value = isActive ? 1 : 0;
        alarmItem.setIsActive(value);

        ContentValues values = new ContentValues();
        values.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE, value);

        String selection =
                UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?";
        String[] selectionArgs = {String.valueOf(alarmItem.getId())};

        alarmItem.updateAlarmInDatabase(values, selection, selectionArgs);

    }

    private void setTimeTextViews(View convertView, Alarm alarmItem) {
        TextView alarmListItemTime =
                (TextView) convertView.findViewById(R.id.alarm_time_text_view);

        long timestamp = alarmItem.getTimestamp();

        alarmListItemTime.setText(Utility.getFormattedTime(timestamp));


        final TextView alarmAmPm = (TextView) convertView.findViewById(R.id.am_pm_text_view);
        alarmAmPm.setText(Utility.getAmOrPm(timestamp));

        TimeViewOnClickListener timeViewOnClickListener =
                new TimeViewOnClickListener(activity, alarmItem, alarmAmPm);

        alarmListItemTime.setOnClickListener(timeViewOnClickListener);
    }

    @NonNull
    private View.OnClickListener getAlarmTypeSelection(final int position, final Alarm alarm) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, AlarmSelectionActivity.class);

                intent.putExtra("viewPosition", position);
                String alarmType = alarm.getAlarmType();
                intent.putExtra("alarmType", alarmType);

                ((Activity) context).startActivityForResult(intent, 0);

            }
        };

    }
}