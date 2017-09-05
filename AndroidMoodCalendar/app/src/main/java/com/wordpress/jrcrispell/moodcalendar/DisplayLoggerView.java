package com.wordpress.jrcrispell.moodcalendar;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DisplayLoggerView extends AppCompatActivity {

    private EditText startTime;
    private EditText endTime;
    int incomingStartTime;
    double incomingSpecificStartTime;
    //Calendar incomingEndTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_logger_view);


        endTime = (EditText) findViewById(R.id.endTimeET);

        incomingStartTime = (int) getIntent().getExtras().getDouble("startHour");

        Format timeFormat = new SimpleDateFormat("hh:mm");
        startTime = (EditText) findViewById(R.id.startTimeET);

        startTime.setText(incomingStartTime + ":00");


        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int startMin = 0;

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DisplayLoggerView.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTime.setText(hourOfDay + ":" + minute);
                    }
                }, incomingStartTime, startMin, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        Button saveButton = (Button) findViewById(R.id.save_button);
        //saveButton.setOnClickListener();
    }

}
