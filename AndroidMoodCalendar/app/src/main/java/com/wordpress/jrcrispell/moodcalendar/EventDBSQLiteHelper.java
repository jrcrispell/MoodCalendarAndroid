package com.wordpress.jrcrispell.moodcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.EventLogTags;
import android.util.Log;

import java.util.ArrayList;


public class EventDBSQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MCEventDatabase.db";
    private static final int DATABASE_VERSION = 1;
    public static final String CALENDAREVENT_TABLE_NAME = "CalendarEvent";
    public static final String CALENDAREVENT_COLUMN_ID = "_id";
    public static final String CALENDAREVENT_COLUMN_STARTTIME = "startTime";
    public static final String CALENDAREVENT_COLUMN_DURATION = "duration";
    public static final String CALENDAREVENT_COLUMN_DESCRIPTION = "description";
    public static final String CALENDAREVENT_COLUMN_MOODSCORE = "moodScore";
    public static final String CALENDAREVENT_COLUMN_STARTDAY = "startDay";

    public static final String STARTDAY_TABLE_NAME = "StartDay";
    public static final String STARTDAY_COLUMN_STARTDAY = "startDay";
    public static final String STARTDAY_COLUMN_EVENTID = "event_id";

    private static EventDBSQLiteHelper INSTANCE = null;

    private SQLiteDatabase database;

    public static EventDBSQLiteHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new EventDBSQLiteHelper(context);
        }
        return INSTANCE;
    }

    private EventDBSQLiteHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String eventTable = "CREATE TABLE " + CALENDAREVENT_TABLE_NAME + "(" +
                CALENDAREVENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CALENDAREVENT_COLUMN_STARTDAY + " TEXT, " +
                CALENDAREVENT_COLUMN_STARTTIME + " REAL, " +
                CALENDAREVENT_COLUMN_DURATION + " REAL, " +
                CALENDAREVENT_COLUMN_DESCRIPTION + " TEXT, " +
                CALENDAREVENT_COLUMN_MOODSCORE + " INTEGER)";
        String startDayTable = "CREATE TABLE " + STARTDAY_TABLE_NAME + "(" +
                STARTDAY_COLUMN_STARTDAY + " TEXT PRIMARY KEY, " +
                STARTDAY_COLUMN_EVENTID + " INTEGER ," +
                " FOREIGN KEY(" + STARTDAY_COLUMN_EVENTID + ") REFERENCES " +
                CALENDAREVENT_TABLE_NAME + "(" + CALENDAREVENT_COLUMN_ID + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE);";

        db.execSQL(eventTable);
        db.execSQL(startDayTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + CALENDAREVENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STARTDAY_TABLE_NAME);

        onCreate(db);
    }

    public void addEvent(CalendarEvent event) {

        ContentValues values = new ContentValues();
        values.put(CALENDAREVENT_COLUMN_STARTDAY, event.getStartDay());
        values.put(CALENDAREVENT_COLUMN_STARTTIME, event.getStartTime());
        values.put(CALENDAREVENT_COLUMN_DURATION, event.getDuration());
        values.put(CALENDAREVENT_COLUMN_DESCRIPTION, event.getDescription());
        values.put(CALENDAREVENT_COLUMN_MOODSCORE, event.getMoodScore());
        database.insert(CALENDAREVENT_TABLE_NAME, null, values);

        String query = "SELECT * FROM " + CALENDAREVENT_TABLE_NAME + " ORDER BY " + CALENDAREVENT_COLUMN_ID + " DESC LIMIT 1";
        Cursor cursor = database.rawQuery(query, null);

        int newEventID = -1;

        if (cursor.moveToFirst()) {
            do {
                newEventID = cursor.getInt(0);
                event.setDbEventID(newEventID);
            } while (cursor.moveToNext());
        }

        if (newEventID != -1) {
            values = new ContentValues();
            values.put(STARTDAY_COLUMN_STARTDAY, event.getStartDay());
            values.put(STARTDAY_COLUMN_EVENTID, newEventID);
            database.insert(STARTDAY_TABLE_NAME, null, values);
        }

        query = "SELECT * FROM " + STARTDAY_TABLE_NAME;
        cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String result = cursor.getString(0) + " " + Integer.toString(cursor.getInt(1));
                Log.d("debug", result);
            } while (cursor.moveToNext());
        }

        cursor.close();

    }

    public void addStartDay(CalendarEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put()
    }

    public ArrayList<CalendarEvent> getDaysEvents(String dateString) {
        ArrayList<CalendarEvent> daysEvents = new ArrayList<>();

        String query = "SELECT * FROM " + CALENDAREVENT_TABLE_NAME + " WHERE " + CALENDAREVENT_COLUMN_STARTDAY + " = '" + dateString + "'";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                CalendarEvent event = new CalendarEvent();
                event.setStartDay(dateString);
                event.setDbEventID(cursor.getInt(0));
                event.setStartTime(cursor.getDouble(2));
                event.setDuration(cursor.getDouble(3));
                event.setDescription(cursor.getString(4));
                event.setMoodScore(cursor.getInt(5));

                daysEvents.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return daysEvents;
    }
}
