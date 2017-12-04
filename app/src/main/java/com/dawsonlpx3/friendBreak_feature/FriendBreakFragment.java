package com.dawsonlpx3.friendBreak_feature;

import android.app.AlertDialog;
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
import android.widget.Toast;
import com.dawsonlpx3.R;
import com.dawsonlpx3.async_utils.WhosFreeAsyncTask;
import com.dawsonlpx3.data.TeacherDetails;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that is used to find the user's friends who are on a break in-between a specified time
 * interval on a given day. A list of the friends who are on break will then be visible
 * ans clicking on their name in the list will allow the user to send them an email.
 *
 * @author Lyrene Labor
 + @author Pengkim Sy
 + @author Phil Langlois
 + @author Peter Bellefleur
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
    private String[] friendsOnBreak = null;
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

        // Restore the state of the friend list
        if (savedInstanceState != null) {
            Log.d(TAG, "Restoring friend list from bundle");
            int size = savedInstanceState.getInt("friend_break_list_size");
            friendsOnBreak = new String[size];
            friendEmails = new String[size];
            for (int i = 0; i < size; i++) {
                friendsOnBreak[i] = savedInstanceState.getSerializable("friends_" + i).toString();
                friendEmails[i] = savedInstanceState.getSerializable("friend_emails" + i).toString();
            }
            // setup the adapter for the friend's list
            ArrayAdapter<String> friendAdapter = new ArrayAdapter<String>(context,
                    R.layout.list_item, friendsOnBreak);

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
        }

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
        String[] daysOfTheWeek = view.getResources().getStringArray(R.array.day_array);

        // ArrayAdapter to add relevant days of the week to the spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                context, R.layout.spinner_item, daysOfTheWeek);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Days of the week spinner
        weekSpinner.setAdapter(spinnerArrayAdapter);
        weekSpinner.setSelection(0); // set default value to Sunday

        // ArrayAdapter to add relevant 30-minutes time intervals to the time spinners.
        String[] timesOfDay = view.getResources().getStringArray(R.array.search_times);
        ArrayAdapter<String> timeArrayAdapter = new ArrayAdapter<String>(
                context, R.layout.spinner_item, timesOfDay);
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
                        Log.d(TAG, response.toString());
                        if(!invalidOrErrorCheck(response)){
                            friendsOnBreak = new String[response.length()];
                            friendEmails = new String[response.length()];
                            // Parse through the array data
                            for(int i = 0; i < response.length(); i++){
                                friendsOnBreak[i] = response.getJSONObject(i).getString("firstname");
                                friendEmails[i] = response.getJSONObject(i).getString("email");
                            }
                            // setup the adapter for the friend's list
                            ArrayAdapter<String> friendAdapter = new ArrayAdapter<String>(context,
                                    R.layout.list_item, friendsOnBreak);

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
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                }else{ // invalid time interval
                    Toast.makeText(context, getResources().getString(R.string.invalid_time_interval), Toast.LENGTH_LONG).show();
                }
            }
        }); // end of setOnClickListener()
    } // setupApiButton()

    /**
     *  Process the rseponse from the api. If the response is null or produces an error
     *  thenr eturn false. If the response is valid but empty or the response returned invalid
     *  credentials or error message return true.
     *
     * @return
     */
    private Boolean invalidOrErrorCheck(JSONArray response){
        // CHeck if request was null
        if(response == null){
            Log.d(TAG, "NULL JSON Response.");
            displayAlert(getResources().getString(R.string.break_null));
            return true;
        }

        // Check for empty response
        if(response.length() == 0){
            Log.d(TAG, "No friends on break, how very lonely.");
            displayAlert(getResources().getString(R.string.break_no_friends));
            return true;
        }

        // Check if response returned an error
        if(response.length() == 1){
            String errormsg = null;
            try{
                errormsg = response.getJSONObject(0).getString("error");
                Log.d(TAG, "Response returned error message.");
                displayAlert(getResources().getString(R.string.break_invalid_creds));
                return true;
            } catch(Exception e){
                Log.d(TAG, "no message returned");
                return false;
            }
        }
        return false;
    } // invalidOrErrorCheck()

    /**
     * Creates an alert dialog with the appropriate message passed as the parameter.
     *
     * @param message The message to be displayed.
     */
    private void displayAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setTitle(R.string.warning)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    } // displayAlert()


    /**
     * Saves the current state of the fragment by saving each item of the list
     * and the total count of items in the list.
     *
     * @param saveInstanceState Bundle object
     */
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.d(TAG, "onSaveInstanceState started");
        //loop through teachers list, save each teacher and their order
        for (int i = 0; i < this.friendsOnBreak.length; i++) {
            saveInstanceState.putSerializable("friends_" + i, this.friendsOnBreak[i]);
            saveInstanceState.putSerializable("friend_emails_" + i, this.friendEmails[i]);
        }
        saveInstanceState.putInt("friend_break_list_size", friendsOnBreak.length);
    }
} // FriendBreakFragment
