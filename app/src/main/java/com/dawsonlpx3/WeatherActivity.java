package com.dawsonlpx3;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Activity Fragment to display the weather and location.
 *
 *  @author Philippe Langlois-Pedroso, 1542705
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
} // WeatherActivity

