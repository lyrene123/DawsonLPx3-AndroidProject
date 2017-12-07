package com.dawsonlpx3.friends_in_course_feature;

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
import android.widget.ListView;
import android.widget.TextView;

import com.dawsonlpx3.R;
import com.dawsonlpx3.async_utils.IsFriendInCourseAsyncTask;
import com.dawsonlpx3.data.CanceledClassDetails;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * The FriendInCourseFragment class represents a Fragment that displays a list of friends attending
 * a class that has been cancelled.
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 * @author Philippe Langlois
 * @author Pengkim Sy
 */

public class FriendInCourseFragment extends Fragment {

    private View view;
    private CanceledClassDetails details;
    private final String TAG = "LPx3-FriendInCourse";
    private JSONArray jsonResponse = null;
    private List<String> friendNames;
    private List<String> friendEmails;
    private ArrayAdapter<String> itemsAdapter;
    private String email;
    private String password;

    /**
     * Retrieves information from Shared Preferences once the fragment is created, and saves them
     * in global variables to be used later.
     *
     * @param savedInstanceState    The Bundle passed to the Fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getActivity().getSharedPreferences("userInfo",
                Context.MODE_PRIVATE);
        email = prefs.getString("email", "");
        password = prefs.getString("password", "");
        Log.d(TAG, "Retrieved email and password from SharedPrefs: " + email + ", "
                + password);
    }

    /**
     * Saves data in a Bundle, to restore after the fragment has been temporarily destroyed.
     *
     * @param outState  The Bundle, which will contain the data to save.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        outState.putSerializable("class", details);

        super.onSaveInstanceState(outState);
    }

    /**
     * Checks the JSON response to ensure it contains valid data for populating the ListView.
     * Verifies that the response is not null, not empty, and contains only non-error elements.
     *
     * @return  true if the response does not contain valid data for the ListView, false otherwise.
     */
    private boolean validateJson() {
        //response should not be null; this means an error has occurred in our code
        if (jsonResponse == null) {
            Log.e(TAG, "null JSON response");
            //display appropriate message to user
            displayMessage(getResources().getString(R.string.problem_retrieving_friends));
            return true;
        }

        //if response is empty, no friends have been found
        if (jsonResponse.length() == 0) {
            Log.d(TAG, "no friends found");
            //display appropriate message to user
            displayMessage(getResources().getString(R.string.no_friends_cancelled));
            return true;
        }

        //response with only 1 element could either be valid or an error response; check to see
        if (jsonResponse.length() == 1) {
            String errorMessage = null;
            try {
                //try to retrieve the contents of the tag "error"
                errorMessage = jsonResponse.getJSONObject(0).getString("error");
                //if no exception thrown, result was an error - user not authenticated properly
                Log.d(TAG, "Error message found: " + errorMessage);
                displayMessage(getResources().getString(R.string.invalid_creds));
                return true;
            } catch (JSONException je) {
                //if exception thrown, no "error" tag - result contains one friend
                Log.d(TAG, "no error found, proceeding as normal");
                return false;
            }
        }
        //if JSON contains more than 1 element, it should contain multiple friends - no problems
        return false;
    }

    /**
     * Populates the List objects corresponding to retrieved friend information; specifically,
     * their names and their email addresses.
     *
     * @throws JSONException    if the JSON response does not contain the expected tags
     */
    private void buildFriendsNamesAndEmail() throws JSONException {
        //instantiate lists
        friendNames = new ArrayList<>();
        friendEmails = new ArrayList<>();
        //iterate through response
        for (int i = 0; i < jsonResponse.length(); i++) {
            //add first & last names together for presentation
            friendNames.add(jsonResponse.getJSONObject(i).getString("firstname")
                    + " "
                    + jsonResponse.getJSONObject(i).getString("lastname"));
            friendEmails.add(jsonResponse.getJSONObject(i).getString("email"));
            Log.d(TAG, "friend name: " + friendNames.get(i));
            Log.d(TAG, "friend email: " + friendEmails.get(i));
        }
    }

