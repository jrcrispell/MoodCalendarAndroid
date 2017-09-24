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

    ArrayList<Float> draggableYLocs;

    int handleHitBox = DayCalendarView.hourVerticalPoints/4;


    interface DayCalendarFragmentListener {
        void openLoggerActivity(Intent intent);
        ArrayList<CalendarEvent> getDaysEvents();
        void setDaysEvents(ArrayList<CalendarEvent> daysEvents);
        ArrayList<Float> getDraggableYLocs();
        void setDraggableYLocs(ArrayList<Float> draggableYLocs);
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

                    if (handleSelected > -1) {
                        if (action == MotionEvent.ACTION_MOVE) {
                            Log.d(TAG, "onTouch: MOVING HANDLE" + action);
                            draggableYLocs.set(handleSelected, event.getY());
                            listener.setDraggableYLocs(draggableYLocs);

                            // Top handle
                            if (handleSelected == 0) {
                                double originalStartTime = editingEvent.getStartTime();
                                double originalDuration = editingEvent.getDuration();
                                Log.d(TAG, "original start: " + originalStartTime);
                                Log.d(TAG, "onTouch: original duration: " + editingEvent.getDuration());

                                editingEvent.setStartTime(yLocationToTime(event.getY()));
                                double timeDifference = originalStartTime - editingEvent.getStartTime();
                                double originalEnd = originalStartTime + editingEvent.getDuration();
                                editingEvent.setDuration(editingEvent.getDuration() + timeDifference);

                                // Prevent start time/end time flipping
                                if (editingEvent.getStartTime() > originalEnd) {
                                    editingEvent.setStartTime(originalEnd - 0.1);
                                    editingEvent.setDuration(0.1);

                                    // hide handle
                                    draggableYLocs.set(handleSelected, -1000.0f);

                                }

                                Log.d(TAG, "onTouch: difference: " + timeDifference);


                                Log.d(TAG, "onTouch: new duration: " + editingEvent.getDuration());

                                eventDBHelper.editEvent(editingEvent, editingEvent.getDbEventID());
                            }
                            else if (handleSelected == 1) {
                                double originalStartTime = editingEvent.getStartTime();
                                Log.d(TAG, "original start: " + originalStartTime);
                                Log.d(TAG, "onTouch: original duration: " + editingEvent.getDuration());
                                double newEnd = yLocationToTime(event.getY());
                                Log.d(TAG, "onTouch: newEnd: " + newEnd);

                                double newDuration = newEnd - editingEvent.getStartTime();

                                if (newDuration < 0) {
                                    newDuration = 0.1;
                                    draggableYLocs.set(handleSelected, -1000.0f);
                                }

                                editingEvent.setDuration(newDuration);
                                eventDBHelper.editEvent(editingEvent, editingEvent.getDbEventID());
                            }

                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            v.invalidate();
                            return true;
                        }
                    }



                    if (action == MotionEvent.ACTION_DOWN) {
                        float topHandleY = draggableYLocs.get(0);
                        float bottomHandleY = draggableYLocs.get(1);
                        float actionY = event.getY();
                        if (actionY > topHandleY - handleHitBox && actionY < topHandleY + handleHitBox) {
                            Log.d(TAG, "onTouch: top handle grabbed");
                            handleSelected = 0;
                            return true;
                        }
                        else if (actionY > bottomHandleY - handleHitBox && actionY < bottomHandleY + handleHitBox) {
                            Log.d(TAG, "onTouch: bot handle grabbed");
                            handleSelected = 1;
                        }
                    }
                }

                DayCalendarView dcView = (DayCalendarView) v;

                if (event.getAction() != 1 || event.getX() < dcView.hourLabelXStart) {
                    Log.d(TAG, "onTouch action returning - " + Integer.toString(event.getAction()));

                    return false;

                }

                hourVerticalPoints = dayCalendarView.hourVerticalPoints;
                hourLineTopPadding = dayCalendarView.hourLineTopPadding;


                if (!longClickDetected) {

                    if (editMode) {
                        Toast.makeText(getActivity(), "end edit mode", Toast.LENGTH_SHORT).show();
                        endEditingMode(v);
                        Log.d(TAG, "end edit mode");
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
                else if (handleSelected == -1) {
                    longClickDetected = false;
                    Log.d(TAG, "handling long click");


                    for (int i=0; i < listener.getDaysEvents().size(); i++) {

                        CalendarEvent calendarEvent = listener.getDaysEvents().get(i);

                        float eventStartLocation = timeToYLocation(calendarEvent.getStartTime());
                        float eventEndLocation = timeToYLocation(calendarEvent.getStartTime() + calendarEvent.getDuration());

                        if (event.getY() > eventStartLocation && event.getY() < eventEndLocation) {
                            beginEditingMode(calendarEvent, eventStartLocation, eventEndLocation, v);
                            return true;
                        }
                    }
                }
                Log.d(TAG, "handling long click returning false");
                endEditingMode(v);

                return false;

            }
        });



        dayCalendarView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Log.d(TAG, "long click detected");

                longClickDetected = true;
                editMode = true;
                return false;
            }
        });

        return dayCalendarView;
    }

    private void beginEditingMode(CalendarEvent calendarEvent, float eventStartLocation, float eventEndLocation, View v) {
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
        listener.setDraggableYLocs(new ArrayList<Float>());
        handleSelected = -1;
        v.invalidate();

        scrollView.setOnTouchListener(null);


        //TODO - save
    }

    public float timeToYLocation(double time) {
        float timeFloat = (float) time;
        return hourLineTopPadding + timeFloat * hourVerticalPoints;
    }


    public double yLocationToTime(float yLocation) {
        return (yLocation - hourLineTopPadding) / hourVerticalPoints;
    }

}