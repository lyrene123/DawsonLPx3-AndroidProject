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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Activity Fragment to display teh current local temperature in Celsius and to dispaly a 5
 * day forecast for a selected city and country. Both are accessed through OpenWeatherMap
 * Source: https://openweathermap.org
 *
 * @author Philippe Langlois-Pedroso, 1542705
 */
public class WeatherActivity extends Fragment implements View.OnClickListener {

    View view;
    private Context context; // Current Activity Context
    private GPSTracker gps; // Custom class for GPS functionality
    private TextView temperatureView;
    private EditText cityView;
    private Spinner countriesSpinner;
    private Button forecastButton;
    private final String TAG = "WeatherActivity";
    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private final int MY_PERMISSIONS_INTERNET = 2;
    private Boolean userPermissions = true; // Flag for user permissions

    /**
     * Create the fragment and any initialization code that is required within the.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_weather, container, false);
        Log.d(TAG, "Weather Activity onCreateView");

        // Get handles to required classes and information.
        context = this.getActivity();
        gps = new GPSTracker(context);
        cityView = (EditText) view.findViewById(R.id.weather_city);
        temperatureView = (TextView) view.findViewById(R.id.temperature_view);

        // Check is the application has the required permissions.
        checkPermissions();
        if(userPermissions){
            // Check if GPS is enabled
            if(gps.canGetLocation()){
                displayTemperature();
            }else{
                Log.d(TAG, "GPS NOT ENABLED");
                gps.showSettingsAlert();
            }
        }else{
            temperatureView.setText(R.string.tempDisabled);
        }

        cityView = (EditText) view.findViewById(R.id.weather_city);
        setupWeather(); // Setup the views for the weather functionality

        return view;
    } // onCreateView()

    /**
     * OnClick handler for the forecast button. Will call an AsyncTask in order to find the 5-day
     * forecast from the city and country inputted by the user.
     *
     * @param v
     */
    @Override
    public void onClick(View v){
        Log.d(TAG, "getForecast");
        String city = cityView.getText().toString();
        String countryCode = countriesSpinner.getSelectedItem().toString();
        Log.d(TAG, city);
        Log.d(TAG, countryCode);
    }

    /**
     *  Setup the EditText and Spinner for use in displaying the 5-day weather forecast.
     */
    private void setupWeather(){
        Log.d(TAG, "setupWeather");
        // Handle to the spinner
        countriesSpinner = (Spinner) view.findViewById(R.id.countries_spinner);
        // Get a String array of all countries with an ISO 3166-1 alpha-2 codes
        String[] isoCountryCodes = Locale.getISOCountries();
        // ArrayAdapter to add items to the spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, isoCountryCodes);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countriesSpinner.setAdapter(spinnerArrayAdapter);
        countriesSpinner.setSelection(37); // set default value to CA

        forecastButton = (Button) view.findViewById(R.id.calculate_forecast_button);
        forecastButton.setOnClickListener(this);
    } // setupWeather()

    /**
     *  Display the local temperature in degrees Celsius.
     */
    private void displayTemperature(){
        Log.d(TAG, "displayTemperature");
        // Get the Latitude and Longitude
        String latitude = Double.toString(gps.getLatitude());
        String longitude = Double.toString(gps.getLongitude());
        // Obtain the values for the user's current latitude and longitude
        new TemperatureTask(latitude, longitude).execute();
    } // displayTemperature()

    /**
     * Check the device's current permissions for the required permissions for the temperature.
     */
    private void checkPermissions(){
        Log.d(TAG, "checkPermissions");
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
     *  An AsyncTask that creates an Asynchronous thread to communicate with openweathermap.org
     *  in order to get the current temperature by using a latitude and longitude passed to the
     *  class.
     *
     *  @author Philippe Langlois-Pedroso, 1542705
     */
    private class TemperatureTask extends AsyncTask<String, Void, String>{

        private String lat;
        private String lon;
        private static final String OPEN_WEATHER = "http://api.openweathermap.org/data/2.5/weather?";
        private static final String API_KEY = "&appid=1845a7224a9c4164a4007cae1129a582";

        /**
         * Constructor that is passed a latitude and longitude String.
         *
         * @param lat
         * @param lon
         */
        public TemperatureTask(String lat, String lon){
            this.lat = lat;
            this.lon = lon;
        } // TemperatureTask()

        /**
         * Overidden AyncTask method that deals with any setup to be made before the Task starts.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "TemperatureTask: onPreExecute");
        } // onPreExecute()

        /**
         * Upon the conclusion of the Task, methos will set the text value for the temperature
         * Text View.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result){
            Log.d(TAG, "onPostExecute");
            temperatureView.setText(result);
        } // onPostExecute()

        /**
         *  Perform the request and parsing of data in teh background of the fragment.
         *
         * @param params
         * @return
         */
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
                // Temperature is given in Kelvin
                double tempKelvin = main.getDouble("temp");
                Log.d(TAG, "temperature in K: " +Double.toString(tempKelvin));
                // Convert Kelvin to Celsius
                double tempCelsius = tempKelvin -273.15;
                // Round the temperature to a single decimal place
                tempCelsius = (double)Math.round(tempCelsius * 10) / 10;
                Log.d(TAG, "temperature in C: " +Double.toString(tempCelsius));
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
} // WeatherActivity

