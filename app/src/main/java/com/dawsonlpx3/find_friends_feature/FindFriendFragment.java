package com.dawsonlpx3.find_friends_feature;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dawsonlpx3.R;

/**
 * Fragment activity which handles the display of the location of a friend of the logged in user.
 * Receives all location information from the arguments passed to the fragment or from the Bundle if
 * it's not null. Retrieves all necessary widgets from the view and once view is ready,
 * all location information will be loaded into those widgets.
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefleur
 */
public class FindFriendFragment extends Fragment {

    private String name, time, course, section;
    private int day;
    private TextView locationTV, dayTV, timeTV, courseTV, sectionTV;
    private final String TAG = "Dawson-FindFriendFrag";

    /**
     * Retrieves the location information to display into the view from the
     * arguments passed to the fragment or from the Bundle object if the savedInstanceState is
     * not null.
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            if (getArguments() != null) {
                Log.d(TAG, "onCreate: retrieving from arguments");
                name = getArguments().getString("name");
                time = getArguments().getString("time");
                course = getArguments().getString("course");
                section = getArguments().getString("section");
                day = getArguments().getInt("day");
                Log.d(TAG, "Day from arguments: " + day);
            }
        } else {
            Log.d(TAG, "onCreate: retrieving from savedInstance");
            name = savedInstanceState.getString("name");
            time = savedInstanceState.getString("time");
            course = savedInstanceState.getString("course");
            section = savedInstanceState.getString("section");
            day = savedInstanceState.getInt("day");
        }
    }

    /**
     * Inflates the fragment into the view
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View containing the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_friend, container, false);
    }

    /**
     * Retrieves the necessary widgets from the view in order to display the location information
     * into the view.
     *
     * @param view View containing the fragment
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        locationTV = (TextView) view.findViewById(R.id.locationTitle);
        dayTV = (TextView) view.findViewById(R.id.dayTextView);
        timeTV = (TextView) view.findViewById(R.id.timeTextView);
        courseTV = (TextView) view.findViewById(R.id.courseTextView);
        sectionTV = (TextView) view.findViewById(R.id.sectionTextView);
        displayLocationInfo();
    }

    /**
     * Displays all location information into the widgets of the view
     */
    private void displayLocationInfo(){
        locationTV.setText(getResources().getString(R.string.locationOf) + " " + name);
        courseTV.setText(course);
        sectionTV.setText(section);
        timeTV.setText(time);
        dayTV.setText(retrieveDayValue());
    }

    /**
     * Retrieves the real string day of the week value of the integer day
     *
     * @return String day of the week
     */
    private String retrieveDayValue(){
        String[] dayArr = getResources().getStringArray(R.array.day_array);
        Log.d(TAG, "day: " + dayArr[day-1]);
        return dayArr[day-1];
    }

    /**
     * Saves all location information into the Bundle
     *
     * @param outState Bundle object
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: saving");
        outState.putString("name", name);
        outState.putString("time", time);
        outState.putString("course", course);
        outState.putString("section", section);
        outState.putInt("day", day);

    }



}
