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
 *
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
     *
     * @param params
     * @return
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
            if(response != HttpURLConnection.HTTP_OK){
                Log.d(TAG, "Aborting read. Response was not 200");
                return null;
            }

            // Read data from the response.
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer json = new StringBuffer();
            String tmp = "";

            // Parse through the response.
            while((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            // Obtain needed data from response.
            JSONArray jArr = new JSONArray(json.toString());
            Log.d(TAG, jArr.toString());
            return jArr;

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
