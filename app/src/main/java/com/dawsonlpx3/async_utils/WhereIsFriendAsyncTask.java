package com.dawsonlpx3.async_utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class extending the AsyncTask class and handling the API call made to the backend of the
 * application that will request and return the location of a friend of the logged in user.
 * Receives the necessary information from the calling activity as parameters such as credentials,
 * the email of the friend, and a day and time. Those input data will be used to perform the API
 * call in the background. Returns the location details of a friend as a JSONObject object
 * or null if an error occurred during the process of the api call.
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefleur
 */
public class WhereIsFriendAsyncTask extends AsyncTask <Object, Void, JSONObject>{

    private final String TAG = "Dawson-WhereFriendTask";
    private final String api_url = "https://friendfinder05.herokuapp.com/api/api/whereisfriend?";

    /**
     * Performs in the background the api call to the backend in order to retrieve the location of
     * a friend of the logged in user. Verifies first if the number of input parameters are
     * correct and that they are not empty.
     *
     * @param params Array of necessary info such as credentials in order to perform the api call
     * @return The Json response object or null if an error has occurred
     */
    @Override
    protected JSONObject doInBackground(Object... params) {
        Log.d(TAG, "doInBackground started with params: " + params.toString());

        //verify if valid number of params, if not then return null
        if(params.length == 5 && !((String)(params[0])).isEmpty() && !((String)(params[1])).isEmpty()){
            String url = api_url + "email=" + params[0] + "&password=" + params[1]
                    + "&friendemail=" + params[2] + "&day=" + (int)params[3]
                    + "&time=" + (int)params[4];
            Log.d(TAG, "URL: " + url);
            try {
                URL api_url = new URL(url);
                return retrieveLocation(api_url);
            } catch (MalformedURLException e) {
                Log.e(TAG, "URl Error: " + Log.getStackTraceString(e));
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Creates a Http connection and executes the api call in order to retrieve all the friends
     * of the current logged in user. Once the response is returned, builds the Json object array
     * and returns it. Response may contain an error message due to a Http Unauthorized message
     * or response may contain the array of friends and their individual information. May return null
     * if an error occurred during the process
     *
     * @param url URL containing the api url string with query
     * @return JSONObject response or null if an error has occurred during process
     */
    private JSONObject retrieveLocation(URL url){
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            // create and open the connection and set up
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            Log.i(TAG, "connection is set");

            conn.connect(); //start connection
            int response = conn.getResponseCode();
            Log.d(TAG, "Server returned: " + response);

            //if unexpected response, return null
            if (response != HttpURLConnection.HTTP_OK && response != HttpURLConnection.HTTP_UNAUTHORIZED
                    && response != HttpURLConnection.HTTP_BAD_REQUEST) {
                Log.e(TAG, "Response not HTTP OK, UNAUTHORIZED NOR BAD REQUEST, aborting read");
                return null;
            }

            //if HTTP UNAUTHORIZED or BAD REQUEST response, get the ErrorStream, otherwise the normal InputStream
            if(response == HttpURLConnection.HTTP_UNAUTHORIZED || response == HttpURLConnection.HTTP_BAD_REQUEST) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }

            //retrieve the json data
            StringBuffer json = new StringBuffer();
            String tmp = "";
            while((tmp = reader.readLine()) != null) {
                json.append(tmp).append("\n");
            }
            reader.close();

            //return the response as a JSONObject array
            Log.d(TAG, "json result: " + json.toString());
            return new JSONObject(json.toString());
        }  catch(Exception e){
            Log.e(TAG, "Exception when retrieving friend location" + Log.getStackTraceString(e));
            return null;
        } finally {
            //close http connection and the Buffered reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (IllegalStateException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

    }
}
