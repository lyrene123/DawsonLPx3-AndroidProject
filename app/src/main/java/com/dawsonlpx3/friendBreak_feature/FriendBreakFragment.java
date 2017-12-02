package com.dawsonlpx3.friendBreak_feature;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.dawsonlpx3.R;
import com.dawsonlpx3.async_utils.WhosFreeAsyncTask;
import org.json.JSONArray;

/**
 * Fragment that is used to find the user's friends who are on a break in-between a specified time
 * interval on a given day. A list of the friends who are on break will then be visible
 * ans clicking on their name in the list will allow the user to send them an email.
 */
public class FriendBreakFragment extends Fragment {

    private static final String TAG = "FriendBreakFragment";
    View view;
    SharedPreferences preferences;
    private Context context; // Current Activity Context
    private Spinner weekSpinner, startSpinner, endSpinner;
    private Button apiButton;
    private ListView friendList;
    private int friendPosition = 0;
    private String[] friendEmails = null;

    /**
     *  Sets up any handlers to the views that are needed an initialization of the view.
     *  Then inflate the view to display to the user.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
        friendList = view.findViewById(R.id.free_friends_list);
        preferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        setupSpinners(); // Setup the spinners
        setupApiButton(); // Setup logic relevant to button

        return view;
    } // onCreateView


    /**
     *  Helper method that will return true if the start time specified in the start time spinner
     *  is less than the end time. Returns false if the end time is equal to or before the
     *  start time.
     *
     * @return
     */
    private boolean validateTimes(){
        if(startSpinner.getSelectedItemPosition() < endSpinner.getSelectedItemPosition()){
            return true;
        }
        return false;
    } // validateTimes()

    /**
     * Sets up any relevant information with the spinner views in the layout.
     */
    private void setupSpinners(){
        // Access the string array that contains the strings for the days of the week.
        String[] daysOfTheWeek = view.getResources().getStringArray(R.array.days_of_the_week);

        // ArrayAdapter to add relevant days of the week to the spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, daysOfTheWeek);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Days of the week spinner
        weekSpinner.setAdapter(spinnerArrayAdapter);
        weekSpinner.setSelection(0); // set default value to Sunday

        // ArrayAdapter to add relevant 30-minutes time intervals to the time spinners.
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
    } // setupSpinners()

    /**
     * Sets up the onclick listener for the api call button and the friend list.
     */
    private void setupApiButton(){
        // Setup the button
        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateTimes()){ // valid time interval
                    JSONArray response = null;
                    WhosFreeAsyncTask task = new WhosFreeAsyncTask(); // create the AsyncTask
                    // setup the AsyncTask parameters
                    task.execute(preferences.getString("email", ""),
                            preferences.getString("password", ""),
                            Integer.toString(weekSpinner.getSelectedItemPosition()+1),
                            startSpinner.getSelectedItem().toString(),
                            endSpinner.getSelectedItem().toString());
                    try {
                        response = task.get(); // get response from AsyncTask
                        String[] friendsOnBreak = new String[response.length()];
                        friendEmails = new String[response.length()];
                        // Parse through the array data
                        for(int i = 0; i < response.length(); i++){
                            friendsOnBreak[i] = response.getJSONObject(i).getString("firstname");
                            friendEmails[i] = response.getJSONObject(i).getString("email");
                        }
                        // setup teh adapter for the friend's list
                        ArrayAdapter<String> friendAdapter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_list_item_1, friendsOnBreak);

                        // Associate the ListView with the adapter
                        friendList.setAdapter(friendAdapter);
                        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                friendPosition = position; // get position in the list of the selected person
                                // New intent to send an email to the selected person.
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:"));
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{friendEmails[friendPosition]});
                                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.from));
                                startActivity(intent);
                            }
                        });
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                }else{ // invalid time interval
                    Toast.makeText(context, getResources().getString(R.string.invalid_time_interval), Toast.LENGTH_LONG).show();
                }
            }
        }); // end of setOnClickListener()
    } // setupApiButton()

} // FriendBreakFragment
