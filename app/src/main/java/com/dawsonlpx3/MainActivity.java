package com.dawsonlpx3;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import com.dawsonlpx3.canceled_class_feature.CanceledFragment;
import com.dawsonlpx3.async_utils.WhereIsFriendAsyncTask;
import com.dawsonlpx3.data.TeacherDetails;
import com.dawsonlpx3.find_friends_feature.AllFriendsFragment;
import com.dawsonlpx3.find_friends_feature.FindFriendFragment;
import com.dawsonlpx3.find_teacher_feature.ChooseTeacherFragment;
import com.dawsonlpx3.find_teacher_feature.FindTeacherFragment;
import com.dawsonlpx3.find_teacher_feature.TeacherContactFragment;
import com.dawsonlpx3.friendBreak_feature.FriendBreakFragment;
import com.dawsonlpx3.weather_feature.WeatherActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
/**
 * Launches the Main Activity that will display the the app's main interaction with
 * the user.
 *
 *  TODO : more detailed javadocs, better to implement this class level javadocs when class is more or less done
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChooseTeacherFragment.OnTeacherSelectedListener,
        NotesFragment.OnNoteSelectedListener,
        AllFriendsFragment.onFriendSelectedListener {

    private final String TAG = "LPx3-Main";

    private String FNAME, LNAME, EMAIL, PASSWORD;
    private int day, time, hour;
    private String email, password, minStr;


    /**
     * Create a toolbar and make it toggle to show the drawer, and add click
     * listener to each of the drawer item, so that it can show the appropriate
     * fragment when pressed.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Log.d(TAG, "onCreate launched");
        if(savedInstanceState != null){
            Log.d(TAG, "restoring saved fragment...");
            Fragment savedFragment = getFragmentManager().getFragment(savedInstanceState,"currentFragment");
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, savedFragment)
                    .commit();
        } else {
            Log.d(TAG, "Displaying home fragment");
            Fragment frag = new HomeFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Toggle the the hamburger button to see the drawer on the left
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Set the click listener to all the items in the drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        styleNavigationItem(navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        Log.d(TAG, "onCreate launched");
        checkForUserAuthentication(); //retrieve user credentials from SharedPref

    }

    /**
     * Check the Shared Preferences and verify if the user's credential exist. If
     * yes, then store them as constants and if none existing, then launch the Register
     * Activity in order to retrieve the user credentials from the user which will be stored
     * in Shared Preferences.
     */
    private void checkForUserAuthentication(){
        Log.d(TAG, "checkForUserAuthentication launched");

        //retrieve from shared prefs any existing user credentials.
        SharedPreferences prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
        this.FNAME = prefs.getString("fname", null);
        this.LNAME = prefs.getString("lname", null);
        this.EMAIL = prefs.getString("email", null);
        this.PASSWORD = prefs.getString("password", null);
//        this.TIMESTAMP = prefs.getString("timestamp", null);

        Log.d(TAG, "firstname: " + prefs.getAll());
        Log.d(TAG, "lastname: " + this.LNAME);
        Log.d(TAG, "email: " + this.EMAIL);
        Log.d(TAG, "password: " + this.PASSWORD);
        //if no or some credentials missing, then launch the register activity
        if(this.FNAME == null || this.LNAME == null  || this.EMAIL == null || this.PASSWORD == null){
            Log.d(TAG,"Launching register activity");
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Change the color of the item in the drawer to blue
     * @param navigationView
     */
    private void styleNavigationItem(NavigationView navigationView) {
        int[][] state = new int[][] {
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed

        };

        int[] color = new int[] {
                Color.rgb(0,0,139),
                Color.rgb(0,0,139),
                Color.rgb(0,0,139),
                Color.rgb(0,0,139)
        };

        ColorStateList csl = new ColorStateList(state, color);

        navigationView.setItemTextColor(csl);
    }


    /**
     * When the Back button is pressed, if the drawer still open, close it, else
     * let AppCompatActivity handle the it.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * When the the item of the menu is pressed, replace the content_frame
     * to the Fragment. For example, when the Class Cancellation is pressed
     * replace the content_frame to show the Class Cancel layout.
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_home) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new HomeFragment())
                    .commit();
        } else if (id == R.id.nav_classCancel) {
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, new CanceledFragment())
                    .commit();
        } else if (id == R.id.nav_findTeacher) {
            Fragment frag = new FindTeacherFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_addToCalendar) {
            Fragment frag = new AddToCalendarFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_note) {
            Fragment frag = new NotesFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_acedemicCalendar) {
            Fragment frag = new AcedemicCalendarFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_find_friends) {
            Fragment frag = new AllFriendsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_weather) {
            Fragment frag = new WeatherActivity();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_about) {
            Fragment frag = new AboutFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_setting) {
            Fragment frag = new SettingsActivity();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_dawson) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.dawsoncollege.qc.ca/computer-science-technology/"));
            startActivity(intent);
        } else if (id == R.id.nav_whoIsFree) {
            Fragment frag = new FriendBreakFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Handles the click event on an item of the teachers list displayed in a ChooseTeacherFragment.
     * When an teacher is selected from the list view, a TeacherContactFragment will be inflated
     * to display the details of the selected teacher.
     *
     * @param teacher TeacherDetails objec
     */
    @Override
    public void onTeacherSelected(TeacherDetails teacher) {
        TeacherContactFragment teacherContactFragment = new TeacherContactFragment();
        Log.d(TAG, "onTeacherSelected started");
        Bundle args = new Bundle();
        args.putSerializable("teacher", teacher);
        teacherContactFragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.side_frame, teacherContactFragment)
                .addToBackStack(null)
                .commit();
    }


    /**
     * When the item on the note list is selected, the item note fragment will
     * replace the side_frame which is in the content_drawer layout.
     *
     * @param id - the id of the record in sqlite, so it can retrieved by the
     *             ItemNoteFragment and displays its note detail.
     */
    @Override
    public void onNoteSelected(int id) {
        ItemNoteFragment item = new ItemNoteFragment();

        Bundle args = new Bundle();
        args.putInt("id", id);
        item.setArguments(args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.side_frame, item)
                .addToBackStack(null)
                .commit();
    }
    /**
     * Saves the currently displayed fragment into the bundle.
     *
     * @param outState Bundle object
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState started");
        getFragmentManager().putFragment(outState, "currentFragment", getCurrentFragment());

    }

    /**
     * Returns the currently displayed fragment which is the fragment inflated in the side_frame
     * FrameLayout.
     *
     * @return Fragment object
     */
    private Fragment getCurrentFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.side_frame);
        return frag;
    }

    /**
     * Handles the selection of a friend in the list of friends retrieved from the allfriends
     * api call. When a friend is selected, an async task is started in order to make another
     * whereisfriend api call to the backend to retrieve the location of the selected friend.
     * Once a response is received, process the json response for any errors or empty data.
     * If all data is present, then inflate the FindFriendFragment to display the location
     *
     * @param friendemail email of the selected friend
     * @param name name of the selected friend
     */
    @Override
    public void onFriendSelected(String friendemail, String name) {
        Log.d(TAG, "onFriendSelected");
        getApiInformation(); //retrieve all necessary info for the api call

        WhereIsFriendAsyncTask friendAsyncTask = new WhereIsFriendAsyncTask();
        friendAsyncTask.execute(email, password, friendemail, day, time);
        try {
            JSONObject jsonResponse = friendAsyncTask.get();
            if(!checkForErrorsOrNoLoc(jsonResponse)){
                FindFriendFragment friendFragment = new FindFriendFragment();
                Bundle args = new Bundle();
                args.putString("name", name);
                args.putString("time", hour+":"+minStr);
                args.putInt("day", day);
                args.putString("course", jsonResponse.getString("course"));
                args.putString("section", jsonResponse.getString("section"));
                friendFragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.side_frame, friendFragment)
                        .addToBackStack(null)
                        .commit();
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Api Error getting friend location: " + Log.getStackTraceString(e));
            displayErrorReponse(getResources().getString(R.string.problem_retrieving_location));
        } catch (JSONException e) {
            Log.e(TAG, "Error retrieving json items: " + Log.getStackTraceString(e));
            displayErrorReponse(getResources().getString(R.string.problem_retrieving_location));
        }
    }

    /**
     * Process the Json object response returned from the whereisfriend api call to the backend.
     * Checks if the json object response is null and if yes, an alert dialog will display with appropriate message.
     * Checks if json object contains an error message, and if yes, an alert dialog will display with appropriate message.
     * Checks if json object contains an empty course and section data and if yes,
     * an alert dialog will display for the user to be notified that friend is not in class
     *
     * @param jsonResponse JSONObject returned from api call
     * @return true if JSON response is null, contains error, or empty course/section data
     *          false if course/section data exists
     */
    private boolean checkForErrorsOrNoLoc(JSONObject jsonResponse) {
        //check if response is null
        if(jsonResponse == null){
            Log.e(TAG, "Api Error getting friend location");
            displayErrorReponse(getResources().getString(R.string.problem_retrieving_location));
            return true;
        }
        //check if any errors in the response
        try {
            String error = jsonResponse.getString("error");
            Log.d(TAG, "Found error message");
            if(error.equalsIgnoreCase("invalid_credentials") || error.equalsIgnoreCase("invalid credentials")){
                displayErrorReponse(getResources().getString(R.string.invalid_creds));
            } else if(error.equalsIgnoreCase("bad or missing parameter")){
                displayErrorReponse(getResources().getString(R.string.missing_params));
            } else {
                displayErrorReponse(getResources().getString(R.string.not_friends));
            }
            return true;
        } catch (JSONException e) {
            //check if course/section data is empty or not
            try {
                String course = jsonResponse.getString("course");
                String section = jsonResponse.getString("section");

                if(course.isEmpty() || section.isEmpty()){
                    Log.d(TAG, "unknown whereabouts, not in class");
                    displayErrorReponse(getResources().getString(R.string.not_in_class));
                    return true;
                }
                return false; //return false if no error, no null json response, and no empty course/section
            } catch (JSONException e1) {
                Log.e(TAG, "Api Error getting friend location: " + Log.getStackTraceString(e1));
                displayErrorReponse(getResources().getString(R.string.problem_retrieving_location));
                return true;
            }
        }
    }

    /**
     * Retrieves and stores into instance variables all data necessary to pass into the
     * whereisfriend api call to the backend. These data are passed into the async task and
     * passed into the url of the api call.
     */
    private void getApiInformation(){
        //get current day and time
        Calendar calendar = Calendar.getInstance();
        Log.d(TAG, "DAY OF WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
        day = convertDayNumber(calendar.get(Calendar.DAY_OF_WEEK));
        Log.d(TAG, "DAY OF WEEK converted: " + day);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        //format the time to combine both hour and min. Append an extra 0 in front of the minutes if only one digit
        minStr = "";
        String timeStr = "";
        if((min+"").length() == 2) {
            timeStr = hour + "" + min;
            minStr = min+"";
        } else {
            timeStr = hour + "0" + min;
            minStr = "0" + min;
        }
        time = Integer.parseInt(timeStr); //convert minutes into integer

        //retrieve email and password credentials from SharedPref
        SharedPreferences prefs = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        email = prefs.getString("email", "");
        password = prefs.getString("password", "");
    }

    /**
     * Helper method that retrieves an integer representing the current day of the week
     * from the java Calendar class and converts it to the appropriate value that matches the
     * criteria of the whereisfriend api call
     *
     * @param dayOfWeek an integer representing a day of the week
     * @return converted integer
     */
    private int convertDayNumber(int dayOfWeek){
        switch (dayOfWeek){
            case 1 :
                return 7; //sunday
            case 2 :
                return  1; //monday
            case 3 :
                return 2; //tuesday
            case 4 :
                return 3; //wednesday
            case 5 :
                return 4; //thursday
            case 6 :
                return 5; //friday
            case 7 :
                return 6; //saturday
        }
        return 0;
    }

    /**
     * Creates and displays an Alert Dialog with the input string message as the content.
     *
     * @param message Message to display in the dialog
     */
    private void displayErrorReponse(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(R.string.warning)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
