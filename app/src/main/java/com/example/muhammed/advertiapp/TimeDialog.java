package com.example.muhammed.advertiapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Bahaa on 30/11/2017.
 */

@SuppressLint("ValidFragment")
public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    EditText txtTime;
    public TimeDialog(View view) {
        txtTime = (EditText) view;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {

// Use the current date as the default date in the dialog
        final Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int SECOND = c.get(Calendar.SECOND);
        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hourOfDay, minute,true);
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();

// set the calendar to start of today
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

// and get that as a Date
        Date today = c.getTime();

// or as a timestamp in milliseconds
        String date = hourOfDay + ":" + minute;
        txtTime.setText(date);
    }
}