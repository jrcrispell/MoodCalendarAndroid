package com.wordpress.jrcrispell.moodcalendar;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragment {

    public static final String NOTIFICATIONS_START_SUMMARY = "com.wordpress.jrcrispell.moodcalendar.notifications_start_summary";
    public static final String NOTIFICATIONS_START_VALUE = "com.wordpress.jrcrispell.moodcalendar.notifications_start";
    public static final String NOTIFICATIONS_START_HOUR = "com.wordpress.jrcrispell.moodcalendar.notifications_start_hour";
    public static final String NOTIFICATIONS_START_MINUTES = "com.wordpress.jrcrispell.moodcalendar.notifications_start_minutes";

    public static final String NOTIFICATIONS_END_SUMMARY = "com.wordpress.jrcrispell.moodcalendar.notifications_end_summary";
    public static final String NOTIFICATIONS_END_VALUE = "com.wordpress.jrcrispell.moodcalendar.notifications_end";
    public static final String NOTIFICATIONS_END_HOUR = "com.wordpress.jrcrispell.moodcalendar.notifications_end_hour";
    public static final String NOTIFICATIONS_END_MINUTES = "com.wordpress.jrcrispell.moodcalendar.notifications_end_minutes";
    public static final String SCREEN_LAST_TAKEN = "com.wordpress.jrcrispell.moodcalendar.screen_last_taken";
    public static final String SCREEN_INTERVAL = "com.wordpress.jrcrispell.moodcalendar.depression_screen_days";

    int startHourPicker;
    int startMinutesPicker;
    int endHourPicker;
    int endMinutesPicker;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    Locale locale;

    SharedPreferences.Editor editor;
    SharedPreferences dPrefs;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }

        dPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = dPrefs.edit();


        addPreferencesFromResource(R.xml.prefs_screen);

        startHourPicker = dPrefs.getInt(NOTIFICATIONS_START_HOUR, 8);
        startMinutesPicker = dPrefs.getInt(NOTIFICATIONS_START_MINUTES, 0);


        // Date Picker Logic
        final Preference startTime = findPreference(NOTIFICATIONS_START_VALUE);
        String startSummary = dPrefs.getString(NOTIFICATIONS_START_SUMMARY, "8:00 AM");
        String endSummary = dPrefs.getString(NOTIFICATIONS_END_SUMMARY, "11:00 PM");

        startTime.setSummary(startSummary);

        startTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                        double decimal = ((double) minutes / 60);
                        String startPrefValue = Double.toString(hour + decimal);
                        editor.putString(NOTIFICATIONS_START_VALUE, startPrefValue);

                        boolean isPM = false;

                        int convertedHour = hour;

                        if (hour > 12) {
                            convertedHour = hour - 12;
                            isPM = true;
                        } else if (hour == 12) {
                            isPM = true;
                        }

                        StringBuilder builder = new StringBuilder();
                        builder.append(String.format(locale, "%2d:%02d", convertedHour, minutes));

                        if (!isPM) {
                            builder.append(" AM");
                        }
                        else {
                            builder.append(" PM");
                        }
                        startTime.setSummary(builder.toString());
                        startHourPicker = hour;
                        startMinutesPicker = minutes;

                        editor.putString(NOTIFICATIONS_START_SUMMARY, builder.toString());
                        editor.putInt(NOTIFICATIONS_START_HOUR, hour);
                        editor.putInt(NOTIFICATIONS_START_MINUTES, minutes);
                    }
                }, startHourPicker, startMinutesPicker, false);
                dialog.show();
                return true;
            }
        });

        final Preference endTime = findPreference(NOTIFICATIONS_END_VALUE);
        endHourPicker = dPrefs.getInt(NOTIFICATIONS_END_HOUR, 23);
        endMinutesPicker = dPrefs.getInt(NOTIFICATIONS_END_MINUTES, 0);
        endTime.setSummary(endSummary);

        endTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                        double decimal = ((double) minutes / 60);
                        String endPrefValue = Double.toString(hour + decimal);
                        editor.putString(NOTIFICATIONS_END_VALUE, endPrefValue);

                        boolean isPM = false;

                        int convertedHour = hour;
                        if (hour > 12) {
                            convertedHour = hour - 12;
                            isPM = true;
                        } else if (hour == 12) {
                            isPM = true;
                        }

                        StringBuilder builder = new StringBuilder();
                        builder.append(String.format(locale, "%2d:%02d", convertedHour, minutes));

                        if (!isPM) {
                            builder.append(" AM");
                        } else {
                            builder.append(" PM");
                        }
                        endTime.setSummary(builder.toString());
                        endHourPicker = hour;
                        endMinutesPicker = minutes;
                        editor.putString(NOTIFICATIONS_END_SUMMARY, builder.toString());
                        editor.putInt(NOTIFICATIONS_END_HOUR, hour);
                        editor.putInt(NOTIFICATIONS_END_MINUTES, minutes);
                    }
                }, endHourPicker, endMinutesPicker, false );
                dialog.show();
                return true;
            }
        });

    }

    public void savePreferences() {
        editor.apply();
        Toast.makeText(getActivity(), R.string.prefs_saved, Toast.LENGTH_SHORT).show();
    }

}
