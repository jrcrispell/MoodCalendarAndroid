package com.wordpress.jrcrispell.moodcalendar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public EventDBSQLiteHelper eventDBHelper;
    String todaysDate;
    Calendar currentInstance;
    DayCalendarFragment calendarFragment;
    DayCalendarView calendarView;
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Get today's date
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        }
        else {
            locale = getResources().getConfiguration().locale;
        }

        currentInstance = Calendar.getInstance();
        todaysDate = new SimpleDateFormat("MM-dd-yyyy", locale).format(currentInstance.getTime());

        eventDBHelper = new EventDBSQLiteHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void configureButtons(){

        final TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        dateTextView.setText(convertDateString(todaysDate));

        ImageButton previousDayButton = (ImageButton) findViewById(R.id.previousDayButton);
        previousDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInstance.add(Calendar.DATE, -1);
                calendarFragment = (DayCalendarFragment) getSupportFragmentManager().findFragmentById(R.id.DayCalendarFragment);
                calendarView = (DayCalendarView) calendarFragment.getView();

                String newDate = new SimpleDateFormat("MM-dd-yyyy", locale).format(currentInstance.getTime());
                dateTextView.setText(convertDateString(newDate));
                calendarView.daysEvents = eventDBHelper.getDaysEvents(newDate);
                calendarView.invalidate();
            }
        });

        ImageButton nextDayButton = (ImageButton) findViewById(R.id.nextDayButton);
        nextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInstance.add(Calendar.DATE, 1);
                calendarFragment = (DayCalendarFragment) getSupportFragmentManager().findFragmentById(R.id.DayCalendarFragment);
                calendarView = (DayCalendarView) calendarFragment.getView();

                String newDate = new SimpleDateFormat("MM-dd-yyyy", locale).format(currentInstance.getTime());
                dateTextView.setText(convertDateString(newDate));
                calendarView.daysEvents = eventDBHelper.getDaysEvents(newDate);
                calendarView.invalidate();
            }
        });
    }

    private String convertDateString(String date) {
        String[] dateArray = date.split("-");
        String month;

        switch (dateArray[0]) {
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "June";
                break;
            case "07":
                month = "July";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sept";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
            default:
                month = "Error";
                break;
        }

        String day = dateArray[1];
        if (day.indexOf("0") == 0) {
            day = day.replace("0", "");
        }
        return month + " " + day + ", " + dateArray[2];
    }
}
