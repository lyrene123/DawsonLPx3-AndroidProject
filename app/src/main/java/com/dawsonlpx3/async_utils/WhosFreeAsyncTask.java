package com.dawsonlpx3.async_utils;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  Extends the AsyncTask in order to  create a background thread in order to receiver information
 *  from the friendfinder website. Receives parameters that are user to construct the url object
 *  and communicate with the web api.
 *
 * @author Lyrene Labor
 + @author Pengkim Sy
 + @author Phil Langlois
 + @author Peter Bellefleur
 */
public class WhosFreeAsyncTask extends AsyncTask <String, Void, JSONArray>{

    private static final String TAG = "WhosFreeAsyncTask";
    private String API = "https://friendfinder05.herokuapp.com/api/api/friendbreak?";

    /**
     * Overridden AsyncTask method that deals with any setup to be made before the Task starts.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
    } // onPreExecute()

    /**
     *  The work that is performed in a background thread. Opens a connection to teh web api
     *  using information passed to this instance of the AsyncTask and returns the response.
     *
     * @param params
     * @return The response as a JSON array.
     */
    @Override
    protected JSONArray doInBackground(String... params) {
        Log.d(TAG, "doInBackground");
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try{
            URL url = new URL(API +"email=" +params[0] +"&password=" +params[1] +"&day=" +params[2]
                +"&start=" +params[3] +"&end=" +params[4]);

            Log.d(TAG, url.toString());
            // Setup HttpURLConnection using the URL object.
            conn = (HttpURLConnection) url.openConnection();
            // Setup connection options
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            Log.d(TAG, "connection setup complete, starting query");

            // Test connection
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "Response Code: " +Integer.toString(response));
            //if unexpected response, return null
            if (response != HttpURLConnection.HTTP_OK && response != HttpURLConnection.HTTP_UNAUTHORIZED) {
                Log.e(TAG, "Response not HTTP OK, UNAUTHORIZED NOR BAD REQUEST, aborting read");
                return null;
            }

            //if HTTP UNAUTHORIZED response, get the ErrorStream, otherwise the normal InputStream
            if(response == HttpURLConnection.HTTP_UNAUTHORIZED) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }

            //retrieve the json data
            StringBuffer json = new StringBuffer();
            String tmp = "";
            while ((tmp = reader.readLine()) != null) {
                json.append(tmp).append("\n");
            }
            reader.close();

            //return the response as a JSONObject array
            Log.d(TAG, "json result: " + json.toString());
            if(response == HttpURLConnection.HTTP_UNAUTHORIZED || response == HttpURLConnection.HTTP_BAD_REQUEST) {
                JSONObject errorObj = new JSONObject(json.toString());
                JSONArray jsonErrorArray = new JSONArray();
                jsonErrorArray.put(errorObj);
                return jsonErrorArray;
            }
            return new JSONArray(json.toString());

        }catch(Exception e){
            Log.d(TAG, "ERROR WITH REQUEST: " +e.getMessage());
            return null;
        }finally{
            // Clean-up any Objects that need to be closed.
            if (reader != null) {
                try {
                    Log.d(TAG, "Closing reader");
                    reader.close();
                } catch (IOException ioe) {
                    Log.d(TAG, ioe.getMessage());
                }
            }
            if(conn != null){
                try{
                    Log.d(TAG, "Disconnecting connection");
                    conn.disconnect();
                } catch(IllegalStateException ise) {
                    Log.d(TAG, ise.getMessage());
                }
            }
        }
    } // doInBackground()
}
