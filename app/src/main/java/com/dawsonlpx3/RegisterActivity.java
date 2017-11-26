package com.dawsonlpx3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    private EditText firstnameET, lastnameET, emailET;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstnameET = (EditText) findViewById(R.id.firstnameEditText);
        lastnameET = (EditText) findViewById(R.id.lastnameEditText);
        emailET = (EditText) findViewById(R.id.emailEditText);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "RegisterBtn clicked");
                if (isValidated()) {
                    SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                    editor.putString("fname", firstnameET.getText().toString());
                    editor.putString("lname", lastnameET.getText().toString());
                    editor.putString("email", emailET.getText().toString());
                    editor.commit();
                    Log.i(TAG, "Launch MainActiviyt");
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }
            }
        });
    }

    /**
     * Validate the user input, if there are any missing input, notify the user by
     * show the hint in red color.
     * @return true if all fields is completed.
     */
    private boolean isValidated() {
        if (firstnameET.getText().toString().isEmpty()) {
            firstnameET.setHint(getResources().getString(R.string.completeThisField));
            firstnameET.setHintTextColor(Color.RED);
            return false;
        }

        if (lastnameET.getText().toString().isEmpty()) {
            lastnameET.setHint(getResources().getString(R.string.completeThisField));
            lastnameET.setHintTextColor(Color.RED);
            return false;
        }

        if (emailET.getText().toString().isEmpty()) {
            emailET.setHint(getResources().getString(R.string.completeThisField));
            emailET.setHintTextColor(Color.RED);
            return false;
        }

        return true;
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
