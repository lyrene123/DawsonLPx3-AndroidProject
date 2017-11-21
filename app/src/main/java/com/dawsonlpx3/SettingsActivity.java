package com.dawsonlpx3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Activity that deals with the activity_settings layout file.
 * The Activity takes as input 4 different values and using these, will update critical
 * information for other parts of the project. The settings will only be saved when a user presses
 * the save button on the screen.
 *
 * @author Philippe Langlois
 */
public class SettingsActivity extends Fragment {

    private View view;
    private static final String TAG = "SettignsActivity";
    private EditText firstNameEdit, lastNameEdit, emailEdit, passwordEdit;
    private TextView lastUpdatedText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_settings, container, false);

        // Get handle to the EditText views
        firstNameEdit = view.findViewById(R.id.first_name_input);
        lastNameEdit = view.findViewById(R.id.last_name_input);
        emailEdit = view.findViewById(R.id.email_input);
        passwordEdit = view.findViewById(R.id.email_input);
        lastUpdatedText = view.findViewById(R.id.last_modified_input);

        return view;
    }

    /**
     * Event handler for the onClick event from the save button. Will update SharedPreferences
     * with new values inputted from the user.
     *
     * @param view
     */
    public void saveSettings(View view){
        Log.d(TAG, "saveSettings");
        SharedPreferences prefs = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fname", firstNameEdit.getText().toString());
        editor.putString("lname", lastNameEdit.getText().toString());
        editor.putString("email", emailEdit.getText().toString());
        editor.putString("password", passwordEdit.getText().toString());
        editor.putString("timestamp", Calendar.getInstance().getTime().toString());

        Log.d("first name: ", firstNameEdit.getText().toString());
        Log.d("last name: ", lastNameEdit.getText().toString());
        Log.d("email: ", emailEdit.getText().toString());
        Log.d("password: ", passwordEdit.getText().toString());
        Log.d("last update: ", Calendar.getInstance().getTime().toString());
        editor.commit(); // Save changes
        // Update last updated text
        lastUpdatedText.setText(prefs.getString("timestamp", null));
    }
}
