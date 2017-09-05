package com.wordpress.jrcrispell.moodcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.util.ArrayList;

//TODO - have a stand-in message when no contacts are added "tap screen to blah"

public class DayCalendarView extends View {

    private Paint hourLinePaint;
    private Paint textPaint;
    private Paint borderPaint;

    int hourLineXStart = 170;
    int hourVerticalPoints = 120;
    int hourLineXEnd = 1200;
    int hourLineHeight = 5;
    int hourLineTopPadding = 50;
    int hourLabelXStart = 20;
    int hourLabelYStart = hourLineTopPadding + 20;
    int eventLabelXPadding = 10;
    int eventBorderHeight = 5;

    ArrayList<Rect> eventRectangles = new ArrayList<>();
    ArrayList<String> eventDescriptions = new ArrayList<>();
    ArrayList<String> eventScores = new ArrayList<>();
    ArrayList<Integer> hourLinesToDraw = new ArrayList<>();
    ArrayList<Integer> hourLinesToOmit = new ArrayList<>();

    ArrayList<CalendarEvent> daysEvents;
    Context context;

    public DayCalendarView(Context context, ArrayList<CalendarEvent> daysEvents) {
        super(context);

        this.daysEvents = daysEvents;
        this.context = context;

    }

    public void drawCalendar() {

        eventRectangles = new ArrayList<>();
        eventDescriptions = new ArrayList<>();
        eventScores = new ArrayList<>();
        hourLinesToDraw = new ArrayList<>();
        hourLinesToOmit = new ArrayList<>();


        for (int i=0; i<24; i++) {
            hourLinesToDraw.add(i);
        }

        for (int i=0; i<daysEvents.size(); i++) {
            CalendarEvent event = daysEvents.get(i);
            int startY = (int) (event.getStartTime() * hourVerticalPoints) + hourLineTopPadding;
            int endY = (int) (event.getDuration() * hourVerticalPoints) + startY;
            eventRectangles.add(new Rect(hourLineXStart, startY, hourLineXEnd, endY));
            eventDescriptions.add(event.getDescription());
            eventScores.add(Integer.toString(event.getMoodScore()));

            // Omit hour lines that already have an event.
            for (int hour : hourLinesToDraw) {
                if (hour >= event.getStartTime() && hour <= event.getStartTime() + event.getDuration()) {
                    hourLinesToOmit.add(hour);
                }
            }
        }

        for (int hour : hourLinesToOmit) {
            if (hourLinesToDraw.contains(hour)) {
                hourLinesToDraw.remove(hour);
            }
        }

        // create the Paint and set its color
        hourLinePaint = new Paint();
        hourLinePaint.setColor(ContextCompat.getColor(context, R.color.white20Percent));

        borderPaint = new Paint();
        borderPaint.setColor(ContextCompat.getColor(context, R.color.eventBorderColor));

        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(context, R.color.white80Percent));
        textPaint.setTextSize(50);
    }



    @Override
    protected void onDraw(Canvas canvas) {

        drawCalendar();

        // Draw events
        for (int i=0; i<eventRectangles.size(); i++) {

            Rect rect = eventRectangles.get(i);
            Rect topBorder = new Rect(rect.left, rect.top - eventBorderHeight/2, rect.right, rect.top + eventBorderHeight/2);
            Rect bottomBorder = new Rect(rect.left, rect.bottom - eventBorderHeight/2, rect.right, rect.bottom + eventBorderHeight/2);

            canvas.drawRect(rect, hourLinePaint);
            canvas.drawRect(topBorder, borderPaint);
            canvas.drawRect(bottomBorder, borderPaint);

            // Math to center text vertically in event rectangle
            int descriptionY = rect.top + (rect.bottom - rect.top)/2 - (int) ((textPaint.ascent() + textPaint.descent())/2);
            int scoreY = rect.top + (rect.bottom - rect.top)/2 - (int) ((textPaint.ascent() + textPaint.descent())/2);

            canvas.drawText(eventDescriptions.get(i), rect.left + eventLabelXPadding, descriptionY, textPaint);

            String scoreString = eventScores.get(i);

            if ( Integer.parseInt(eventScores.get(i)) < 10) {
                scoreString = "  " + scoreString;
            }

            canvas.drawText(scoreString, rect.right - 200, scoreY, textPaint);
        }

        // Hour lines
        for (int i=0; i<24; i++) {
            int top = (i * hourVerticalPoints) + hourLineTopPadding;

            Rect hourLine = new Rect(hourLineXStart, top, hourLineXEnd, top + hourLineHeight);

            if (!hourLinesToOmit.contains(i)) canvas.drawRect(hourLine, hourLinePaint);

            // Hour labels
            String hour = "";
            if (i == 0 || i == 12) {
                hour = "12:00";
            }
            else if (i < 12) {
                hour = Integer.toString(i) + ":00";
            }
            else {
                hour = Integer.toString(i - 12) + ":00";
            }

            canvas.drawText(hour, hourLabelXStart, (i * hourVerticalPoints) + hourLabelYStart, textPaint);
        }

    }
}