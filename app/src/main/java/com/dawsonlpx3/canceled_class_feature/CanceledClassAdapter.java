package com.dawsonlpx3.canceled_class_feature;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dawsonlpx3.R;
import com.dawsonlpx3.data.CanceledClassDetails;
import com.dawsonlpx3.friends_in_course_feature.FriendInCourseFragment;

import java.util.List;

/**
 * The CanceledClassAdapter class is an ArrayAdapter designed to to connect a ListView with a list
 * of CanceledClassDetails objects generated from the Dawson College RSS feed for cancelled
 * classes. The adapter creates Views for each individual CanceledClassDetails object, and creates
 * event handlers for them as needed.
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 * @author Phil Langlois
 * @author Pengkim Sy
 */

public class CanceledClassAdapter extends ArrayAdapter<CanceledClassDetails> {

    private LayoutInflater inflater;
    private Activity activity;
    private List<CanceledClassDetails> classList;
    private final String TAG = "LPx3-CanceledAdapter";

    /**
     * Two-parameter constructor for the adapter.
     *
     * @param context   The current application Context.
     * @param classes   The list of CanceledClassDetails objects.
     */
    public CanceledClassAdapter(Context context, List<CanceledClassDetails> classes) {
        super(context, 0, classes);
        Log.d(TAG, "constructor");
        this.activity = (Activity) context;
        this.classList = classes;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Generates the View for a specific row in the ListView.
     *
     * @param position  The View's current position in the list.
     * @param convertView   The View object to be converted into a row in the ListView. Can be null.
     * @param parent    The parent ViewGroup containing the rows.
     * @return  The View object representing a row in the ListView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView");
        //a specific row in the ListView is related to a specific item in the list
        CanceledClassDetails c = getItem(position);

        if (convertView == null) {
            Log.d(TAG, "null convertView - inflating from resources");
            convertView = inflater.inflate(R.layout.canceled_custom_item, null);
        }

        //get handle to TextViews in a list row
        TextView tvDate = (TextView) convertView.findViewById(R.id.cancelledDateTV);
        TextView tvCourse = (TextView) convertView.findViewById(R.id.cancelledCourseTV);
        //fill TextViews with data from CanceledClassDetails
        tvDate.setText(c.getDateCancelled());
        tvCourse.setText(c.getCourse());
        //attach event listeners
        setRowClickListener(convertView, position);
        Log.d(TAG, "view for row generated");
        return convertView;
    }

    /**
     * Sets an onClickListener for a specific row in the ListView.
     *
     * @param row   The row in the ListView to set an onClickListener for.
     * @param position  The position in the List of CanceledClassDetails objects related to the row.
     */
    private void setRowClickListener(View row, final int position) {
        Log.d(TAG, "setRowClickListener");
        row.setOnClickListener(new View.OnClickListener() {
            /**
             * When clicked, launches a ShowCancelFragment and displays the information for the
             * relevant CanceledClassDetails object.
             *
             * @param v The View object that was clicked.
             */
            @Override
            public void onClick(View v) {
                Log.d(TAG, "row clicked: " + position);
                Log.d(TAG, "related bean: " + classList.get(position).getCourse());
                ShowCancelFragment showCancelFragment = new ShowCancelFragment();
                //serialize the bean, add to the bundle
                Bundle args = new Bundle();
                args.putSerializable("class", classList.get(position));
                showCancelFragment.setArguments(args);
                //begin fragment transaction to launch new fragment
                FragmentManager fm = activity.getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.side_frame, showCancelFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "row long clicked: " + position);
                Log.d(TAG, "related bean: " + classList.get(position).getCourse());
                FriendInCourseFragment friendFragment = new FriendInCourseFragment();

                Bundle args = new Bundle();
                args.putSerializable("class", classList.get(position));
                friendFragment.setArguments(args);

                FragmentManager fm = activity.getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.side_frame, friendFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });
    }
}
