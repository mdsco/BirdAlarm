package com.example.mike.birdalarm;

import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

class TimeViewOnClickListener implements View.OnClickListener,
                                                    TimePickerDialog.OnTimeSetListener {

    public static final String LOG_TAG = TimeViewOnClickListener.class.getSimpleName();
    private final MainActivity activity;
    private final Alarm alarm;
    private final TextView amPmTextView;
    View view;

    TimeViewOnClickListener(MainActivity activity, Alarm alarm, TextView amPmTextView){
        this.activity = activity;
        this.alarm = alarm;
        this.amPmTextView = amPmTextView;
    }

    @Override
    public void onClick(View view) {

        this.view = view;

        long currentTime = System.currentTimeMillis();

        int hour = Utility.get24FormatHourFromTimeStamp(currentTime);
        int minute = Utility.getMinuteFromTimeStamp(currentTime);

        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, this, hour, minute,
                DateFormat.is24HourFormat(activity));

        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        TextView textView = (TextView) this.view;
        long timeStampFromHourAndMinute =
                            Utility.getTimeStampFromHourAndMinute(hour, minute);
        alarm.setTimestamp(timeStampFromHourAndMinute);
        String formattedTime =
                            Utility.getFormattedTime(timeStampFromHourAndMinute);
        amPmTextView.setText(Utility.getAmOrPm(timeStampFromHourAndMinute));

        textView.setText(formattedTime);

        alarm.reregisterAlarm();

    }
}
