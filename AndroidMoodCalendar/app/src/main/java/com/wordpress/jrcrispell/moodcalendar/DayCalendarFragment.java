package com.wordpress.jrcrispell.moodcalendar;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


public class DayCalendarFragment extends Fragment {

    private double sendStartHour;
    int hourVerticalPoints;
    int hourLineTopPadding;
    String todaysDateString;

    DayCalendarFragmentListener listener;

    ArrayList<CalendarEvent> daysEvents;
    EventDBSQLiteHelper eventDBHelper;

    boolean longClickDetected = false;

    interface DayCalendarFragmentListener {
        void openLoggerActivity(Intent intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DayCalendarFragmentListener) {
            listener = (DayCalendarFragmentListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDBHelper = EventDBSQLiteHelper.getInstance(getActivity());
    }

    public static DayCalendarFragment newInstance() {
        return new DayCalendarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getDayCalendarView();
    }

    private View getDayCalendarView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        todaysDateString = mainActivity.todaysDate;

        daysEvents = eventDBHelper.getDaysEvents(todaysDateString);

        // Calendar touch event
        final DayCalendarView dayCalendarView = new DayCalendarView(getActivity(), daysEvents);
        dayCalendarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                DayCalendarView dcView = (DayCalendarView) v;

                if (event.getAction() != 1 || event.getX() < dcView.hourLabelXStart) {
                    return false;
                }

                if (!longClickDetected) {
                    hourVerticalPoints = dayCalendarView.hourVerticalPoints;
                    hourLineTopPadding = dayCalendarView.hourLineTopPadding;
                    sendStartHour = (event.getY() - hourLineTopPadding) / hourVerticalPoints;

                    Intent intent = new Intent(getActivity(), DisplayLoggerActivity.class);
                    intent.putExtra("startHour", sendStartHour);
                    intent.putExtra("startDay", todaysDateString);
                    listener.openLoggerActivity(intent);
                    return true;
                }

                longClickDetected = false;
                return false;

            }
        });


        dayCalendarView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                longClickDetected = true;

                Toast.makeText(getActivity(), "long click", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return dayCalendarView;
    }
}


