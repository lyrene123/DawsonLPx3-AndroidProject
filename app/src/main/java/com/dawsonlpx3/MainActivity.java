package com.dawsonlpx3;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;

/**
 * Launches the Main Activity that will display the the app's main interaction with
 * the user.
 *
 *  TODO : more detailed javadocs, better to implement this class level javadocs when class is more or less done
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NotesFragment.OnNoteSelectedListener {

    private final String TAG = "LPx3-Main";
    private String FNAME, LNAME, PASSWORD, EMAIL, TIMESTAMP;
    /**
     * Check the Shared Preferences and verify if the user's credential exist. If
     * yes, then store them as constants and if none existing, then launch the Register
     * Activity in order to retrieve the user credentials from the user which will be stored
     * in Shared Preferences.
     */
    private void checkForUserAuthentication(){
        Log.d(TAG, "checkForUserAuthentication launched");

        //retrieve from shared prefs any existing user credentials.
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        this.FNAME = prefs.getString("fname", null);
        this.LNAME = prefs.getString("lname", null);
        this.PASSWORD = prefs.getString("password", null);
        this.EMAIL = prefs.getString("email", null);
        this.TIMESTAMP = prefs.getString("timestamp", null);

        //if no or some credentials missing, then launch the register activity
        if(this.FNAME == null || this.LNAME == null || this.PASSWORD == null ||
                this.EMAIL == null){
            Log.d(TAG,"Launching register activity");
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

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


//        checkForUserAuthentication(); //retrieve user credentials from SharedPref

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

        if (id == R.id.nav_classCancel) {
            Fragment frag = new CanceledActivity();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_findTeacher) {
            Fragment frag = new FindTeacherActivity();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_addToCalendar) {
            Fragment frag = new CalendarActivity();
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
        } else if (id == R.id.nav_weather) {
            Fragment frag = new WeatherActivity();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_setting) {
            Fragment frag = new SettingsActivity();
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, frag)
                    .commit();
        } else if (id == R.id.nav_dawson) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


}
