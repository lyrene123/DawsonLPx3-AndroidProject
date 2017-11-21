package com.dawsonlpx3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Launches the Main Activity that will display the the app's main interaction with
 * the user.
 *
 *  TODO : more detailed javadocs, better to implement this class level javadocs when class is more or less done
 *
 */
public class MainActivity extends MenuActivity {

    private final String TAG = "LPx3-Main";
    private String FNAME, LNAME, PASSWORD, EMAIL, TIMESTAMP;

    /**
     * Sets the Content View of the main activity.
     * Verifies the SharedPreferences and retrieves any user credentials if existing.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate launched");
        checkForUserAuthentication(); //retrieve user credentials from SharedPref
    }

    /**
     * Check the Shared Preferences and verify if the user's credential exist. If
     * yes, then store them as constants and if none existing, then launch the Register
     * Activity in order to retrieve the user credentials from the user which will be stored
     * in Shared Preferences.
     */
    private void checkForUserAuthentication(){
        Log.d(TAG, "checkForUserAuthentication launched");

        //retrieve from shared prefs any existing user credentials.
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        this.FNAME = prefs.getString("fname", null);
        this.LNAME = prefs.getString("lname", null);
        this.PASSWORD = prefs.getString("password", null);
        this.EMAIL = prefs.getString("email", null);
        this.TIMESTAMP = prefs.getString("timestamp", null);

        //if no or some credentials missing, then launch the register activity
        if(this.FNAME == null || this.LNAME == null || this.PASSWORD == null ||
                this.EMAIL == null){
            Log.d(TAG,"Launching register activity");
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }


}
