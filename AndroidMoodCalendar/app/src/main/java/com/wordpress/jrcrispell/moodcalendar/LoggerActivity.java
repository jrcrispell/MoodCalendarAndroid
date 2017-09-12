package com.wordpress.jrcrispell.moodcalendar;

import android.app.ActionBar;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Locale;

public class LoggerActivity extends AppCompatActivity {

    private TextView startTime;
    private TextView endTime;
    int incomingStartTime;
    //Calendar incomingEndTime;
    EventDBSQLiteHelper dbHelper;
    Locale locale;
    double startDouble;
    double endDouble;
    String startDay;
    boolean editingExisting = false;
    int editingId = -1;


    private void setStartDouble(double start) {
        startDouble = start;
    }

    private void setEndDouble(double end) {
        endDouble = end;
    }

//TODO - get rid of military time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_logger_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        }
        else {
            locale = getResources().getConfiguration().locale;
        }

        dbHelper = EventDBSQLiteHelper.getInstance(this);

        // Unpack bundle
        Bundle extras = getIntent().getExtras();

        incomingStartTime = (int) extras.getDouble("startHour");
        startDay = extras.getString("startDay");
        editingExisting = extras.getBoolean("editingExistingEvent");

        startDouble = incomingStartTime;
        endDouble = incomingStartTime + 1;

        startTime = (TextView) findViewById(R.id.startTimeTV);
        endTime = (TextView) findViewById(R.id.endTimeTV);


        startTime.setText(String.format(locale, "%02d:%02d", incomingStartTime, 0));


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.logger_action_bar);
        TextView dateTV = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.headerTextView);
        dateTV.setText(MainActivity.convertDateString(startDay));

        ImageButton deleteButton = (ImageButton) findViewById(R.id.delete_button);
        final EditText descriptionET = (EditText) findViewById(R.id.descriptionET);
        final TextView moodTV = (TextView) findViewById(R.id.moodTV);

        CalendarEvent editingEvent;

        // Default values (if there's no existing activity)
        int startHourInt = (int) extras.getDouble("startHour");
        int startMinutesInt = 0;
        int endHourInt = startHourInt + 1;
        int endMinutesInt = 0;

        if (editingExisting) {
            editingId = extras.getInt("eventID");
            deleteButton.setVisibility(View.VISIBLE);

            editingEvent = dbHelper.getEvent(editingId);

            startHourInt = (int) editingEvent.getStartTime();
            double startHourFractional = editingEvent.getStartTime() - (int) editingEvent.getStartTime();
            startMinutesInt = (int) Math.round(startHourFractional * 60);

            endHourInt = (int) (editingEvent.getStartTime() + editingEvent.getDuration());
            double endHourFractional = editingEvent.getStartTime() + editingEvent.getDuration() - (int) (editingEvent.getStartTime() + editingEvent.getDuration());
            endMinutesInt = (int) Math.round(endHourFractional * 60);

            descriptionET.setText(editingEvent.getDescription());
            moodTV.setText(Integer.toString(editingEvent.getMoodScore()));
            Log.d("POOP", "onCreate: " + editingEvent);
        }

            startTime.setText(String.format(locale, "%02d:%02d", startHourInt, startMinutesInt));
            endTime.setText(String.format(locale, "%02d:%02d", endHourInt, endMinutesInt));


        final TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime.setText(String.format(locale, "%02d:%02d", hourOfDay, minute));
                double decimal = ((double) minute/60);
                setStartDouble(hourOfDay + decimal);
                validateTimes();
            }
        };

        final TimePickerDialog startTimePicker = new TimePickerDialog(LoggerActivity.this, startTimeListener, startHourInt, startMinutesInt, false);;
        startTimePicker.setTitle("Select Time");

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimePicker.show();
            }
        });


        final TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime.setText(String.format(locale, "%02d:%02d", hourOfDay, minute));
                double decimal = ((double) minute/60);
                setEndDouble(hourOfDay + decimal);
                validateTimes();
            }
        };

        final TimePickerDialog endTimePicker = new TimePickerDialog(LoggerActivity.this, endTimeListener, endHourInt, endMinutesInt, false);;
        startTimePicker.setTitle("Select Time");

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimePicker.show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoggerActivity.this);
                builder.setTitle(R.string.confirm_deletion);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.deleteEvent(editingId);
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing
                    }
                });
                builder.show();

            }
        });

        moodTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggerActivity.this);
                builder.setTitle(R.string.select_mood)
                .setItems(R.array.mood_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        moodTV.setText(" " + Integer.toString(index + 1) + " ");
                    }
                });
                builder.show();

            }
        });




        ImageButton saveButton = (ImageButton) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // The string resource file has a space before and after the mood score to increase
                // hitbox of the mood.
                String trimmedMood = moodTV.getText().toString().replace(" ", "");
                int mood = Integer.parseInt(trimmedMood);

                CalendarEvent newEvent = new CalendarEvent(startDouble, endDouble - startDouble, descriptionET.getText().toString(), mood, startDay);

                if (editingExisting) {
                    dbHelper.editEvent(newEvent, editingId);
                }

                else {
                    dbHelper.addEvent(newEvent);
                }
                finish();
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void validateTimes() {
        ImageButton saveButton = (ImageButton) findViewById(R.id.save_button);

        if (endDouble <= startDouble) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoggerActivity.this);
            builder.setTitle("Invalid times");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Do nothing
                }
            });
            builder.setMessage("End time must be later than start time!");
            builder.show();
            saveButton.setVisibility(View.GONE);
        }
        else {
            saveButton.setVisibility(View.VISIBLE);
        }
    }
}
