package com.dawsonlpx3;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Asynchronous task to communicate with openweathermap.org in order to obtain the uv index for the
 * user's current location.
 *
 * @author Philippe Langlois-Pedroso, 1542705
 */

public class UVAsyncTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "UVAsyncTask";
    private String APICALL = "http://api.openweathermap.org/data/2.5/uvi?";
    //api.openweathermap.org/data/2.5/uvi?lat=37.75&lon=-122.37
    private static final String API_KEY = "&appid=1845a7224a9c4164a4007cae1129a582";

    /**
     * Constructor that is passed a latitude and longitude and finishes constructin an API call url.
     *
     * @param lat
     * @param lon
     */
    public UVAsyncTask(double lat, double lon){
        Log.d(TAG, "UVAsyncTask: Constructor");
        APICALL = APICALL +"lat=" +Double.toString(lat) +"&lon=" +Double.toString(lon);
        Log.d(TAG, "UV_API: " +APICALL);
    } // ForecastTask()

    /**
     * Overridden AsyncTask method that deals with any setup to be made before the Task starts.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "UVAsyncTask: onPreExecute");
    } // onPreExecute()

    /**
     * Displays the result from the api call into the Weather Activity
     *
     * @param result
     */
    @Override
    protected void onPostExecute(String result){
        Log.d(TAG, "UVAsyncTask: onPostExecute");
    } // onPostExecute()

    /**
     *  Perform the request and parsing of data in the background of the fragment.
     *
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        String uv = "";
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            // Setup the URL.
            URL url = new URL(APICALL +API_KEY);
            Log.d(TAG, url.toString());
            // Setup HttpURLConnection using the URL object.
            conn = (HttpURLConnection) url.openConnection();
            // Setup connection options
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
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
            JSONObject jObj = new JSONObject(json.toString());
            uv = Double.toString(jObj.getDouble("value"));
            Log.d(TAG, uv);

            return uv;

        }catch(Exception e){
            Log.d(TAG, "ERROR WITH REQUEST: " +e.getMessage());
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
        return uv;
    } // doInBackground()
} // UVAsyncTask

