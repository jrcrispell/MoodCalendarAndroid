package com.wordpress.jrcrispell.moodcalendar;

import android.app.usage.UsageEvents;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.security.Guard;
import java.util.ArrayList;
import java.util.HashMap;


public class DayCalendarFragment extends Fragment {

    private double sendStartHour;
    int hourVerticalPoints;
    int hourLineTopPadding;
    String todaysDateString;

    ArrayList<CalendarEvent> daysEvents;
    EventDBSQLiteHelper eventDBHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        eventDBHelper = new EventDBSQLiteHelper(getActivity());

        MainActivity mainActivity = (MainActivity) getActivity();
        todaysDateString = mainActivity.todaysDate;
        Log.d("debug", todaysDateString);

        daysEvents = eventDBHelper.getDaysEvents(todaysDateString);

        // Calendar touch event
        final DayCalendarView dayCalendarView = new DayCalendarView(getActivity(), daysEvents);
        dayCalendarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getX() < 163) return false;
                hourVerticalPoints = dayCalendarView.hourVerticalPoints;
                hourLineTopPadding = dayCalendarView.hourLineTopPadding;
                sendStartHour = (event.getY() - hourLineTopPadding) / hourVerticalPoints;

                Intent intent = new Intent(getActivity(), DisplayLoggerView.class);
                intent.putExtra("startHour", sendStartHour);
                startActivity(intent);
                return false;

            }
        });


        dayCalendarView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(getActivity(), "long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return dayCalendarView;
    }
}


