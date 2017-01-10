package com.example.mike.birdalarm;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener {

    private String LOG_TAG = MainActivity.class.getSimpleName();

    public AlarmListFragment getAlarmListFragment() {
        return alarmListFragment;
    }

    private AlarmListFragment alarmListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmListFragment = (AlarmListFragment) getFragmentManager()
                .findFragmentById(R.id.alarm_list_fragment);

        alarmListFragment.setActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.settings_item) {
            Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void showTimePickerDialog(View view) {

        DialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        timePickerDialogFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        alarmListFragment.addAlarm(MainActivity.this, hour, minute);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        updateAlarmTypeForAlamAtPositionInList(resultCode, data);
    }

    private void updateAlarmTypeForAlamAtPositionInList(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            String alarmName = data.getStringExtra("alarmName");
            int viewPosition = data.getIntExtra("viewPosition", -1);

            if (viewPosition != -1) {

                //get Alarm to be updated
                AlarmListFragment fragment = (AlarmListFragment) getFragmentManager()
                        .findFragmentById(R.id.alarm_list_fragment);

                ArrayList<Alarm> alarmItems = fragment.getAlarmItems();

                Alarm alarm = alarmItems.get(viewPosition);

                //get filename from name
                String fileName = getFileName(alarmName);
                alarm.setAlarmType(fileName);

                //update alarm type in database
                ContentValues contentValues = new ContentValues();
                contentValues.put(UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_TYPE, fileName);
                String selection = UserCreatedAlarmContract.NewAlarmEntry.COLUMN_ALARM_ID + " = ?";
                String[] selectionArgs = {String.valueOf(alarm.getId())};

                alarm.updateAlarmInDatabase(contentValues, selection, selectionArgs);

                alarm.reregisterAlarm();

                //!!! A method in AlarmListFragment to reload the list might nice (to be called here)
                ListView listView =
                        (ListView) fragment.getView().findViewById(android.R.id.list);
                TextView textView = (TextView) listView.getChildAt(viewPosition)
                        .findViewById(R.id.alarm_type_textview);
                textView.setText(alarmName);

            }

        }
    }

    private String getFileName(String name) {

        switch (name) {

            case "cute robin chirping":
                return "small_robin_chirping.mp4";
            case "bower bird":
                return "bower_bird.mp4";
            case "bower bird3":
                return "bower_bird3.mp4";
            case "bower bird4":
                return "bower_bird4.mp4";
            default:
                return "";
        }

    }
}