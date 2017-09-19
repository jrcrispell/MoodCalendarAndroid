package com.wordpress.jrcrispell.moodcalendar;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SettingsActivity extends AppCompatActivity {

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences dPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String oldDepressionValue = dPrefs.getString("com.wordpress.jrcrispell.moodcalendar.depression_screen_days", null);

        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.settings_action_bar);

        getFragmentManager().beginTransaction().replace(R.id.root_fragment_container, SettingsFragment.newInstance()).commit();

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = dPrefs.edit();

                // Restore old value
                editor.putString("com.wordpress.jrcrispell.moodcalendar.depression_screen_days", oldDepressionValue);
                editor.apply();
                onBackPressed();

            }
        });
    }
}
