package com.wordpress.jrcrispell.moodcalendar;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;

public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_screen);

    }

}
