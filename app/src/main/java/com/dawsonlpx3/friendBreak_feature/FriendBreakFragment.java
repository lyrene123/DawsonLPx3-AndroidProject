package com.dawsonlpx3.friendBreak_feature;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dawsonlpx3.R;
import com.dawsonlpx3.async_utils.WhosFreeAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 *
 */
public class FriendBreakFragment extends Fragment {

    private static final String TAG = "FriendBreakFragment";

    View view;
    private Context context; // Current Activity Context
    private Spinner weekSpinner, startSpinner, endSpinner;
    //private EditText start_time, end_time;
    private Button apiButton;
    private TextView testText;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_break, container, false);
        Log.d(TAG, "onCreateView");

        // Get handles to required classes and information.
        context = this.getActivity();
        weekSpinner = view.findViewById(R.id.free_friends_days);
        startSpinner = view.findViewById(R.id.free_friends_start_time);
        endSpinner = view.findViewById(R.id.free_friends_end_time);
        apiButton = view.findViewById(R.id.free_friends_button);
        testText = view.findViewById(R.id.free_friends_test);
        preferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        // Setup the spinners
        String[] daysOfTheWeek = view.getResources().getStringArray(R.array.days_of_the_week);
        // ArrayAdapter to add items to the spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, daysOfTheWeek);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpinner.setAdapter(spinnerArrayAdapter);
        weekSpinner.setSelection(0); // set default value to Sunday

        String[] timesOfDay = view.getResources().getStringArray(R.array.search_times);
        ArrayAdapter<String> timeArrayAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, timesOfDay);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Start-time spinner
        startSpinner.setAdapter(timeArrayAdapter);
        startSpinner.setSelection(0); // set default value 10 AM
        // End-time spinner
        endSpinner.setAdapter(timeArrayAdapter);
        endSpinner.setSelection(1); // set default value 10:30 AM

        // Setup the button
        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateTimes()){
                    JSONArray response = null;
                    WhosFreeAsyncTask task = new WhosFreeAsyncTask();
                    task.execute(preferences.getString("email", ""),
                            preferences.getString("password", ""), Integer.toString(weekSpinner.getSelectedItemPosition()+1),
                            startSpinner.getSelectedItem().toString(), endSpinner.getSelectedItem().toString());
                    try {
                        response = task.get();
                    } catch (InterruptedException|ExecutionException e) {
                        Log.d(TAG, e.getMessage());
                    }

                    if(response != null){
                        testText.setText(response.toString());
                    }else{
                        testText.setText("Response returned NULL");
                    }
                }else{
                    testText.setText("That is INVALID");
                }
            }
        });

        return view;
    } // onCreateView


    /**
     *
     * @return
     */
    private boolean validateTimes(){
        if(startSpinner.getSelectedItemPosition() < endSpinner.getSelectedItemPosition()){
            return true;
        }
        return false;
    } // validateTimes()

} // FriendBreakFragment
