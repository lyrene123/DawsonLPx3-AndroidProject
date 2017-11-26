package com.dawsonlpx3;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
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
import android.view.View;
import android.widget.FrameLayout;

import com.dawsonlpx3.data.TeacherDetails;
import com.dawsonlpx3.find_teacher_feature.ChooseTeacherFragment;
import com.dawsonlpx3.find_teacher_feature.FindTeacherFragment;
import com.dawsonlpx3.find_teacher_feature.TeacherContactFragment;
import java.util.List;

/**
 * Launches the Main Activity that will display the the app's main interaction with
 * the user.
 *
 *  TODO : more detailed javadocs, better to implement this class level javadocs when class is more or less done
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChooseTeacherFragment.OnTeacherSelectedListener 
        NotesFragment.OnNoteSelectedListener {

    private final String TAG = "LPx3-Main";
    private String FNAME, LNAME, PASSWORD, EMAIL, TIMESTAMP;
    private TeacherContactFragment teacherContactFragment;

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

        getFragmentManager().beginTransaction()
                .replace(R.id.side_frame, new HomeFragment())
                .addToBackStack(null)
                .commit();

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
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, new CanceledActivity())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_findTeacher) {
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, new FindTeacherFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_addToCalendar) {
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, new CalendarActivity())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_note) {
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, new NotesFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_acedemicCalendar) {
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, new AcedemicCalendarActivity())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_weather) {
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, new WeatherActivity())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_setting) {
            fragmentManager.beginTransaction()
                    .replace(R.id.side_frame, new SettingsActivity())
                    .commit();
        } else if (id == R.id.nav_dawson) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onTeacherSelected(TeacherDetails teacher) {
        this.teacherContactFragment = new TeacherContactFragment();

        Bundle args = new Bundle();
        args.putSerializable("teacher", teacher);
        teacherContactFragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, teacherContactFragment)
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
}
