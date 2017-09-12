package com.wordpress.jrcrispell.moodcalendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DayCalendarFragment.DayCalendarFragmentListener {

    public EventDBSQLiteHelper eventDBHelper;
    Calendar currentInstance;
    DayCalendarFragment calendarFragment;
    DayCalendarView calendarView;
    Locale locale;
    String selectedDate;
    ActionBar actionBar;

    ArrayList<CalendarEvent> daysEvents;
    ArrayList<Double> draggableYLocs = new ArrayList<>();

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
        selectedDate = new SimpleDateFormat("MM-dd-yyyy", locale).format(currentInstance.getTime());

        eventDBHelper = EventDBSQLiteHelper.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.main_action_bar);


        configureButtons();
        getFragmentManager().beginTransaction().add(R.id.dayCalendarFragmentContainer, DayCalendarFragment.newInstance()).commit();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getFragmentManager().beginTransaction().replace(R.id.dayCalendarFragmentContainer, DayCalendarFragment.newInstance()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void configureButtons(){

        final String year = new SimpleDateFormat("yyyy", locale).format(currentInstance.getTime());
        final String month = new SimpleDateFormat("MM", locale).format(currentInstance.getTime());
        final String day = new SimpleDateFormat("dd", locale).format(currentInstance.getTime());

        final TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        dateTextView.setText(convertDateString(selectedDate));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        currentInstance.set(year, month, day);
                        selectedDate = new SimpleDateFormat("MM-dd-yyyy", locale).format(currentInstance.getTime());
                        refreshView();

                    }
                }, Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day)); // month is actually zero indexed wtf

                dialog.show();


                //getFragmentManager().beginTransaction().replace(R.id.MonthPickerFragmentContainer, DatePickerFragment.newInstance()).commit();
            }
        });

        ImageButton previousDayButton = (ImageButton) findViewById(R.id.previousDayButton);
        previousDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInstance.add(Calendar.DATE, -1);
                calendarFragment = (DayCalendarFragment) getFragmentManager().findFragmentById(R.id.dayCalendarFragmentContainer);
                calendarView = (DayCalendarView) calendarFragment.getView();

                selectedDate = new SimpleDateFormat("MM-dd-yyyy", locale).format(currentInstance.getTime());
                dateTextView.setText(convertDateString(selectedDate));

                daysEvents = eventDBHelper.getDaysEvents(selectedDate);
                refreshView();
            }
        });

        ImageButton nextDayButton = (ImageButton) findViewById(R.id.nextDayButton);
        nextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInstance.add(Calendar.DATE, 1);
                calendarFragment = (DayCalendarFragment) getFragmentManager().findFragmentById(R.id.dayCalendarFragmentContainer);
                calendarView = (DayCalendarView) calendarFragment.getView();

                selectedDate = new SimpleDateFormat("MM-dd-yyyy", locale).format(currentInstance.getTime());
                dateTextView.setText(convertDateString(selectedDate));
                daysEvents = eventDBHelper.getDaysEvents(selectedDate);
                refreshView();

            }
        });

    }

    public static String convertDateString(String date) {
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

    @Override
    public void openLoggerActivity(Intent intent) {
        startActivity(intent);
    }

    public ArrayList<CalendarEvent> getDaysEvents() {
        return daysEvents;
    }

    public void setDaysEvents(ArrayList<CalendarEvent> daysEvents) {
        this.daysEvents = daysEvents;
    }

    @Override
    public ArrayList<Double> getDraggableYLocs() {
        return draggableYLocs;
    }

    @Override
    public void setDraggableYLocs(ArrayList<Double> draggableYLocs) {
        this.draggableYLocs = draggableYLocs;
    }


    public void refreshView() {
        getFragmentManager().beginTransaction().replace(R.id.dayCalendarFragmentContainer, DayCalendarFragment.newInstance()).commit();
        configureButtons();
    }
}