    /**
     * Creates and returns the View hierarchy associated with this Fragment. Inflates the layout
     * for this Fragment as defined in its layout resources, retrieves data passed to it from the
     * Bundle, and uses this data to modify the header text.
     *
     * @param inflater  A LayoutInflater.
     * @param container The parent View containing the Fragment.
     * @param savedInstanceState    The Bundle passed to the Fragment.
     * @return  The inflated View.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_friend_class, container, false);
        //check savedInstanceState for data
        if (savedInstanceState == null) {
            //if null, get data from getArguments
            if (getArguments() != null) {
                this.details = (CanceledClassDetails) getArguments().getSerializable("class");
            }
        } else {
            //if not null, get data from savedInstanceState
            this.details = (CanceledClassDetails) savedInstanceState.getSerializable("class");
        }
        //get handle to header TextView, used retrieved data to append to header text
        TextView titleTV = (TextView) view.findViewById(R.id.friend_cancel_header);
        titleTV.append(details.getCourse());
        return view;
    }

    /**
     * Launches the AsyncTask for retrieving friend information once the fragment's parent Activity
     * is created, then associates the retrieved data with the ListView and creates an
     * onClickListener for each list element.
     *
     * @param savedInstanceState    The Bundle passed to the Fragment.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onViewCreated");
        //launch async task
        startFindFriendsTask();

        //get a handle to ListView
        ListView listView = (ListView) view.findViewById(R.id.friend_cancel_LV);
        //assign Adapter to ListView; if list is empty then nothing will display
        listView.setAdapter(itemsAdapter);
        //set click listener for each list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * When clicked, launches an email Intent, which creates an email that will be sent to
             * the associated friend's email address.
             *
             * @param parent    The AdapterView associated with the clicked View.
             * @param view  The clicked View object.
             * @param position  The position of the clicked View in the list.
             * @param id    The ID number of the clicked View in the list.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create intent
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                //retrieve friend's email from list of emails
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{friendEmails.get(position)});
                //email subject should contain app name
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.from));
                startActivity(intent);
            }
        });
    }

    /**
     * Executes the IsFriendInCourseAsyncTask, retrieves the data from it when it is complete, and
     * populates the appropriate Lists using the retrieved data.
     */
    private void startFindFriendsTask() {
        IsFriendInCourseAsyncTask task = new IsFriendInCourseAsyncTask();
        //API must take as input: user's email, user's password, course code, group number
        //course code & group number stored as one entry in CanceledClassDetails; split them
        String[] courseSplitter = details.getTitle().split("\\s");
        //remove leading 0's from group number
        courseSplitter[1] = courseSplitter[1].replaceFirst("^0+(?!$)", "");
        Log.d(TAG, "course code: " + courseSplitter[0]);
        Log.d(TAG, "section number: " + courseSplitter[1]);
        //send data to task
        task.execute(email, password, courseSplitter[0], courseSplitter[1]);

        try {
            //try to retrieve result from task
            jsonResponse = task.get();
            Log.d(TAG, "jsonResponse retrieved");

            //if data is valid, populate lists & associate list of names with list adapter
            if (!validateJson()) {
                buildFriendsNamesAndEmail();
                this.itemsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
                        friendNames.toArray(new String[0]));
            }
        } catch (InterruptedException ie) {
            Log.e(TAG, "startFindFriendsTask: InterruptedException "
                    + Log.getStackTraceString(ie));
        } catch (ExecutionException ee) {
            Log.e(TAG, "startFindFriendTask: ExecutionException "
                    + Log.getStackTraceString(ee));
        } catch (JSONException je) {
            Log.e(TAG, "startFindFriendTask: JSONException " + Log.getStackTraceString(je));
        }
    }

    /**
     * Creates an AlertDialog to display a message to the user.
     *
     * @param message   The message to display.
     */
    private void displayMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle(R.string.warning)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
