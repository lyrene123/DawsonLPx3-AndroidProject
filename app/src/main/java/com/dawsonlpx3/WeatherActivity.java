package com.dawsonlpx3;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Activity Fragment to display the weather and location.
 *
 * @author Philippe Langlois-Pedroso, 1542705
 */
public class WeatherActivity extends Fragment {

    View view;
    private Context context;
    private GPSTracker gps;
    private TextView temperatureView;
    private final String TAG = "WeatherActivity";
    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private final int MY_PERMISSIONS_INTERNET = 2;
    private Boolean userPermissions = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_weather, container, false);

        Log.d(TAG, "Weather Activity onCreateView");

        // Get handles to required classes and information.
        context = this.getActivity();
        temperatureView = (TextView) view.findViewById(R.id.temperature_view);
        gps = new GPSTracker(context);

        // Check is the application has the required permissions.
        checkPermissions();

        // Check if user allowed required permissions for temperature.
        if(userPermissions){
            // check if GPS is enabled.
            if(gps.canGetLocation()){
                Log.d(TAG, "GPS ENABLED");
                String latitude = Double.toString(gps.getLatitude());
                String longitude = Double.toString(gps.getLongitude());
                temperatureView.setText("Latitude: " +latitude.toString() +"Longitude: " +longitude.toString());
                getTemperature(latitude, longitude);
                new TemperatureTask(latitude, longitude).execute();
            }else{
                Log.d(TAG, "GPS NOT ENABLED");
                gps.showSettingsAlert();
            }
        }else{
            temperatureView.setText(R.string.tempDisabled);
        }

        return view;
    } // onCreateView()

    /**
     * Check the device's current permissions for the required permissions for the temperature.
     */
    private void checkPermissions(){
        // Check the permissions for ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Ask user for permission to use Access_FINE_LOCATION
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        }

        // Check the permissions for INTERNET permission
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Ask user for permission to use Access_FINE_LOCATION
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.INTERNET},
                    MY_PERMISSIONS_INTERNET);
        }
    } // checkPermissions()

    /**
     *  Checks the user's response to the request for permissions pop-ups.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "ACCESS_FINE_LOCATION permissions have been granted by user");
                } else {
                    Log.d(TAG, "ACCESS_FINE_LOCATION permissions have been denied by user");
                    userPermissions = false;
                }
                return;
            }
            case MY_PERMISSIONS_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "INTERNET permissions have been granted by user");
                } else {
                    Log.d(TAG, "INTERNET permissions have been denied by user");
                    userPermissions = false;
                }
                return;
            }
        } // end of switch(requestCode)
    } // onRequestPermissionResult

    /**
     *
     * @param
     */
    private void getTemperature(String lat, String lon) {
        TemperatureTask tempTask = new TemperatureTask(lat, lon);
    }

    /**
     *
     */
    private class TemperatureTask extends AsyncTask<String, Void, String>{

        private String lat;
        private String lon;
        private static final String OPEN_WEATHER = "http://api.openweathermap.org/data/2.5/weather?";
        private static final String API_KEY = "&appid=1845a7224a9c4164a4007cae1129a582";

        public TemperatureTask(String lat, String lon){
            this.lat = lat;
            this.lon = lon;
        } // TemperatureTask()

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "TemperatureTask: onPreExecute");
        } // onPreExecute()

        @Override
        protected void onPostExecute(String result){
            Log.d(TAG, "onPostExecute");
            temperatureView.setText(result);
        } // onPostExecute()

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doInBackground Temperature Async");
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

                // Read data from teh response.
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
                double tempKelvin = main.getDouble("temp");
                Log.d(TAG, "temperature in K: " +Double.toString(tempKelvin));
                double tempCelsius = tempKelvin -273.15;
                tempCelsius = (double)Math.round(tempCelsius * 10) / 10;
                Log.d(TAG, "temperature in C: " +Double.toString(tempCelsius));
                temperature = Double.toString(tempCelsius) +"\u00b0" +"C";

            }catch(Exception e){
                Log.d(TAG, e.getMessage());
            }finally{
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
} // WeatherActivity

