package com.dawsonlpx3;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dawsonlpx3.canceled_class_feature.CanceledFragment;
import com.dawsonlpx3.find_teacher_feature.FindTeacherFragment;
import com.thbs.skycons.library.CloudFogView;
import com.thbs.skycons.library.CloudHvRainView;
import com.thbs.skycons.library.CloudRainView;
import com.thbs.skycons.library.CloudSnowView;
import com.thbs.skycons.library.CloudThunderView;
import com.thbs.skycons.library.CloudView;
import com.thbs.skycons.library.SunView;
import com.thbs.skycons.library.WindView;

public class HomeFragment extends Fragment {

    private final String TAG = "HomeFragment";
    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private final int MY_PERMISSIONS_INTERNET = 2;
    private Boolean userPermissions = true; // Flag for user permissions

    /**
     * Inflate the fragment_home layout. This fragment will replace the
     * side_frame in the MainActivity
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    /**
     * Retrieves all widgets from the view that need an onclick listener such as the menu widgets.
     * Verifies the permissions to access user's location and wifi connection in order to determine
     * the current weather. Uses a GPS Tracker in order to display to determine the weather at the
     * user's current location.
     *
     * @param view View
     * @param savedInstanceState Bundle
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton dawsonImageBtn = (ImageButton) view.findViewById(R.id.dawsonImageView);
        dawsonImageBtn.setOnClickListener(showDawsonWeb);

        ImageButton weatherImageBtn = (ImageButton) view.findViewById(R.id.weatherImageButton);
        weatherImageBtn.setOnClickListener(showWeather);

        ImageButton findTeacherImageBtn = (ImageButton) view.findViewById(R.id.findTeacherImageButton);
        findTeacherImageBtn.setOnClickListener(showFindTeacher);

        ImageButton calendarImageBtn = (ImageButton) view.findViewById(R.id.calendarImageButton);
        calendarImageBtn.setOnClickListener(showAddToCalendar);

        ImageButton acedemicCalendarImageBtn = (ImageButton) view.findViewById(R.id.acedemicCalendarImageButton);
        acedemicCalendarImageBtn.setOnClickListener(showAcedemicCalendar);

        ImageButton classCancelledImageBtn = (ImageButton) view.findViewById(R.id.classCancelImageButton);
        classCancelledImageBtn.setOnClickListener(showClassCancelled);

        ImageButton noteImageBtn = (ImageButton) view.findViewById(R.id.noteImageButton);
        noteImageBtn.setOnClickListener(showNote);

        ImageButton teamLogo = (ImageButton) view.findViewById(R.id.teamLogo);
        teamLogo.setOnClickListener(showAboutPage);

        checkPermissions(); //checks for persmission to access location and internet connection for the weather

        //get longitude and latitude from GPS tracker oject
        GPSTracker gps = new GPSTracker(this.getActivity());
        gps.getLocation();
        String lat = Double.toString(gps.getLatitude());
        String lon = Double.toString(gps.getLongitude());

        try {
            //open an async to get the api weather and get the relevant info
            TemperatureAsyncTask tempTask = new TemperatureAsyncTask(lat, lon);
            tempTask.execute();
            String[] temperature = tempTask.get();
            String weather = determineWeatherById(temperature[1]);
            setWeatherLayout(view, weather, temperature[0]);
        }catch(Exception e){
            Log.e(TAG, "error: " + e.getMessage());
        }
    }

    /**
     * The id that got from the weather api, has a range of different weather.
     * Id starts with 2: is similar to Thunder.
     * Id starts with 3 and 5: is similar to heavy rain
     * Id starts with 6: is snow
     * Id starts with 7: is fog
     * Id starts with 8: is cloudy
     * Id starts with 9: is windy
     * by default return sunny
     *
     * @param id
     * @return String containing the status of the current weather
     */
    private String determineWeatherById(String id) {
        int firstNumber = Integer.parseInt(id.substring(0, 1));
        switch (firstNumber) {
            case 2:
                return "THUNDER";
            case 3:
            case 5:
                return "HEAVY_RAIN";
            case 6:
                return "SNOW";
            case 7:
                return "FOG";
            case 8:
                return "CLOUDY";
            case 9:
                return "WINDY";
        }

        return "SUNNY";
    }

    /**
     * Show Dawson Computer Science web page on a browser
     * @param view
     */
    private View.OnClickListener showDawsonWeb = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.dawsoncollege.qc.ca/computer-science-technology/"));
            startActivity(intent);
        }
    };

    /**
     *
     * Show Weather fragment
     */
    private View.OnClickListener showWeather = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new WeatherActivity())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show Find Teacher fragment
     */
    private View.OnClickListener showFindTeacher = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new FindTeacherFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show Add To Calendar fragment
     */
    private View.OnClickListener showAddToCalendar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new AddToCalendarFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show Acedemic Calendar fragment
     */
    private View.OnClickListener showAcedemicCalendar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new AcedemicCalendarFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show class cancelled fragment
     */
    private View.OnClickListener showClassCancelled = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new CanceledFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show Note fragment
     */
    private View.OnClickListener showNote = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new NotesFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show About Page
     *
     * @param view
     */
    private View.OnClickListener showAboutPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new AboutFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Set the view in the FrameLayout with a SkyconView and a TextView. The
     * SkyconView is determined by the weather (2nd parameter), and TextView is the
     * temperature (3rd parameter).
     *
     * @param view - Need this view to find FrameLayout and TextView
     * @param weather - weather in terms of WINDY, SUNNY, ...
     * @param temp - temperature
     */
    private void setWeatherLayout(View view, String weather, String temp) {
        FrameLayout frameLayout = view.findViewById(R.id.weatherFrameLayout);
        TextView tempTextView = view.findViewById(R.id.tempTextView);
        View skyconView = createSkyconView(weather);

        tempTextView.setText(temp);
        frameLayout.addView(skyconView, 0);
    }

    /**
     * The {@link com.thbs.skycons.library.SkyconView} can be get from
     * https://github.com/torryharris/Skycons.
     *
     * Determine SkyconView by the weather
     *
     * @param weather - it can WINDY, SUNNY, CLOUDY, ...
     * @return SkyconView
     */
    private View createSkyconView(String weather) {
        View view = null;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        switch (weather) {
            case "WINDY":
                view = new WindView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "CLOUDY":
                view = new CloudView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "SUNNY":
                view = new SunView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "HEAVY_RAIN":
                view = new CloudHvRainView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "SNOW":
                view = new CloudSnowView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "LIGHT_RAIN":
                view = new CloudRainView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "FOG":
                view = new CloudFogView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "THUNDER":
                view = new CloudThunderView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
        }

        params.width = 300;
        params.height = 300;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        view.setLayoutParams(params);

        return view;
    }

    /**
     * Check the device's current permissions for needed permissions. If they are not enabled, then
     * ask the user to give permission.
     */
    private void checkPermissions(){
        Log.d(TAG, "checkPermissions");
        // Check the permissions for ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Ask user for permission to use Access_FINE_LOCATION
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        }

        // Check the permissions for INTERNET permission
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.INTERNET)
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
}
