package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

class AlarmArrayAdapter extends ArrayAdapter<Alarm> {


    private final MainActivity activity;
    private String LOG_TAG = AlarmArrayAdapter.class.getSimpleName();
    private View currentView;
    private TextView tomorrowView;


    interface Deleter {
        void deleteThisAlarm(Alarm alarm);
    }

    private Context context;
    private List<Alarm> alarmList;
    private AlarmListFragment fragment;

    AlarmArrayAdapter(Context context, AlarmListFragment fragment, List<Alarm> alarmList, MainActivity activity) {
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

        final Alarm alarmItem = alarmList.get(position);

        TextView alarmListItemTime =
                (TextView) convertView.findViewById(R.id.alarm_time_text_view);

        long timestamp = alarmItem.getTimestamp();
        alarmListItemTime.setText(
                Utility.getHourFromTimeStamp(timestamp) + ":"
                        + String.format("%02d",
                                Utility.getMinuteFromTimeStamp(timestamp)));

        final TextView alarmAmPm = (TextView) convertView.findViewById(R.id.am_pm_text_view);
        alarmAmPm.setText(Utility.getAmOrPm(timestamp));

        TimeViewOnClickListener timeViewOnClickListener =
                                    new TimeViewOnClickListener(activity, alarmItem, alarmAmPm);

        alarmListItemTime.setOnClickListener(timeViewOnClickListener);


        tomorrowView = (TextView) convertView.findViewById(R.id.tomorrowTextView);
//        TomorrowViewUpdater tomorrowViewUpdater =
//                                      new TomorrowViewUpdater(tomorrowView, timestamp, context);

        Switch alarmSwitch = (Switch) convertView.findViewById(R.id.alarm_active_switch);

        boolean isActive = false;
        if(alarmItem.getIsActive() == 1){ isActive = true; }

        alarmSwitch.setChecked(isActive);
        alarmSwitch.setOnClickListener(new View.OnClickListener() {
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

            private void updateAlarmActiveStatus(Alarm alarm, boolean isActive) {

                int value = isActive ? 1 : 0;
                alarm.setIsActive(value);

                ContentValues values = new ContentValues();
                values.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ACTIVE, value);

                String selection =
                        UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?";
                String[] selectionArgs = {String.valueOf(alarmItem.getId())};

                alarmItem.updateAlarmInDatabase(values, selection, selectionArgs);

            }
        });

        final RelativeLayout optionsSection =
                (RelativeLayout) convertView.findViewById(R.id.options_layout);

        if (!alarmItem.isExpanded()) {
            optionsSection.setVisibility(View.GONE);
        }

        LinearLayout weekdaySection = (LinearLayout) convertView.findViewById(R.id.day_layout);

        setListenerOnDayButtons(weekdaySection);
        setCorrectDayButtonState(convertView, alarmItem);


        View selectAlarmLayout = convertView.findViewById(R.id.alarm_type_layout);
        selectAlarmLayout.setOnClickListener(getAlarmTypeSelection(position, alarmItem));

        View selectAlarmButton = convertView.findViewById(R.id.change_alarm_button);
        selectAlarmButton.setOnClickListener(getAlarmTypeSelection(position, alarmItem));

        TextView alarmTypeTextView =
                        (TextView) convertView.findViewById(R.id.alarm_type_textview);
        String alarmType = alarmItem.getAlarmType();

        String formattedName = Utility.getFormattedNameFromFilename(alarmType);

        alarmTypeTextView.setText(formattedName);

        String label = alarmItem.getLabel();

        final TextView labelEditText = (EditText) convertView.findViewById(R.id.label_edit_text);

        String defaultLabel = context.getString(R.string.default_label_name);

        if(!label.equals(defaultLabel)) {
            labelEditText.setText(label);
        }

        labelEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

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

