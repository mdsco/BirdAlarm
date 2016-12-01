package com.example.mike.birdalarm;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by mike on 12/1/16.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

//        Preference snoozePreference = getPreferenceManager().findPreference(getString(R.string.pref_snooze_key));
//        snoozePreference.setOnPreferenceChangeListener
    }


}