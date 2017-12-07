package com.dawsonlpx3.register_feature;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dawsonlpx3.R;

import java.util.Calendar;
import static android.content.Context.MODE_PRIVATE;

/**
 * Activity that deals with the activity_settings layout file.
 * The Activity takes as input 4 different values and using these, will update critical
 * information for other parts of the project. The settings will only be saved when a user presses
 * the save button on the screen.
 *
 * @author Lyrene Labor
 * @autor Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefgleur
 */
public class SettingsFragment extends Fragment {

    private View view;
    private static final String TAG = "SettignsActivity";
    private EditText firstNameEdit, lastNameEdit, emailEdit, passwordEdit;
    private TextView lastUpdatedText;
    private Button saveButton;
    private Boolean validFlag = true;

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
        passwordEdit = view.findViewById(R.id.password_input);
        lastUpdatedText = view.findViewById(R.id.last_modified_input);
        saveButton = view.findViewById(R.id.save_settings_button);

        // Set the text to the currently saved values in storage
        SharedPreferences preferences = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
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
                SharedPreferences prefs = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // Check if user has valid inputs.
                if(validateInputs()){
                    editor.putString("fname", firstNameEdit.getText().toString());
                    editor.putString("lname", lastNameEdit.getText().toString());
                    editor.putString("email", emailEdit.getText().toString());
                    editor.putString("password", passwordEdit.getText().toString());
                    editor.putString("timestamp", Calendar.getInstance().getTime().toString());
                    editor.commit(); // Save changes
                    // Update last updated text
                    lastUpdatedText.setText(prefs.getString("timestamp", null));
                    Toast.makeText(getActivity(), getResources().getString(R.string.settings_saved),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    } // onCreateView()

    /**
     * Checks all input field for any errors or missing fields. Sets teh flag to true at start and
     * if any inconsistencies arise, sets the flag to false;
     *
     * @return
     */
    private Boolean validateInputs(){
        validFlag = true;

        if (firstNameEdit.getText().toString().isEmpty()) {
            firstNameEdit.setHint(getResources().getString(R.string.completeThisField));
            firstNameEdit.setHintTextColor(Color.RED);
            validFlag = false;
        }

        if (lastNameEdit.getText().toString().isEmpty()) {
            lastNameEdit.setHint(getResources().getString(R.string.completeThisField));
            lastNameEdit.setHintTextColor(Color.RED);
            validFlag = false;
        }

        if (emailEdit.getText().toString().isEmpty()) {
            emailEdit.setHint(getResources().getString(R.string.completeThisField));
            emailEdit.setHintTextColor(Color.RED);
            validFlag = false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEdit.getText()).matches()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.invalid_email_pattern),
                    Toast.LENGTH_SHORT).show();
            validFlag = false;
        }

        if (passwordEdit.getText().toString().isEmpty()) {
            passwordEdit.setHint(getResources().getString(R.string.completeThisField));
            passwordEdit.setHintTextColor(Color.RED);
            validFlag = false;
        }

        return validFlag;
    } // validateInputs()


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
} // SettingsFragment
