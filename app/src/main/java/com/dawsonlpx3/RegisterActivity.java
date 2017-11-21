package com.dawsonlpx3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /**
     * Retrieves the current date and time in string format.
     *
     * Solution taken from: https://stackoverflow.com/questions/5369682/get-current-time-and-date-on-android
     *
     * @return String format of current date and time
     */
    private  String getDateCurrentTimeZone() {
        return Calendar.getInstance().getTime().toString();
    }

}
