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
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
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

    private static final String TAG = "DayCalendarFragment";

    ScrollView scrollView;
    int handleSelected = -1;

    ArrayList<Double> draggableYLocs;

    int handleHitBox = DayCalendarView.hourVerticalPoints/4;


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

        scrollView = (ScrollView) getActivity().findViewById(R.id.scroll_view);

        MainActivity mainActivity = (MainActivity) getActivity();
        todaysDateString = mainActivity.selectedDate;

        listener.setDaysEvents(eventDBHelper.getDaysEvents(todaysDateString));

        // Calendar touch event
        final DayCalendarView dayCalendarView = new DayCalendarView(getActivity());
        dayCalendarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                if (editMode) {
                    if (action == MotionEvent.ACTION_DOWN) {
                        double topHandleY = draggableYLocs.get(0);
                        double bottomHandleY = draggableYLocs.get(1);
                        float actionY = event.getY();
                        if (actionY > topHandleY - handleHitBox && actionY < topHandleY + handleHitBox) {
                            Log.d(TAG, "onTouch: top handle grabbed");
                            handleSelected = 0;
                            return true;
                        }
                        else if (actionY > bottomHandleY - handleHitBox && actionY < bottomHandleY + handleHitBox) {
                            handleSelected = 1;
                        }
                    }
                }

                DayCalendarView dcView = (DayCalendarView) v;
                Log.d(TAG, "onTouch action " + Integer.toString(event.getAction()));


                if (event.getAction() != 1 || event.getX() < dcView.hourLabelXStart) {
                    return false;
                }




                hourVerticalPoints = dayCalendarView.hourVerticalPoints;
                hourLineTopPadding = dayCalendarView.hourLineTopPadding;


                if (!longClickDetected) {

                    if (editMode) {
                        Toast.makeText(getActivity(), "end edit mode", Toast.LENGTH_SHORT).show();
                        endEditingMode(v);
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
                            beginEditingMode(calendarEvent, eventStartLocation, eventEndLocation, v);
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

        dayCalendarView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (!editMode) {
                    return false;
                }
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.d(TAG, "started ");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(TAG, "entered");
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.d(TAG, "location");
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.d(TAG, "ended");
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.d(TAG, "drop");
                }
                    return true;
            }
        });

        return dayCalendarView;
    }

    private void beginEditingMode(CalendarEvent calendarEvent, double eventStartLocation, double eventEndLocation, View v) {
        editingEvent = calendarEvent;
        Toast.makeText(getActivity(), "Editing event", Toast.LENGTH_SHORT).show();

        draggableYLocs = new ArrayList<>();
        draggableYLocs.add(eventStartLocation);
        draggableYLocs.add(eventEndLocation);
        listener.setDraggableYLocs(draggableYLocs);

        editingEvent.setBeingEdited(true);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        v.invalidate();
    }

    private void endEditingMode(View v) {
        editMode = false;
        editingEvent.setBeingEdited(false);
        listener.setDraggableYLocs(new ArrayList<Double>());
        v.invalidate();

        scrollView.setOnTouchListener(null);

        //TODO - save
    }

    public double timeToYLocation(double time) {
        double result = hourLineTopPadding + time * hourVerticalPoints;
        return result;
    }

    public double yLocationToTime(double yLocation) {
        return (yLocation - hourLineTopPadding) / hourVerticalPoints;
    }

}