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
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
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
    private LinearLayout col1, col2, col3, col4, col5, col6;
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
        cityView = (EditText) view.findViewById(R.id.weather_city);
        col1 = (LinearLayout) view.findViewById(R.id.col_1);
        col2 = (LinearLayout) view.findViewById(R.id.col_2);
        col3 = (LinearLayout) view.findViewById(R.id.col_3);
        col4 = (LinearLayout) view.findViewById(R.id.col_4);
        col5 = (LinearLayout) view.findViewById(R.id.col_5);
        col6 = (LinearLayout) view.findViewById(R.id.col_6);

        checkPermissions(); // Check is the application has the required permissions.
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
        new ForecastTask(city, countryCode).execute();
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
     * Check the device's current permissions for needed permissions. If they are not enabled, then
     * ask the user to give permission.
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
     *  in order to get a 5-day forecast of the inputted city by the user.
     *
     *  @author Philippe Langlois-Pedroso, 1542705
     */
    private class ForecastTask extends AsyncTask<String, Void, JSONObject[]> {

        private String FORECAST = "http://api.openweathermap.org/data/2.5/forecast?q=";
        private static final String API_KEY = "&appid=1845a7224a9c4164a4007cae1129a582";

        /**
         * Constructor that is passed a latitude and longitude String.
         *
         * @param city
         * @param country
         */
        public ForecastTask(String city, String country){
            Log.d(TAG, "ForecastTask: Constructor");
            FORECAST = FORECAST +city +"," +country;
            Log.d(TAG, "FORECSAT_API: " +FORECAST);
        } // ForecastTask()

        /**
         * Overridden AsyncTask method that deals with any setup to be made before the Task starts.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "ForecastTask: onPreExecute");
        } // onPreExecute()

        /**
         * Upon the conclusion of the Task, method will set the appropriate data to the TextViews
         * for the forecast.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(JSONObject[] result){
            Log.d(TAG, "ForecastTask: onPostExecute");
            // forecastDay1.setText(result[0].toString());
            //forecastDay2.setText(result[1].toString());
            // forecastDay3.setText(result[2].toString());
            // forecastDay4.setText(result[3].toString());
            //forecastDay5.setText(result[4].toString());
            Log.d(TAG, result[0].toString());
            Log.d(TAG, result[1].toString());
            Log.d(TAG, result[2].toString());
            Log.d(TAG, result[3].toString());

            LinearLayout[] columns = new LinearLayout[]{col2, col3, col4, col5};
            double d = 0.0;
            try {
                for(int i = 0; i < columns.length; i++){
                    TextView tv1 = (TextView) columns[i].getChildAt(1);
                    d = result[i].getJSONObject("main").getDouble("temp")-273.15;
                    d = (double)Math.round((d) * 10) / 10;
                    tv1.setText(Double.toString(d));

                    TextView tv2 = (TextView) columns[i].getChildAt(2);
                    d = result[i].getJSONObject("main").getDouble("temp_min")-273.15;
                    d = (double)Math.round((d) * 10) / 10;
                    tv2.setText(Double.toString(d));

                    TextView tv3 = (TextView) columns[i].getChildAt(3);
                    d = result[i].getJSONObject("main").getDouble("temp_max")-273.15;
                    d = (double)Math.round((d) * 10) / 10;
                    tv3.setText(Double.toString(d));

                    TextView tv4 = (TextView) columns[i].getChildAt(4);
                    tv4.setText(Double.toString(result[i].getJSONObject("main").getDouble("pressure")));

                    TextView tv5 = (TextView) columns[i].getChildAt(5);
                    tv5.setText(Double.toString(result[i].getJSONObject("main").getDouble("sea_level")));

                    TextView tv6 = (TextView) columns[i].getChildAt(6);
                    tv6.setText(Double.toString(result[i].getJSONObject("main").getDouble("grnd_level")));

                    TextView tv7 = (TextView) columns[i].getChildAt(7);
                    tv7.setText(Integer.toString(result[i].getJSONObject("main").getInt("humidity")));

                    TextView tv8 = (TextView) columns[i].getChildAt(8);
                    d = result[i].getJSONObject("main").getDouble("temp_kf");
                    d = (double)Math.round((d) * 100) / 100;
                    tv8.setText(Double.toString(d));

                    TextView tv9 = (TextView) columns[i].getChildAt(9);
                    TextView tv10 = (TextView) columns[i].getChildAt(10);
                    JSONArray weatherArray = result[i].getJSONArray("weather");
                    tv9.setText(weatherArray.getJSONObject(0).getString("main"));
                    tv10.setText(weatherArray.getJSONObject(0).getString("description"));

                    TextView tv11 = (TextView) columns[i].getChildAt(11);
                    JSONObject cloudObject = result[i].getJSONObject("clouds");
                    tv11.setText(Integer.toString(cloudObject.getInt("all")));

                    TextView tv12 = (TextView) columns[i].getChildAt(12);
                    TextView tv13 = (TextView) columns[i].getChildAt(13);
                    JSONObject windObject = result[i].getJSONObject("wind");
                    d = windObject.getDouble("speed");
                    d = (double)Math.round((d) * 100) / 100;
                    tv12.setText(Double.toString(d));
                    d = windObject.getDouble("deg");
                    d = (double)Math.round((d) * 1000) / 1000;
                    tv13.setText(Double.toString(d));
                }
            }catch(Exception e){
                Log.d(TAG, e.getMessage());
            }
        } // onPostExecute()

        /**
         *  Perform the request and parsing of data in the background of the fragment.
         *
         * @param params
         * @return
         */
        @Override
        protected JSONObject[] doInBackground(String... params) {
            JSONObject[] forecast = null;
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            try {
                // Setup the URL.
                URL url = new URL(FORECAST +API_KEY);
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
                //JSONObject bulkForecast = jObj.getJSONObject("list");
                JSONArray jArr = jObj.getJSONArray("list");
                Log.d(TAG, jArr.get(0).toString());
                Log.d(TAG, jArr.get(1).toString());
                Log.d(TAG, Integer.toString(jArr.length()));
                JSONObject day1part1 = jArr.getJSONObject(0);
                Log.d(TAG, Double.toString(day1part1.getJSONObject("main").getDouble("temp")));

                // Start of day value
                Calendar cal = Calendar.getInstance(); // today
                long currentEpochValue = cal.getTimeInMillis()/1000; // epoch value in seconds
                Log.d(TAG, "Current time in epoch seconds: " +Long.toString(currentEpochValue));
                cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
                cal.set(Calendar.MINUTE, 0); // set minutes to zero
                cal.set(Calendar.SECOND, 0); //set seconds to zero
                long startTodayEpochValue = cal.getTimeInMillis()/1000; // epoch value in seconds
                Log.d(TAG, "Start of day in epoch seconds: " +Long.toString(startTodayEpochValue));
                long threeHourEpochJump = 60*60*3;
                int arrayOffset = 0;

                if(currentEpochValue < startTodayEpochValue+threeHourEpochJump){
                    // section1
                    Log.d(TAG, "Time Section 1");
                    Log.d(TAG, Long.toString(startTodayEpochValue+threeHourEpochJump));
                    Log.d(TAG, jArr.get(0).toString());
                }
                else if(currentEpochValue < (startTodayEpochValue+(threeHourEpochJump*2))){
                    // section2
                    Log.d(TAG, "Time Section 2");
                    Log.d(TAG, jArr.get(1).toString());
                    arrayOffset = 1;
                }
                else if(currentEpochValue < (startTodayEpochValue+(threeHourEpochJump*3))){
                    // section3
                    Log.d(TAG, "Time Section 3");
                    Log.d(TAG, jArr.get(2).toString());
                    arrayOffset = 2;
                }
                else if(currentEpochValue < (startTodayEpochValue+(threeHourEpochJump*4))){
                    // section4
                    Log.d(TAG, "Time Section 4");
                    Log.d(TAG, jArr.get(3).toString());
                    arrayOffset = 3;
                }
                else if(currentEpochValue < (startTodayEpochValue+(threeHourEpochJump*5))){
                    // section5
                    Log.d(TAG, "Time Section 5");
                    Log.d(TAG, jArr.get(4).toString());
                    arrayOffset = 4;
                }
                else if(currentEpochValue < (startTodayEpochValue+(threeHourEpochJump*6))){
                    // section6
                    Log.d(TAG, "Time Section 6");
                    Log.d(TAG, jArr.get(5).toString());
                    arrayOffset = 5;
                }
                else if(currentEpochValue < (startTodayEpochValue+(threeHourEpochJump*7))){
                    // section7
                    Log.d(TAG, "Time Section 7");
                    Log.d(TAG, jArr.get(6).toString());
                    arrayOffset = 6;
                }
                else{
                    // section8
                    Log.d(TAG, "Time Section 8");
                    Log.d(TAG, jArr.get(7).toString());
                    arrayOffset = 7;
                }

                Log.d(TAG, "THE DAY OBJECTS FOR FORECAST");
                JSONObject day1 = jArr.getJSONObject(0 +arrayOffset);
                JSONObject day2 = jArr.getJSONObject(8 +arrayOffset);
                JSONObject day3 = jArr.getJSONObject(16 +arrayOffset);
                JSONObject day4 = jArr.getJSONObject(24 +arrayOffset);
                //JSONObject day5 = jArr.getJSONObject(32 +arrayOffset);
                //Log.d(TAG, "WTF " +jArr.getJSONObject(38).toString());
                Log.d(TAG, Integer.toString(jArr.length()));

                Log.d(TAG, day1.toString());
                Log.d(TAG, day2.toString());
                Log.d(TAG, day3.toString());
                Log.d(TAG, day4.toString());
                //Log.d(TAG, day5.toString());

                forecast = new JSONObject[]{day1, day2, day3, day4};

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
            return forecast;
        } // doInBackground()
    } // ForecastTask
} // WeatherActivity

