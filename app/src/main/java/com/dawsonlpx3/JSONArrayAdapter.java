package com.dawsonlpx3;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dawsonlpx3.async_utils.WhereIsFriendAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by KimHyonh on 12/4/2017.
 */

public class JSONArrayAdapter extends BaseAdapter {

    private final String TAG = "JSONArrayAdapter";

    private LayoutInflater inflater;
    private JSONArray jsonArray;
    private Context context;
    private String email;
    private String password;
    private int day;
    private int time;

    public JSONArrayAdapter(Context context, JSONArray jsonArray, String email
            , String password, int day, int time) {
        Log.i(TAG, "JSONArrayAdapter");

        this.jsonArray = jsonArray;
        this.context = context;
        this.email = email;
        this.password = password;
        Log.d(TAG, "email: " + email);
        Log.d(TAG, "password: " +  password);
        this.day = day;
        this.time = time;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Log.i(TAG, "getView");
        Log.d(TAG, jsonArray.toString());
        try {
            String friendemail = jsonArray.getJSONObject(position).getString("email");
            String name = jsonArray.getJSONObject(position).getString("firstname") + " "
                    + jsonArray.getJSONObject(position).getString("lastname");

            Log.d(TAG, "friendemail: " + friendemail);
            Log.d(TAG, "name: " + name);
            WhereIsFriendAsyncTask friendAsyncTask = new WhereIsFriendAsyncTask();
            friendAsyncTask.execute(email, password, friendemail, day, time);

            JSONObject jsonResponse = null;
            try {
                jsonResponse = friendAsyncTask.get();

                String course = jsonResponse.getString("course");
                String section = jsonResponse.getString("section");

                Log.d(TAG, "course: " + course);
                Log.d(TAG, "section: " + section);

                if (view == null) {
                    view = inflater.inflate(R.layout.allfriends_list_item, viewGroup, false);
                    Log.d(TAG, "view: " + view);
                }
                TextView nameTV = view.findViewById(R.id.nameTV);
                TextView courseTV = view.findViewById(R.id.courseTV);
                TextView sectionTV = view.findViewById(R.id.sectionTV);

               nameTV.setText(name);
               courseTV.setText(course);
               sectionTV.setText(section);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    /**
     * Process the Json object response returned from the whereisfriend api call to the backend.
     * Checks if the json object response is null and if yes, an alert dialog will display with appropriate message.
     * Checks if json object contains an error message, and if yes, an alert dialog will display with appropriate message.
     * Checks if json object contains an empty course and section data and if yes,
     * an alert dialog will display for the user to be notified that friend is not in class
     *
     * @param jsonResponse JSONObject returned from api call
     * @return true if JSON response is null, contains error, or empty course/section data
     *          false if course/section data exists
     */
    private boolean checkForErrorsOrNoLoc(JSONObject jsonResponse) {
        //check if response is null
        if(jsonResponse == null){
            Log.e(TAG, "Api Error getting friend location");
            displayErrorReponse(this.context.getResources().getString(R.string.problem_retrieving_location));
            return true;
        }
        //check if any errors in the response
        try {
            String error = jsonResponse.getString("error");
            Log.d(TAG, "Found error message");
            if(error.equalsIgnoreCase("invalid_credentials") || error.equalsIgnoreCase("invalid credentials")){
                displayErrorReponse(this.context.getResources().getString(R.string.invalid_creds));
            } else if(error.equalsIgnoreCase("bad or missing parameter")){
                displayErrorReponse(this.context.getResources().getString(R.string.missing_params));
            } else {
                displayErrorReponse(this.context.getResources().getString(R.string.not_friends));
            }
            return true;
        } catch (JSONException e) {
            //check if course/section data is empty or not
            try {
                String course = jsonResponse.getString("course");
                String section = jsonResponse.getString("section");

                if(course.isEmpty() || section.isEmpty()){
                    Log.d(TAG, "unknown whereabouts, not in class");
                    displayErrorReponse(this.context.getResources().getString(R.string.not_in_class));
                    return true;
                }
                return false; //return false if no error, no null json response, and no empty course/section
            } catch (JSONException e1) {
                Log.e(TAG, "Api Error getting friend location: " + Log.getStackTraceString(e1));
                displayErrorReponse(this.context.getResources().getString(R.string.problem_retrieving_location));
                return true;
            }
        }
    }


    /**
     * Creates and displays an Alert Dialog with the input string message as the content.
     *
     * @param message Message to display in the dialog
     */
    private void displayErrorReponse(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage(message)
                .setTitle(R.string.warning)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
