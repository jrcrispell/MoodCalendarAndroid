package com.wordpress.jrcrispell.moodcalendar;

import android.app.TimePickerDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DisplayLoggerActivity extends AppCompatActivity {

    private TextView startTime;
    private TextView endTime;
    int incomingStartTime;
    double incomingSpecificStartTime;
    //Calendar incomingEndTime;
    EventDBSQLiteHelper dbHelper;
    Locale locale;


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

        incomingStartTime = (int) getIntent().getExtras().getDouble("startHour");

        Format timeFormat = new SimpleDateFormat("hh:mm");
        startTime = (TextView) findViewById(R.id.startTimeTV);

        startTime.setText(incomingStartTime + ":00");


        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int startMin = 0;

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DisplayLoggerActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTime.setText(String.format(locale, "%02d:%02d", hourOfDay, minute));
                    }
                }, incomingStartTime, startMin, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dbHelper.addEvent();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

}
