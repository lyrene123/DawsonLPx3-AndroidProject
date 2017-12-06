package com.dawsonlpx3.register_feature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dawsonlpx3.MainActivity;
import com.dawsonlpx3.R;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Activity that handles the display of the register page that will appear when user installs
 * the app for the first time. Retrieves all user input from the register form and then validates
 * in order to save the user's credentials into Shared Preferences.
 *
 * @author Lyrene Labor
 * @autor Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefgleur
 */
public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    private EditText firstnameET, lastnameET, emailET, passwordET;
    private Button registerBtn;
    private TextView errorTV;

    // Got this pattern from https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /**
     * Retrieves all widgets from the view necessary in order to retrieve whatever input the user
     * entered when registering for the first time. Sets an onclick listener on the register button
     * which will validate the user input and then save into the SharedPreferences when valid input.
     * Once input saved, the MainActivity is launched.
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstnameET = (EditText) findViewById(R.id.firstnameEditText);
        lastnameET = (EditText) findViewById(R.id.lastnameEditText);
        emailET = (EditText) findViewById(R.id.emailEditText);
        passwordET = (EditText) findViewById(R.id.passwordEditText);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        errorTV = (TextView) findViewById(R.id.errorTV);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "RegisterBtn clicked");
                if (isValidated()) {
                    SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                    editor.putString("fname", firstnameET.getText().toString());
                    editor.putString("lname", lastnameET.getText().toString());
                    editor.putString("email", emailET.getText().toString());
                    editor.putString("password", passwordET.getText().toString());
                    editor.putString("timestamp", getDateCurrentTimeZone());
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

        if (!emailET.getText().toString().matches(EMAIL_PATTERN)) {
            errorTV.setText(getResources().getString(R.string.invalidEmail));
            errorTV.setTextColor(Color.RED);
            errorTV.setVisibility(View.VISIBLE);
            return false;
        }

        if (passwordET.getText().toString().isEmpty()) {
            passwordET.setHint(getResources().getString(R.string.completeThisField));
            passwordET.setHintTextColor(Color.RED);
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
