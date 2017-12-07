package com.dawsonlpx3.find_friends_feature;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dawsonlpx3.R;
import com.dawsonlpx3.async_utils.AllFriendsAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Fragment activity which handles the display of the list of friends of the logged in user.
 * Handles making the async call to the backend in order to retrieve the list of friends in a
 * separate thread and displays the result into the list view. Handles as well the display of
 * any error messages retrieved from the api call in an alert dialog.
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefleur
 */
public class AllFriendsFragment extends Fragment {

    private final String TAG = "Dawson-AllFriendsFrag";
    private onFriendSelectedListener listener;
    private JSONArray jsonResponse = null;
    private List<String> friends_names;
    private ArrayAdapter<String> itemsAdapter;
    private String email, password;

    /**
     * Interface for handling the selection of a friend in the list of friends from the list view
     */
    public interface onFriendSelectedListener {
        /**
         * Method handler necessary to be implemented by any activity that will inflate this
         * fragment. Receives a friend email and the name.
         *
         * @param friendemail Email of a friend
         * @param name Name of a friend
         */
        void onFriendSelected(String friendemail, String name);
    }

    /**
     * Retrieves the credentials of the logged in user from the Shared Preferences and uses that
     * information to perform an API call to the backend in order to retrieve the list of friends
     * of the logged in user
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve credentials from Shared preference
        SharedPreferences prefs = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        email = prefs.getString("email", "");
        password = prefs.getString("password", "");
        Log.d(TAG, "Retrieved email and passwd from SharedPref: " + email + " " + password);

    }

    /**
     * Processes the json response object and verifies for any null response, empty reponse,
     * or error messages. In those cases, an alert dialog will display with the appropriate
     * message and will return true. If none of those cases match, returns false.
     *
     * @return boolean
     */
    private boolean checkForErrorsOrNoFriends() {
        //check for null reponse
        if(jsonResponse == null){
            Log.e(TAG, "Null jsonResponse!");
            displayErrorReponse(getResources().getString(R.string.problem_retrieving_friends));
            return true;
        }

        //check for empty result which means no friends found
        if(jsonResponse.length() == 0){
            Log.d(TAG, "no friends found");
            displayErrorReponse(getResources().getString(R.string.no_friends));
            return true;
        }

        //check if any error messages returned
        if(jsonResponse.length() == 1) {
            String errorMsg = null;
            try {
                errorMsg = jsonResponse.getJSONObject(0).getString("error");
                Log.d(TAG, "Error message found: " + errorMsg);
                displayErrorReponse(getResources().getString(R.string.invalid_creds));
                return true;
            } catch (JSONException e) {
                Log.d(TAG, "No error message found");
                return false;
            }
        }
        return false;
    }

    /**
     * Retrieve all friends items from the Json array response and add the names of each friend into
     * an array of strings.
     *
     * @throws JSONException when error reading the json object
     */
    private void buildFriendsNames() throws JSONException {
        friends_names = new ArrayList<>();
        for(int i = 0; i < jsonResponse.length(); i++){
            friends_names.add(jsonResponse.getJSONObject(i).getString("firstname") +
                                    " " + jsonResponse.getJSONObject(i).getString("lastname"));
            Log.d(TAG, "buildFriendsNames: " + friends_names.get(i));
        }
    }

    /**
     * Inflates the fragment into the view
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View containing the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_friends, container, false);
    }

    /**
     * Retrieves the context in which the fragment is inflated into and verifies
     * if the parent activity is implementing the onFriendSelectedListener listener.
     * If not, then an exception is thrown.
     *
     * @param context Context of current fragment
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onFriendSelectedListener) {
            this.listener = (onFriendSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onFriendSelectedListener");
        }
    }

    /**
     * Starts the api call to the backend to retrieve list of friends of the logged in user.
     * Retrieves the ListView widget from the view and sets an onclick listener for each
     * item in the list. When an onclick occurs, each item will execute the onFriendSelected method
     * from the onFriendSelectedListener interface
     *
     * @param view View containing fragment
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated started");
        startFindFriendsTask();

        ListView listView = (ListView) view.findViewById(R.id.allfriendsLV);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    listener.onFriendSelected(jsonResponse.getJSONObject(position).getString("email"),
                            friends_names.get(position));
                } catch (JSONException e) {
                    Log.e(TAG, "Error getting email for onClick friend: " + Log.getStackTraceString(e));
                    displayErrorReponse(getResources().getString(R.string.problem_retrieving_location));
                }
            }
        });
    }

    /**
     * Sends the api request to the backend to retrieve the list of friends of the logged in user.
     * Once response is received, process the json result for any error messages, null response, or
     * empty response which in those cases, an Alert dialog will display with an appropriate message.
     * If all data is in the response, add the list into the list view adapter.
     */
    private void startFindFriendsTask(){

        //start the async task to retrieve all friends and display progress
        AllFriendsAsyncTask allFriendsAsyncTask = new AllFriendsAsyncTask();
        allFriendsAsyncTask.execute(email, password);
        try {
            //retrieve response and remove progress
            jsonResponse = allFriendsAsyncTask.get();
            Log.d(TAG, "jsonResponse: " + jsonResponse.toString());
            //process json result
            if(!checkForErrorsOrNoFriends()) {
                buildFriendsNames();
                this.itemsAdapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.list_item, friends_names.toArray(new String[0]));
            }
        } catch (InterruptedException | ExecutionException | JSONException e ) {
            displayErrorReponse(getResources().getString(R.string.problem_retrieving_friends));
            Log.e(TAG, "API Error getting friends: " + Log.getStackTraceString(e));
        }
    }

    /**
     * Sets the fragment's onFriendSelectedListener interface to null
     */
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    /**
     * Creates and displays an Alert Dialog with the input string message as the content.
     *
     * @param message Message to display in the dialog
     */
    private void displayErrorReponse(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle(R.string.warning)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
