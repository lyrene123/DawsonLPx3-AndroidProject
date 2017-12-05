package com.dawsonlpx3.home_feature;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dawsonlpx3.about_feature.AboutFragment;
import com.dawsonlpx3.R;
import com.dawsonlpx3.async_utils.DawsonRssXmlDownloadTask;
import com.dawsonlpx3.async_utils.TemperatureAsyncTask;
import com.dawsonlpx3.calendar_feature.AcedemicCalendarFragment;
import com.dawsonlpx3.calendar_feature.AddToCalendarFragment;
import com.dawsonlpx3.canceled_class_feature.CanceledClassAdapter;
import com.dawsonlpx3.canceled_class_feature.CanceledFragment;
import com.dawsonlpx3.canceled_class_feature.DawsonRssXmlParser;
import com.dawsonlpx3.data.CanceledClassDetails;
import com.dawsonlpx3.find_teacher_feature.FindTeacherFragment;
import com.dawsonlpx3.notes_feature.NotesFragment;
import com.dawsonlpx3.weather_feature.GPSTracker;
import com.dawsonlpx3.weather_feature.WeatherFragment;
import com.thbs.skycons.library.CloudFogView;
import com.thbs.skycons.library.CloudHvRainView;
import com.thbs.skycons.library.CloudRainView;
import com.thbs.skycons.library.CloudSnowView;
import com.thbs.skycons.library.CloudThunderView;
import com.thbs.skycons.library.CloudView;
import com.thbs.skycons.library.SunView;
import com.thbs.skycons.library.WindView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Fragment activity that handles the display of the content of the Home Page of the application
 * and the interaction between the user and the Home Page. The Home Page will display the Dawson college
 * logo, the app logo, the current weather, and the list of cancelled classes. Sets the click listeners
 * on the Dawson college logo and the app logo. Starts an async task to retrieve all the cancelled classes
 * from the Dawson Rss feed in order to display to the view. Handles as well the checking of permission
 * to access the user's current location and internet connection in order to retrieve current temperature.
 *
 * @author Lyrene Labor
 * @autor Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefgleur
 */
public class HomeFragment extends Fragment {

    private final String TAG = "HomeFragment";
    private final String CELCUS = (char) 0x00B0 + "C";
    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private final int MY_PERMISSIONS_INTERNET = 2;
    private Boolean userPermissions = true; // Flag for user permissions

    private final String RSS_URL =
            "https://www.dawsoncollege.qc.ca/wp-content/external-includes/cancellations/feed.xml";
    Context context;
    List<CanceledClassDetails> canceled;
    private DawsonRssXmlDownloadTask task;
    private ListView classCancelListView;
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

        classCancelListView = (ListView) view.findViewById(R.id.classCancelListView);
        context = view.getContext();
        task = new DawsonRssXmlDownloadTask();
        task.execute(RSS_URL);
        try {
            populateListView(task.get());
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(context, getResources().getString(R.string.cancel_problem),
                    Toast.LENGTH_LONG).show();
        }

        ImageButton dawsonImageBtn = (ImageButton) view.findViewById(R.id.dawsonImageView);
        dawsonImageBtn.setOnClickListener(showDawsonWeb);

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
            setWeatherLayout(view, weather, temperature[0] + CELCUS);
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
<<<<<<< HEAD:app/src/main/java/com/dawsonlpx3/HomeFragment.java
=======
     *
     * Show Weather fragment
     */
    private View.OnClickListener showWeather = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new WeatherFragment())
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
>>>>>>> testing:app/src/main/java/com/dawsonlpx3/home_feature/HomeFragment.java
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

    /**
     * Ensures the XML download task completed successfully, and if so, creates an adapter to
     * populate the ListView with the data retrieved.
     *
     * @param canceled  A List of CanceledClassDetails objects, generated by the XML download task.
     */
    private void populateListView(List<CanceledClassDetails> canceled) {
        this.canceled = canceled;
        //display relevant errors to user in Toast as needed
        if (canceled == null){
            Toast.makeText(context, getResources().getString(R.string.connection_error),
                    Toast.LENGTH_LONG).show();
        } else if (checkIfNoCancelled()) {
            Toast.makeText(context, getResources().getString(R.string.empty_list_error),
                    Toast.LENGTH_LONG).show();
        } else {
            //if list generated and not empty, send to adapter, assign adapter to list
            CanceledClassAdapter adapter = new CanceledClassAdapter(context, canceled);
            classCancelListView.setAdapter(adapter);
        }
    }

    /**
     * Checks the data retrieved from the RSS feed to see if it represents cancelled classes, or
     * contains a message stating no classes are cancelled. If no classes are cancelled, the first
     * item in the list of CanceledClassDetails will have its title contain the string "No classes
     * cancelled." If this string is found, then no classes are found to be cancelled.
     * Additionally, no classes are found cancelled if the list is empty or null.
     *
     * @return true if no class is cancelled, false otherwise.
     */
    private boolean checkIfNoCancelled() {
        boolean isNoClassCancelled = true;
        if (canceled != null && ! canceled.isEmpty()) {
            if (! canceled.get(0).getTitle().equals("No classes cancelled.")) {
                isNoClassCancelled = false;
            }
        }
        return isNoClassCancelled;
    }
}
