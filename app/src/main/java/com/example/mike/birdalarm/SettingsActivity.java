package com.example.mike.birdalarm;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by mike on 11/30/16.
 */


public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }
}

