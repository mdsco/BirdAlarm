package com.example.mike.birdalarm;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    AlarmListFragment alarmListFragment;

//    long tie = System.currentTimeMillis() + 5000;
//
//    Handler timerHandler = new Handler();
//    Runnable timerRunnable;

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

//    public void setTime(final String time){
//
//        timerRunnable = new Runnable(){
//
//            @Override
//            public void run() {
//
//            TextView alarmLabel  = (TextView) findViewById(R.id.textView2);
//            alarmLabel.setText(time);
//
//            if(alarmLabel.getCurrentTextColor() != Color.RED) {
//                System.out.println("Here 1 ");
//                alarmLabel.setTextColor(Color.RED);
//            } else {
//                System.out.println("Here 2 ");
//                alarmLabel.setTextColor(Color.BLUE);
//            }
//
//            timerHandler.postDelayed(this, 500);
//            }
//
//        };
//
//        long waitTime = new Long(time) * 1000;
//
//        timerHandler.postDelayed(timerRunnable, waitTime);
//    }

    @Override
    protected void onPause() {
        super.onPause();
//        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        alarmListFragment.addAlarm(MainActivity.this, hour + "", minute + "");
    }

}