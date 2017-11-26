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
 *  An AsyncTask that creates an Asynchronous thread to communicate with openweathermap.org
 *  in order to get the current temperature by using a latitude and longitude passed to the
 *  class.
 *
 *  @authors Philippe Langlois, Lyrene Labor, Peter Bellefleur, Pengkim Sy
 */
public class TemperatureAsyncTask extends AsyncTask<String, Void, String> {

    private String lat;
    private String lon;
    private static final String TAG = "TemperatureAsyncTask";
    private static final String OPEN_WEATHER = "http://api.openweathermap.org/data/2.5/weather?";
    private static final String API_KEY = "&appid=1845a7224a9c4164a4007cae1129a582";

    /**
     * Constructor that is passed a latitude and longitude String.
     *
     * @param lat
     * @param lon
     */
    public TemperatureAsyncTask(String lat, String lon){
        Log.d(TAG, "TemperatureTask: Constructor");
        this.lat = lat;
        this.lon = lon;
    } // TemperatureTask()

    /**
     * Overridden AsyncTask method that deals with any setup to be made before the Task starts.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "TemperatureTask: onPreExecute");
    } // onPreExecute()

    /**
     * Returns the local temperature in degrees Celsius
     *
     * @param result
     */
    @Override
    protected void onPostExecute(String result){
        Log.d(TAG, "TemperatureTask: onPostExecute");
    } // onPostExecute()

    /**
     *  Perform the request and parsing of data in teh background of the fragment.
     *
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "TemperatureTask: doInBackground");
        String temperature = "";
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            // Setup the URL.
            URL url = new URL(OPEN_WEATHER +"lat=" +lat +"&lon=" +lon +API_KEY);
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
                return "Server Returned: " +Integer.toString(response);
            }

            // Read data from the response.
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp = "";

            // Parse through the response.
            while((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            // Obtain needed data from response.
            JSONObject jObj = new JSONObject(json.toString());
            JSONObject main = jObj.getJSONObject("main");
            Log.d(TAG, main.toString());
            // Temperature is given in Kelvin
            double tempKelvin = main.getDouble("temp");
            Log.d(TAG, "temperature in K: " +Double.toString(tempKelvin));
            // Convert Kelvin to Celsius
            double tempCelsius = tempKelvin -273.15;
            // Round the temperature to a single decimal place
            tempCelsius = (double)Math.round(tempCelsius * 10) / 10;
            Log.d(TAG, "temperature in C: " +Double.toString(tempCelsius));

            // Final value as a string with symbol for Celsius
            temperature = "Current temperature in your area: " +Double.toString(tempCelsius)
                        +"\u00b0" +"C";

        }catch(Exception e){
            Log.d(TAG, e.getMessage());
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
        return temperature;
    } // doInBackground()
} // TemperatureTask
