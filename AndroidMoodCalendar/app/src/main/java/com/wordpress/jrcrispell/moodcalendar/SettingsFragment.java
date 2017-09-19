package com.wordpress.jrcrispell.moodcalendar;

import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TimePicker;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_screen);

        Preference startTime = findPreference("com.wordpress.jrcrispell.moodcalendar.notifications_start");
        startTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Toast.makeText(getActivity(), Integer.toString(i) + "." + Integer.toString(i1), Toast.LENGTH_SHORT).show();
                    }
                }, 8, 0, false);
                dialog.show();
                return true;
            }
        });
    }


}
