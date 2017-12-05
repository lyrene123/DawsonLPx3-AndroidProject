package com.dawsonlpx3.async_utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by laborlyrene on 2017-12-01.
 */

public class IsFriendInCourseAsyncTask extends AsyncTask <String, Void, JSONArray>{

    private final String TAG = "LPx3-IsFriendInCourse";
    private final String API_URL = "https://friendfinder05.herokuapp.com/api/api/coursefriends?";

    @Override
    protected JSONArray doInBackground(String... params) {
        Log.d(TAG, "doInBackground started with params: " + params.toString());

        if(params.length == 4 && !params[0].isEmpty() && !params[1].isEmpty()) {
            String urlString = API_URL
                    + "email=" + params[0]
                    + "&password=" + params[1]
                    + "&coursename=" + params[2]
                    + "&section=" + params[3];
            Log.d(TAG, "URL: " + urlString);
            try {
                URL urlObject = new URL(urlString);
                return retrieveFriendsInCourse(urlObject);
            } catch (MalformedURLException mue) {
                Log.e(TAG, "URL Error: " + Log.getStackTraceString(mue));
                return null;
            }
        } else {
            return null;
        }
    }

    private JSONArray retrieveFriendsInCourse(URL url) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            Log.i(TAG, "connection is set");

            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "server returned: " + response);

            if (response != HttpURLConnection.HTTP_OK
                    && response != HttpURLConnection.HTTP_UNAUTHORIZED) {
                Log.e(TAG, "Response not OK, UNAUTHORIZED or BAD REQUEST; aborting read");
                return null;
            }

            if(response == HttpURLConnection.HTTP_UNAUTHORIZED) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }

            StringBuffer json = new StringBuffer();
            String temp = "";
            while ((temp = reader.readLine()) != null) {
                json.append(temp).append("\n");
            }
            reader.close();

            Log.d(TAG, "JSON result: " + json.toString());
            if(response == HttpURLConnection.HTTP_UNAUTHORIZED
                    || response == HttpURLConnection.HTTP_BAD_REQUEST) {
                JSONObject errorObj = new JSONObject(json.toString());
                JSONArray jsonErrorArray = new JSONArray();
                jsonErrorArray.put(errorObj);
                return jsonErrorArray;
            }
            return new JSONArray(json.toString());
        } catch (IOException ioe) {
            Log.e(TAG, "IO Exception: " + Log.getStackTraceString(ioe));
            return null;
        } catch (JSONException je) {
            Log.e(TAG, "JSON Exception: " + Log.getStackTraceString(je));
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.getMessage());
                }
            }
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
