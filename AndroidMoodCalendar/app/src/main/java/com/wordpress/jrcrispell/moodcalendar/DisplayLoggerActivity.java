package com.wordpress.jrcrispell.moodcalendar;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DisplayLoggerActivity extends AppCompatActivity {

    private TextView startTime;
    private TextView endTime;
    int incomingStartTime;
    //Calendar incomingEndTime;
    EventDBSQLiteHelper dbHelper;
    Locale locale;
    double startDouble;
    double endDouble;
    String startDay;


    private void setStartDouble(double start) {
        startDouble = start;
    }

    private void setEndDouble(double end) {
        endDouble = end;
    }


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




        endTime = (TextView) findViewById(R.id.endTimeTV);

        Bundle extras = getIntent().getExtras();

        incomingStartTime = (int) extras.getDouble("startHour");
        startDay = extras.getString("startDay");

        final TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        dateTextView.setText(MainActivity.convertDateString(startDay));

        startDouble = incomingStartTime;
        endDouble = incomingStartTime + 1;

        startTime = (TextView) findViewById(R.id.startTimeTV);

        startTime.setText(String.format(locale, "%02d:%02d", incomingStartTime, 0));



        //TODO - fix bug where popup dialog doesn't update after a time has been picked.
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int startMin = 0;

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(DisplayLoggerActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTime.setText(String.format(locale, "%02d:%02d", hourOfDay, minute));
                        double decimal = ((double) minute/60);
                        setStartDouble(hourOfDay + decimal);
                    }
                }, incomingStartTime, startMin, false);
                timePicker.setTitle("Select Time");
                timePicker.show();

            }
        });

        endTime.setText(String.format(locale, "%02d:%02d", incomingStartTime + 1, 0));

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int startMin = 0;

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(DisplayLoggerActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTime.setText(String.format(locale, "%02d:%02d", hourOfDay, minute));
                        double decimal = ((double) minute/60);
                        setEndDouble(hourOfDay + decimal);
                    }
                }, incomingStartTime + 1, startMin, false);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });



        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView moodTV = (TextView) findViewById(R.id.moodTV);
        moodTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayLoggerActivity.this);
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

        final EditText descriptionET = (EditText) findViewById(R.id.descriptionET);



        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // The string resource file has a space before and after the mood score to increase
                // hitbox of the mood.
                String trimmedMood = moodTV.getText().toString().replace(" ", "");
                int mood = Integer.parseInt(trimmedMood);

                dbHelper.addEvent(new CalendarEvent(startDouble, endDouble - startDouble, descriptionET.getText().toString(), mood, startDay));
                finish();
            }
        });

    }

}
