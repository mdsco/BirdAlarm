package com.example.mike.birdalarm;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private String LOG_TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(getPreferenceManager().findPreference(getString(R.string.pref_snooze_key)));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(this);

        String key = preference.getKey();

        Log.v("SettingsFragment", "Sleep interval: " + key);

        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(key, getString(R.string.pref_snooze_default)));

//
//        onPreferenceChange(preference, PreferenceManager
//                .getDefaultSharedPreferences(preference.getContext())
//                .getString(preference.getKey(), ""));
//

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        return true;
    }


}