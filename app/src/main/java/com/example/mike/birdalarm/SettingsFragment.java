package com.example.mike.birdalarm;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment
                implements Preference.OnPreferenceChangeListener{

    private String LOG_TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(getPreferenceManager()
                .findPreference(getString(R.string.pref_snooze_key)));
    }

    private void bindPreferenceSummaryToValue(Preference preference){

        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), getString(R.string.pref_snooze_default)));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value){


        String summaryValue = value.toString();
        Log.v(LOG_TAG, " Preferenc: " + preference.toString());



        if(preference instanceof ListPreference){

            Log.v(LOG_TAG, "In here");
            ListPreference listPreference = (ListPreference) preference;
            int preferenceIndex = listPreference.findIndexOfValue(summaryValue);
            preference.setSummary(preferenceIndex);

        } else {
            preference.setSummary(summaryValue);
        }

        return false;
    }
}