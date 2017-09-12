package com.wordpress.jrcrispell.moodcalendar;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    CalendarEvent editingEvent;


    interface DayCalendarFragmentListener {
        void openLoggerActivity(Intent intent);
        ArrayList<CalendarEvent> getDaysEvents();
        void setDaysEvents(ArrayList<CalendarEvent> daysEvents);
        ArrayList<Double> getDraggableYLocs();
        void setDraggableYLocs(ArrayList<Double> draggableYLocs);
        void refreshView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DayCalendarFragmentListener) {
            listener = (DayCalendarFragmentListener) context;
        }
        Log.d("TAG", "onAttach: wtf");

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

                    if (editMode) {
                        Toast.makeText(getActivity(), "end edit mode", Toast.LENGTH_SHORT).show();
                        endEditingMode();
                        return true;
                    }

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

                        if (event.getY() > eventStartLocation && event.getY() < eventEndLocation) {
                            editingEvent = calendarEvent;
                            Toast.makeText(getActivity(), "Editing event", Toast.LENGTH_SHORT).show();

//                            ImageView imageView = new ImageView(getActivity());
//                            imageView.setImageResource(R.drawable.ic_drag_handle_black_24dp);
//                            imageView.setY((float)eventStartLocation);
//                            ArrayList<View> touchables = new ArrayList<View>();
//                            touchables.add(imageView);
//                            v.addTouchables(touchables);

                            ArrayList<Double> draggableYLocs = new ArrayList<Double>();
                            draggableYLocs.add(eventStartLocation);
                            listener.setDraggableYLocs(draggableYLocs);

                            editingEvent.setBeingEdited(true);


                            v.invalidate();
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
        editingEvent.setBeingEdited(false);

    }

    public double timeToYLocation(double time) {
        double result = hourLineTopPadding + time * hourVerticalPoints;
        return result;
    }

    public double yLocationToTime(double yLocation) {
        return (yLocation - hourLineTopPadding) / hourVerticalPoints;
    }

}