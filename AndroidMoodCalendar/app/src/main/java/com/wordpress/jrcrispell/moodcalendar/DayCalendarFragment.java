package com.wordpress.jrcrispell.moodcalendar;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    boolean editMode = false;

    DayCalendarFragmentListener listener;

    EventDBSQLiteHelper eventDBHelper;

    boolean longClickDetected = false;

    interface DayCalendarFragmentListener {
        void openLoggerActivity(Intent intent);
        ArrayList<CalendarEvent> getDaysEvents();
        void setDaysEvents(ArrayList<CalendarEvent> daysEvents);
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
        todaysDateString = mainActivity.selectedDate;

        listener.setDaysEvents(eventDBHelper.getDaysEvents(todaysDateString));

        // Calendar touch event
        final DayCalendarView dayCalendarView = new DayCalendarView(getActivity());
        dayCalendarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                DayCalendarView dcView = (DayCalendarView) v;

                if (event.getAction() != 1 || event.getX() < dcView.hourLabelXStart) {
                    return false;
                }

                hourVerticalPoints = dayCalendarView.hourVerticalPoints;
                hourLineTopPadding = dayCalendarView.hourLineTopPadding;

                if (!longClickDetected) {


                    sendStartHour = (event.getY() - hourLineTopPadding) / hourVerticalPoints;

                    for (int i=0; i < listener.getDaysEvents().size(); i++) {
                        CalendarEvent calendarEvent = listener.getDaysEvents().get(i);
                        if (calendarEvent.getStartTime() <= sendStartHour && sendStartHour <= calendarEvent.getStartTime() + calendarEvent.getDuration()) {
                            Intent intent = new Intent(getActivity(), LoggerActivity.class);
                            intent.putExtra("startHour", sendStartHour);
                            intent.putExtra("startDay", todaysDateString);
                            intent.putExtra("editingExistingEvent", true);
                            intent.putExtra("eventID", calendarEvent.getDbEventID());
                            listener.openLoggerActivity(intent);
                            return true;
                        }
                    }


                    Intent intent = new Intent(getActivity(), LoggerActivity.class);
                    intent.putExtra("startHour", sendStartHour);
                    intent.putExtra("startDay", todaysDateString);
                    intent.putExtra("editingExistingEvent", false);
                    listener.openLoggerActivity(intent);
                    return true;
                }
                // Handle long click
                else {
                    longClickDetected = false;

                    for (int i=0; i < listener.getDaysEvents().size(); i++) {

                        CalendarEvent calendarEvent = listener.getDaysEvents().get(i);

                        double eventStartLocation = timeToYLocation(calendarEvent.getStartTime());
                        double eventEndLocation = timeToYLocation(calendarEvent.getStartTime() + calendarEvent.getDuration());
                        double touchEventLocation = event.getY();

                        if (event.getY() > eventStartLocation && event.getY() < eventEndLocation) {
                            Toast.makeText(getActivity(), "Editing event", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                }

                return false;

            }
        });


        dayCalendarView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                longClickDetected = true;
                editMode = true;
                return true;
            }
        });

        return dayCalendarView;
    }

    private void endEditingMode() {
        //TODO - Remove lines

        //TODO - save

        editMode = false;

    }

    public double timeToYLocation(double time) {
        double result = hourLineTopPadding + time * hourVerticalPoints;
        return result;
    }

    public double yLocationToTime(double yLocation) {
        return (yLocation - hourLineTopPadding) / hourVerticalPoints;
    }

}