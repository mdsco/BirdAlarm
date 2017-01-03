package com.example.mike.birdalarm;

import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity
                implements TimePickerDialog.OnTimeSetListener {

    AlarmListFragment alarmListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmListFragment = (AlarmListFragment) getFragmentManager()
                                    .findFragmentById(R.id.alarmListFragment);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.settings_item){
            Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void showTimePickerDialog(View view){

        DialogFragment timePickerDialogFragment = new TimePickerDialogFragment();

        timePickerDialogFragment.show(getSupportFragmentManager(), "timePicker");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        alarmListFragment.addAlarm(MainActivity.this, hour, minute);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("MainActivity", "Result Code: " + resultCode);

        if(resultCode == RESULT_OK) {

            String alarmName = data.getStringExtra("alarmName");
            int viewPosition = data.getIntExtra("viewPosition", -1);

            if(viewPosition != -1){

                Fragment fragment = getFragmentManager()
                                .findFragmentById(R.id.alarmListFragment);
                ListView listView =
                            (ListView) fragment.getView().findViewById(android.R.id.list);
                TextView textView = (TextView) listView.getChildAt(viewPosition)
                                       .findViewById(R.id.alarm_type_textview);
                textView.setText(alarmName);

            }

            Log.v("MAinActivity", alarmName);
        }

    }

}