package com.dawsonlpx3;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button saveButton;

    /**
     * Construct the view and get handles to the views for manipulation.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_settings, container, false);

        // Get handle to the settings views
        firstNameEdit = view.findViewById(R.id.first_name_input);
        lastNameEdit = view.findViewById(R.id.last_name_input);
        emailEdit = view.findViewById(R.id.email_input);
        passwordEdit = view.findViewById(R.id.email_input);
        lastUpdatedText = view.findViewById(R.id.last_modified_input);
        saveButton = view.findViewById(R.id.save_settings_button);

        // Set the text to the currently saved values in storage
        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        firstNameEdit.setText(preferences.getString("fname", ""));
        lastNameEdit.setText(preferences.getString("lname", ""));
        emailEdit.setText(preferences.getString("email", ""));
        passwordEdit.setText(preferences.getString("password", ""));
        lastUpdatedText.setText(preferences.getString("timestamp", ""));

        // Setup the onClick listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "save button onClick");
                // get a handle to this activity's shared preferences
                SharedPreferences prefs = getActivity().getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                // put all user input into SharedPreferences
                editor.putString("fname", firstNameEdit.getText().toString());
                editor.putString("lname", lastNameEdit.getText().toString());
                editor.putString("email", emailEdit.getText().toString());
                editor.putString("password", passwordEdit.getText().toString());
                editor.putString("timestamp", Calendar.getInstance().getTime().toString());
                editor.commit(); // Save changes
                // Update last updated text
                lastUpdatedText.setText(prefs.getString("timestamp", null));
            }
        });

        return view;
    } // onCreateView()

    /**
     * Save user input in the fields to maintain app state.
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("tempFName", firstNameEdit.getText().toString());
        savedInstanceState.putString("tempLName", lastNameEdit.getText().toString());
        savedInstanceState.putString("tempEmail", emailEdit.getText().toString());
        savedInstanceState.putString("tempPassword", passwordEdit.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    } // onSaveInstanceState
} // SettingsActivity
