package com.example.mike.birdalarm;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


class DayButtonUtility {

    public static final String LOG_TAG = DayButtonUtility.class.getSimpleName();

    void setCorrectDayButtonState(Context context, View view, Alarm alarm) {

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
                    setCorrectButtonBackgroundColorBasedOnTag(context, mondayButton);
                    break;
                case TUESDAY:
                    tuesdayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(context, tuesdayButton);
                    break;
                case WEDNESDAY:
                    wednesdayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(context, wednesdayButton);
                    break;
                case THURSDAY:
                    thursdayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(context, thursdayButton);
                    break;
                case FRIDAY:
                    fridayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(context, fridayButton);
                    break;
                case SATURDAY:
                    saturdayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(context, saturdayButton);
                    break;
                case SUNDAY:
                    sundayButton.setTag("on");
                    setCorrectButtonBackgroundColorBasedOnTag(context, sundayButton);
                    break;
                default:
                    break;
            }
        }
    }

    void setListenerOnDayButtons(final Context context, View view,
                                                final Alarm alarm, final TextView tommorowView) {

        final LinearLayout linearLayout = ((LinearLayout) view);
        int childCount = linearLayout.getChildCount();

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;

                if(button.getTag().equals("off")){
                    setButtonLook(button, context, "on", Color.BLACK,
                                                    R.drawable.on_day_selection_button_shape);
                } else {

                    setButtonLook(button, context, "off", Color.WHITE,
                                                    R.drawable.off_day_selection_button_shape);
                }

                setTomorrowLabelBasedOnAlarmId(linearLayout, tommorowView);

                resetDaysListToSelectedDays(linearLayout, alarm);

                updateDatebaseWithNewDaysString(alarm);

                alarm.setTimestampBasedOnNextViableDay();

            }
        };

        for(int i = 0; i < childCount; i++ ) {
            Button button = (Button) linearLayout.getChildAt(i);
            button.setOnClickListener(clickListener);
        }
    }

    private void setButtonLook(Button button, Context context, String onOffTagString,
                                                                    int color, int drawable) {

        button.setBackground(ContextCompat.getDrawable(context, drawable));
        button.setTextColor(color);
        button.setTag(onOffTagString);
        setButtonTouchAnimation(button);

    }


    private void setButtonTouchAnimation(final Button button){

        final DecelerateInterpolator dInterpolator = new DecelerateInterpolator();
        final OvershootInterpolator oInterpolator = new OvershootInterpolator(10f);

        button.animate().setDuration(200);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    button.animate().setInterpolator(dInterpolator).scaleX(.7f).scaleY(.7f);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    button.animate().setInterpolator(oInterpolator).scaleX(1f).scaleY(1f);
                }

                return false;
            }
        });


    }

    private void updateDatebaseWithNewDaysString(Alarm alarm) {

        String daysStringFromString = Alarm.getDaysStringFromString(alarm.getDays());

        ContentValues values = new ContentValues();
        values.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_DAYS_ACTIVE, daysStringFromString);

        String selection = UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?";
        String[] selectionArgs = {String.valueOf(alarm.getId())};

        alarm.updateAlarmInDatabase(values, selection, selectionArgs);

    }

    private  void resetDaysListToSelectedDays(View view, Alarm alarm){

        LinearLayout linearLayout = (LinearLayout) view;
        int childCount = linearLayout.getChildCount();
        int onCount = 0;

        for(int i = 0; i < childCount; i++){
            Button button = (Button) linearLayout.getChildAt(i);
            if(button.getTag().equals("on")){
                onCount++;
            }
        }

        Alarm.Days[] days = new Alarm.Days[onCount];

        int count = 0;
        for(int i = 0; i < childCount; i++) {

            Button button = (Button) linearLayout.getChildAt(i);
            int id = button.getId();

            if(button.getTag().equals("on")) {
                switch (id) {
                    case R.id.Monday: days[count] = Alarm.Days.MONDAY; break;
                    case R.id.Tuesday: days[count] = Alarm.Days.TUESDAY; break;
                    case R.id.Wednesday: days[count] = Alarm.Days.WEDNESDAY; break;
                    case R.id.Thursday: days[count] = Alarm.Days.THURSDAY; break;
                    case R.id.Friday: days[count] = Alarm.Days.FRIDAY; break;
                    case R.id.Saturday: days[count] = Alarm.Days.SATURDAY; break;
                    case R.id.Sunday: days[count] = Alarm.Days.SUNDAY; break;
                }
                count++;
            }
        }

        alarm.setDays(days);
    }

    private  String addDayLabel(String day, Button button, int commaCount, int onCount){

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

    private  void setTomorrowLabelBasedOnAlarmId(
                                            LinearLayout linearLayout, TextView tomorrowView){

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

            String day;
            if(button.getTag().equals("on")) {
                commaCount++;
                switch (id) {
                    case R.id.Monday: day = "Monday"; break;
                    case R.id.Tuesday: day = "Tuesday"; break;
                    case R.id.Wednesday: day = "Wednesday"; break;
                    case R.id.Thursday: day = "Thursday"; break;
                    case R.id.Friday: day = "Friday"; break;
                    case R.id.Saturday: day = "Saturday"; break;
                    case R.id.Sunday: day = "Sunday"; break;
                    default: day = ""; break;
                }
                alarmDaysLabelString += addDayLabel(day, button, commaCount, onCount);
            }
        }

        tomorrowView.setText(alarmDaysLabelString);

    }

    private  void setCorrectButtonBackgroundColorBasedOnTag(Context context, Button button) {
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
}
