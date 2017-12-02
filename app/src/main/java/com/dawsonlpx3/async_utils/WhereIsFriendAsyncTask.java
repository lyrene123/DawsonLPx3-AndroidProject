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


public class WhereIsFriendAsyncTask extends AsyncTask <Object, Void, JSONObject>{

    private final String TAG = "Dawson-WhereFriendTask";
    private final String api_url = "https://friendfinder05.herokuapp.com/api/api/whereisfriend?";

    @Override
    protected JSONObject doInBackground(Object... params) {
        Log.d(TAG, "doInBackground started with params: " + params.toString());
        String url = "";
        //verify if valid number of params, if not then return null
        if(params.length == 5 && !((String)(params[0])).isEmpty() && !((String)(params[1])).isEmpty()){
            url = api_url + "email=" + params[0] + "&password=" + params[1]
                    + "&friendemail=" + params[2] + "&day=" + (int)params[3]
                    + "&time=" + (int)params[4];
            Log.d(TAG, "URL: " + url);
            URL api_url = null;
            try {
                api_url = new URL(url);
            } catch (MalformedURLException e) {
                Log.e(TAG, "URl Error: " + Log.getStackTraceString(e));
                return null;
            }
            return retrieveLocation(api_url);
        } else {
            return null;
        }
    }

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

            if (response != HttpURLConnection.HTTP_OK && response != HttpURLConnection.HTTP_UNAUTHORIZED
                    && response != HttpURLConnection.HTTP_BAD_REQUEST) {
                Log.e(TAG, "Response not HTTP OK, UNAUTHORIZED NOR BAD REQUEST, aborting read");
                return null;
            }

            if(response == HttpURLConnection.HTTP_UNAUTHORIZED || response == HttpURLConnection.HTTP_BAD_REQUEST) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }

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
