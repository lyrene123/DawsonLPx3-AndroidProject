package com.dawsonlpx3.find_friends_feature;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dawsonlpx3.R;

public class FindFriendFragment extends Fragment {

    private String name, time, course, section;
    private int day;
    private TextView locationTV, dayTV, timeTV, courseTV, sectionTV;

    private final String TAG = "Dawson-FindFriendFrag";

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_friend, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        locationTV = (TextView) view.findViewById(R.id.locationTitle);
        dayTV = (TextView) view.findViewById(R.id.dayTextView);
        timeTV = (TextView) view.findViewById(R.id.timeTextView);
        courseTV = (TextView) view.findViewById(R.id.courseTextView);
        sectionTV = (TextView) view.findViewById(R.id.sectionTextView);
        displayLocationInfo();
    }

    private void displayLocationInfo(){
        locationTV.setText(getResources().getString(R.string.locationOf) + " " + name);
        courseTV.setText(course);
        sectionTV.setText(section);
        timeTV.setText(time);
        dayTV.setText(retrieveDayValue());
    }

    private String retrieveDayValue(){
        String[] dayArr = getResources().getStringArray(R.array.day_array);
        Log.d(TAG, "day: " + dayArr[day-1]);
        return dayArr[day-1];
    }

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
