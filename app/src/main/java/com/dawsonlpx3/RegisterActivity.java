package com.dawsonlpx3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /**
     * Converts the input long current timestamp based on the timezone and returns String
     * format yyyy-MM-dd HH:mm:ss
     * Solution taken from: https://stackoverflow.com/questions/18717111/to-get-date-and-time-from-timestamp-android
     *
     * @param timestamp the current timestamp in long format
     * @return String format of current timestamp in yyyy-MM-dd HH:mm:ss
     */
    private  String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
            return "";
        }
    }
}
