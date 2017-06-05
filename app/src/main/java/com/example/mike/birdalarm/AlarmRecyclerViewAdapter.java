package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;


public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.AlarmViewHolder> {


    private final AlarmRecyclerViewFragment fragment;
    private final MainActivity activity;
    Context context;

    private List<Alarm> alarmList;
    private TextView tomorrowView;
    private Alarm currentAlarm;
    private float expandedViewElevation = 20.0f;
    private float expandedViewZ = 40.0f;
    private float collapsedViewElevation = 3.5f;
    private float collapsedViewZ = 3.5f;

    interface Deleter {
        void deleteThisAlarm(Alarm alarm);
    }

    public AlarmRecyclerViewAdapter(Context context, List<Alarm> alarmList,
                                    AlarmRecyclerViewFragment fragment, MainActivity activity) {

        this.context = context;
        this.alarmList = alarmList;
        this.fragment = fragment;
        this.activity = activity;

    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View cardItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_card_item, parent, false);

        AlarmViewHolder alarmViewHolder = new AlarmViewHolder(cardItem);

        final AutoTransition transition = new AutoTransition();
        transition.setDuration(100);

        alarmViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

//            boolean visible = false;
            boolean visible = false;

            @Override
            public void onClick(@NonNull View view) {

                visible = currentAlarm.isExpanded();

                Log.v("Initial elevation", collapsedViewElevation + "");
                Log.v("Initial z", collapsedViewZ + "");

                ConstraintLayout hiddenLayout =
                                (ConstraintLayout) view.findViewById(R.id.card_options_layout);

                visible = !visible;

                float elevation = visible ? expandedViewElevation : collapsedViewElevation;
                float zVal = visible ? expandedViewZ : collapsedViewZ;

                TransitionManager.beginDelayedTransition(parent, transition);
                hiddenLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
                currentAlarm.setExpandedState(visible ? true : false);
                view.setElevation(elevation);
                view.setTranslationZ(zVal);


            }

        });

        return alarmViewHolder;
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {

        currentAlarm = alarmList.get(position);

        tomorrowView = holder.tomorrowView;
        setTimeTextViews(holder.alarmTimeTextView, holder.aMPmTextView, currentAlarm);
        setAlarmSwitch(holder.onOffSwitch, currentAlarm);
        setRepeatCheckbox(holder.repeatCheckbox, currentAlarm);
        setDayButtons(currentAlarm, holder.weekdaySection);
        holder.selectAlarmButton.setOnClickListener(getAlarmTypeSelection(position, currentAlarm));
        holder.selectAlarmTextView.setOnClickListener(getAlarmTypeSelection(position, currentAlarm));
        String formattedName = Utility.getFormattedNameFromFilename(currentAlarm.getAlarmType());
        holder.selectAlarmTextView.setText(formattedName);
        setVibrateCheckbox(holder.vibrateCheckBox, currentAlarm);
        setAlarmLabel(holder.labelEditText, currentAlarm);
        setDeleteAlarmButton(holder.deleteAlarmButton, currentAlarm);

        if (!currentAlarm.isExpanded()) {
            holder.constraintLayout.setVisibility(View.GONE);
            holder.itemView.setElevation(collapsedViewElevation);
            holder.itemView.setZ(collapsedViewZ);
        } else {
            holder.constraintLayout.setVisibility(View.VISIBLE);
            holder.itemView.setElevation(expandedViewElevation);
            holder.itemView.setZ(expandedViewZ);
        }

    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox repeatCheckbox;
        private final Button deleteAlarmButton;
        private final LinearLayout weekdaySection;
        private final Button selectAlarmButton;
        private final CheckBox vibrateCheckBox;
        private final EditText labelEditText;
        private final TextView selectAlarmTextView;
        private final TextView tomorrowView;
        private final ConstraintLayout constraintLayout;
        TextView alarmTimeTextView;
        TextView aMPmTextView;
        Switch onOffSwitch;


        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);

            alarmTimeTextView = (TextView) itemView.findViewById(R.id.alarm_time_text_view);
            aMPmTextView = (TextView) itemView.findViewById(R.id.am_pm_text_view);
            onOffSwitch = (Switch) itemView.findViewById(R.id.alarm_active_switch);
            repeatCheckbox = (CheckBox) itemView.findViewById(R.id.repeat_days_checkbox);
            deleteAlarmButton= (Button) itemView.findViewById(R.id.delete_alarm_button);
            weekdaySection = (LinearLayout) itemView.findViewById(R.id.day_layout);

            selectAlarmButton = (Button) itemView.findViewById(R.id.change_alarm_button);
            selectAlarmTextView = (TextView) itemView.findViewById(R.id.alarm_type_textview);
            labelEditText = (EditText) itemView.findViewById(R.id.label_edit_text);
            vibrateCheckBox = (CheckBox) itemView.findViewById(R.id.vibrateCheckBox);
            tomorrowView = (TextView) itemView.findViewById(R.id.tomorrowTextView);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.card_options_layout);

        }



    }

    private void setDeleteAlarmButton(View convertView, final Alarm alarmItem) {
        Button deleteAlarmButton = (Button) convertView.findViewById(R.id.delete_alarm_button);

        deleteAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmRecyclerViewAdapter.Deleter fragment = AlarmRecyclerViewAdapter.this.fragment;
                fragment.deleteThisAlarm(alarmItem);
            }
        });
    }

    private void setAlarmLabel(EditText labelEditText, final Alarm alarmItem) {

        String label = alarmItem.getLabel();

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

    private void setVibrateCheckbox(CheckBox vibrateCheckbox, final Alarm alarmItem) {

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

    private void setRepeatCheckbox(CheckBox repeatCheckBox, final Alarm alarmItem) {

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

    private void setAlarmSwitch(Switch alarmSwitch, final Alarm alarmItem) {

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

    private void setTimeTextViews(TextView timeView, TextView amPmView, Alarm alarmItem) {

        long timestamp = alarmItem.getTimestamp();

        timeView.setText(Utility.getFormattedTime(timestamp));

        amPmView.setText(Utility.getAmOrPm(timestamp));

        TimeViewOnClickListener timeViewOnClickListener =
                                new TimeViewOnClickListener(activity, alarmItem, amPmView);

        timeView.setOnClickListener(timeViewOnClickListener);

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