    private void setCorrectDayButtonState(View view, Alarm alarm) {

        Button mondayButton = (Button) view.findViewById(R.id.Monday);
        Button tuesdayButton = (Button) view.findViewById(R.id.Tuesday);
        Button wednesdayButton = (Button) view.findViewById(R.id.Wednesday);
        Button thursdayButton = (Button) view.findViewById(R.id.Thursday);
        Button fridayButton = (Button) view.findViewById(R.id.Friday);
        Button saturdayButton = (Button) view.findViewById(R.id.Saturday);
        Button sundayButton = (Button) view.findViewById(R.id.Sunday);

        Alarm.Days[] days = alarm.getDays();
        for (Alarm.Days day : days) {


            switch (day) {

                case MONDAY:
                    mondayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(mondayButton);
                    break;
                case TUESDAY:
                    tuesdayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(tuesdayButton);
                    break;
                case WEDNESDAY:
                    wednesdayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(wednesdayButton);
                    break;
                case THURSDAY:
                    thursdayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(thursdayButton);
                    break;
                case FRIDAY:
                    fridayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(fridayButton);
                    break;
                case SATURDAY:
                    saturdayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(saturdayButton);
                    break;
                case SUNDAY:
                    sundayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(sundayButton);
                    break;
                default:
                    break;
            }

        }

    }

    private void setListenerOnDayButtons(View view) {

        final LinearLayout linearLayout = ((LinearLayout) view);

        int childCount = linearLayout.getChildCount();

        View.OnClickListener clickListener =  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                if(button.getTag().equals("off")){
                    button.setBackground(ContextCompat.getDrawable(context, R.drawable.on_day_selection_button_shape));
                    button.setTextColor(Color.BLACK);
                    button.setTag("on");
                    setTomorrowLabelBasedOnAlarmId(linearLayout);
                } else {
                    button.setBackground(ContextCompat.getDrawable(context, R.drawable.off_day_selection_button_shape));
                    button.setTextColor(Color.WHITE);
                    button.setTag("off");
                    setTomorrowLabelBasedOnAlarmId(linearLayout);
                }
            }
        };

        for(int i = 0; i < childCount; i++ ) {
            Button button = (Button) linearLayout.getChildAt(i);
            button.setOnClickListener(clickListener);
        }
    }

    private void setTomorrowLabelBasedOnAlarmId(LinearLayout linearLayout){

        String alarmDaysLabelString = "";
        int onCount = 0;

        int childCount = linearLayout.getChildCount();

        for(int i = 0; i < childCount; i++){
            Button button = (Button) linearLayout.getChildAt(i);
            if(button.getTag().equals("on")){
                onCount++;
            }
        }

        int commaCount = 0;
        for(int i = 0; i < childCount; i++) {

            Button button = (Button) linearLayout.getChildAt(i);
            int id = button.getId();

            if(button.getTag().equals("on")) {
                commaCount++;
                switch (id) {
                    case R.id.Monday:
                        alarmDaysLabelString += addDayLabel("Monday", button, commaCount, onCount);
                        break;
                    case R.id.Tuesday:
                        alarmDaysLabelString += addDayLabel("Tuesday", button, commaCount, onCount);
                        break;
                    case R.id.Wednesday:
                        alarmDaysLabelString += addDayLabel("Wednesday", button, commaCount, onCount);
                        break;
                    case R.id.Thursday:
                        alarmDaysLabelString += addDayLabel("Thursday", button, commaCount, onCount);
                        break;
                    case R.id.Friday:
                        alarmDaysLabelString += addDayLabel("Friday", button, commaCount, onCount);
                        break;
                    case R.id.Saturday:
                        alarmDaysLabelString += addDayLabel("Saturday", button, commaCount, onCount);
                        break;
                    case R.id.Sunday:
                        alarmDaysLabelString += addDayLabel("Sunday", button, commaCount, onCount);
                        break;
                }
            }

        }

        tomorrowView.setText(alarmDaysLabelString);

    }

    private String addDayLabel(String day, Button button, int commaCount, int onCount){

        String alarmDaysLabelString = "";

        if(button.getTag().equals("on")) {
            alarmDaysLabelString = day;
            if(onCount > 1) {
                alarmDaysLabelString = day.substring(0, 3);
            }
            if(commaCount < onCount){
                alarmDaysLabelString += ", ";
            }
        }
        return alarmDaysLabelString;
    }


    private void setCorrectButtonBackgroundColorBasedOnTag(Button button) {
        if(button.getTag().equals("on")){
            button.setBackground(ContextCompat.getDrawable(context,
                                    R.drawable.on_day_selection_button_shape));
            button.setTextColor(Color.BLACK);
            button.setTag("on");
        } else {
            button.setBackground(ContextCompat.getDrawable(context,
                                    R.drawable.off_day_selection_button_shape));
            button.setTextColor(Color.WHITE);
            button.setTag("off");
        }
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