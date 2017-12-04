package com.dawsonlpx3.canceled_class_feature;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dawsonlpx3.R;
import com.dawsonlpx3.data.CanceledClassDetails;
import com.dawsonlpx3.find_teacher_feature.FindTeacherFragment;

/**
 * The ShowCancelFragment class represents a Fragment that displays details about a cancelled
 * class at Dawson College.
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 * @author Phil Langlois
 * @author Pengkim Sy
 */
public class ShowCancelFragment extends Fragment {

    private View view;
    private CanceledClassDetails details;
    private TextView titleText;
    private TextView courseText;
    private TextView timeText;
    private TextView teacherText;
    private final String TAG = "LPx3-ShowCancel";

    /**
     * Creates and returns the View hierarchy associated with this Fragment. Inflates the layout
     * for this Fragment as defined in its layout resources, and retrieves data passed to it from
     * the Bundle.
     *
     * @param inflater  A LayoutInflater.
     * @param container The parent View containing the Fragment.
     * @param savedInstanceState    The Bundle passed to the Fragment.
     * @return  The inflated View.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        this.view = inflater.inflate(R.layout.activity_show_cancel, container, false);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                this.details = (CanceledClassDetails) getArguments().getSerializable("class");
            }
        } else {
            this.details = (CanceledClassDetails) savedInstanceState.getSerializable("class");
        }
        return view;
    }

    /**
     * Retrieves handles to View objects to be modified programmatically once the fragment's parent
     * Activity is created. Additionally, sets a click listener on the View representing a
     * teacher's name.
     *
     * @param savedInstanceState    The Bundle passed to the Fragment.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        this.titleText = (TextView) view.findViewById(R.id.titleTextView);
        titleText.setText(details.getTitle());
        this.courseText = (TextView) view.findViewById(R.id.courseTextView);
        courseText.setText(details.getCourse());
        this.timeText = (TextView) view.findViewById(R.id.timeTextView);
        timeText.setText(details.getDateCancelled());
        this.teacherText = (TextView) view.findViewById(R.id.teacherTextView);
        teacherText.setText(details.getTeacher());
        Log.i(TAG, "setting click listener on teacher name");
        teacherText.setOnClickListener(new View.OnClickListener() {
            /**
             * Loads informatiom about the teacher of a specific cancelled class.
             *
             * @param v The clicked View.
             */
            @Override
            public void onClick(View v) {
                Log.i(TAG, "teacher name clicked");
                loadTeacherInfo(v);
            }
        });
    }

    /**
     * Saves data in a Bundle, to restore after the fragment has been temporarily destroyed.
     *
     * @param outState The Bundle, which will contain the data to save.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceSTate");
        outState.putSerializable("class", details);

        super.onSaveInstanceState(outState);
    }

    /**
     * Launches a FindTeacherFragment, passing it the teacher's name from a specific cancelled
     * class.
     *
     * @param view  The current View.
     */
    public void loadTeacherInfo (View view) {
        //if teacher is null or empty, display message to user
        if (details.getTeacher() == null || details.getTeacher().equals("")) {
            Toast.makeText(view.getContext(),
                    getResources().getString(R.string.empty_teacher_error),
                    Toast.LENGTH_LONG).show();
        } else { //else, teacher presumably contains valid data for a search
            FindTeacherFragment teacherFragment = new FindTeacherFragment();
            //name must be split between first and last name
            String[] names = details.getTeacher().split("\\s");
            Log.d(TAG, "first name: " + names[0]);
            String lname = "";
            //some teachers have multiple words for their last name
            //retrieve all remaining names after first name, place them in one string
            for (int i = 1; i < names.length; i++) {
                lname = lname + names[i] + " ";
            }
            //trim any whitespace at end as needed
            lname = lname.trim();
            Log.d(TAG, "last name: " + lname);
            //place names in Bundle
            Bundle args = new Bundle();
            args.putString("fname", names[0]);
            args.putString("lname", lname);
            teacherFragment.setArguments(args);
            //launch new fragment via FragmentManager
            FragmentManager fm = getActivity().getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.side_frame, teacherFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }
}
