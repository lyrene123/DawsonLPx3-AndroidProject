package com.dawsonlpx3.async_utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * The IsFriendInCourseAsyncTask is an AsyncTask responsible for retrieving JSON, from the
 * FriendFinder05 website's "find friend in specific class" API, in a background thread.
 *
 * @author  Peter Bellefleur
 * @author  Lyrene Labor
 * @author  Philippe Langlois
 * @author  Pengkim Sy
 */

public class IsFriendInCourseAsyncTask extends AsyncTask <String, Void, JSONArray>{

    //tag for logging
    private final String TAG = "LPx3-FriendInCourseTask";
    //url for API
    private final String API_URL = "https://friendfinder05.herokuapp.com/api/api/coursefriends?";

    /**
     * Retrieevs a JSONArray of friends registered for a specific class from the FriendFinder05
     * JSON API. The list may be empty if no friends are found to attend this course, or null if
     * a logic error has occurred.
     *
     * @param params    An array of Strings containing information needed to use the API.
     * @return  A JSONArray of friends registered for a given class.
     */
    @Override
    protected JSONArray doInBackground(String... params) {
        Log.d(TAG, "doInBackground");
        //task must be passed 4 params, params must not be empty
        if(params.length == 4
                && !params[0].isEmpty()
                && !params[1].isEmpty()
                && !params[2].isEmpty()
                && !params[3].isEmpty()) {
            try {
                //encode input for URL
                String urlString = API_URL
                        + "email=" + URLEncoder.encode(params[0], "UTF-8")
                        + "&password=" + URLEncoder.encode(params[1], "UTF-8")
                        + "&coursename=" + URLEncoder.encode(params[2], "UTF-8")
                        + "&section=" + URLEncoder.encode(params[3], "UTF-8");
                Log.d(TAG, "URL: " + urlString);
                //create URL object from URL string
                URL urlObject = new URL(urlString);
                //retrieve JSONArray of friends
                return retrieveFriendsInCourse(urlObject);
            } catch (MalformedURLException mue) {
                Log.e(TAG, "URL Error: " + Log.getStackTraceString(mue));
                return null;
            } catch (UnsupportedEncodingException uee) {
                Log.e(TAG, "Unsupported Encoding Exception: " + Log.getStackTraceString(uee));
                return null;
            }
        } else {
            //return null if error with program logic occurs; ideally this should never happen
            return null;
        }
    }

    /**
     * Sends a GET request to the JSON API, and parses the result into a JSONArray.
     *
     * @param url   The URL pointing to the API.
     * @return  A JSONArray of friends registered for the class given in the URL.
     */
    private JSONArray retrieveFriendsInCourse(URL url) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {
            //try to open connection to the API URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            Log.i(TAG, "connection is set");

            //connect to API, get response code
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "server returned: " + response);

            //if response is not 200, 400 or 401 - unexpected error has occurred
            //ideally this should never happen; problem with program or APi logic if it does
            if (response != HttpURLConnection.HTTP_OK
                    && response != HttpURLConnection.HTTP_UNAUTHORIZED
                    && response != HttpURLConnection.HTTP_BAD_REQUEST) {
                Log.e(TAG, "Response not OK, UNAUTHORIZED or BAD REQUEST; aborting read");
                return null;
            }

            //if response 400 or 401, open reader to error stream
            if(response == HttpURLConnection.HTTP_UNAUTHORIZED
                    || response == HttpURLConnection.HTTP_BAD_REQUEST) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                //otherwise, open reader to input stream
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            //append results from buffered reader to StringBuffer
            StringBuffer json = new StringBuffer();
            String temp = "";
            while ((temp = reader.readLine()) != null) {
                json.append(temp).append("\n");
            }
            reader.close();
            Log.d(TAG, "JSON result: " + json.toString());
            //if response code was not 200, return error
            if(response == HttpURLConnection.HTTP_UNAUTHORIZED
                    || response == HttpURLConnection.HTTP_BAD_REQUEST) {
                JSONObject errorObj = new JSONObject(json.toString());
                JSONArray jsonErrorArray = new JSONArray();
                jsonErrorArray.put(errorObj);
                return jsonErrorArray;
            }
            //else, return valid results
            return new JSONArray(json.toString());
        } catch (IOException ioe) {
            Log.e(TAG, "IO Exception: " + Log.getStackTraceString(ioe));
            return null;
        } catch (JSONException je) {
            Log.e(TAG, "JSON Exception: " + Log.getStackTraceString(je));
            return null;
        } finally {
            //close reader when done
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.getMessage());
                }
            }
            //disconnect from httpurlconnection when done
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (IllegalStateException ise) {
                    Log.e(TAG, ise.getMessage());
                }
            }
        }
    }
}
