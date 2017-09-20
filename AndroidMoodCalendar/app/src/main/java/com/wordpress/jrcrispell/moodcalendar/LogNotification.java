package com.wordpress.jrcrispell.moodcalendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class LogNotification extends IntentService {

    Locale locale;

    public LogNotification() {
        super("LogNotification");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        EventDBSQLiteHelper eventDBHelper = EventDBSQLiteHelper.getInstance(this);

        // Get today's date
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        }
        else {
            locale = getResources().getConfiguration().locale;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        // Find previous hour, account for midnight
        int prevHour;
        if (currentHour == 0) {
            prevHour = 23;
        }
        else {
            prevHour = currentHour - 1;
        }


        String todaysDate = new SimpleDateFormat("MM-dd-yyyy", locale).format(calendar.getTime());

        ArrayList<CalendarEvent> daysEvents = eventDBHelper.getDaysEvents(todaysDate);
        boolean prevHourLogged = false;
        for (CalendarEvent event : daysEvents) {
            double start = event.getStartTime();
            double end = event.getStartTime() + event.getDuration();

            // Check to see if an activity has been logged for the previous hour.
            if (end > prevHour) {
                prevHourLogged = true;
            }
        }

        if (!prevHourLogged) {
            // Build notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_save_white_24dp);
            builder.setContentTitle("Log activity");
            builder.setContentText("Log activity for TIME");
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_settings_white_24dp));
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            builder.setDefaults(2);


            // Settings button
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            PendingIntent settingsPendingIntent = PendingIntent.getActivity(this, 0, settingsIntent, 0);
            builder.addAction(R.drawable.ic_settings_white_24dp, "Settings", settingsPendingIntent);

            //TODO - add functionality
            // Snooze button
            Intent settingsIntent2 = new Intent(this, SettingsActivity.class);
            PendingIntent settingsPendingIntent2 = PendingIntent.getActivity(this, 0, settingsIntent2, 0);
            builder.addAction(R.drawable.ic_settings_white_24dp, "Snooze", settingsPendingIntent2);

            // Set content intent
            builder.setAutoCancel(true);
            Intent loggerIntent = new Intent(this, LoggerActivity.class);
            double prevHourDouble = prevHour;
            loggerIntent.putExtra("startHour", prevHourDouble);
            loggerIntent.putExtra("startDay", todaysDate);
            loggerIntent.putExtra("editingExistingEvent", false);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 1, loggerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentPendingIntent);

            // Send notification
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

        }

        // Run again in an hour
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, LogNotification.class);
        PendingIntent notificationPendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);

        // Cancel if there's an existing intent
        alarmManager.cancel(notificationPendingIntent);

        // Schedule notification for 5 min after next hour.
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 5);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notificationPendingIntent);
    }
}