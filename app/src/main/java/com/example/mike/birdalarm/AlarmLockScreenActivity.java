package com.example.mike.birdalarm;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;


public class AlarmLockScreenActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.alarm_lock_screen);
    }
}
