package com.dawsonlpx3;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dawsonlpx3.data.CanceledClassDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1132371 on 11/29/2017.
 */

public class CanceledClassAdapter extends ArrayAdapter<CanceledClassDetails> {

    private LayoutInflater inflater;
    private Activity activity;
    private List<CanceledClassDetails> classList;
    private final String TAG = "LPx3-CanceledAdapter";

    public CanceledClassAdapter(Context context, List<CanceledClassDetails> classes) {
        super(context, 0, classes);
        Log.d(TAG, "constructor");
        this.activity = (Activity) context;
        this.classList = classes;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView");
        CanceledClassDetails c = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.canceled_custom_item, null);
        }

        TextView tvDate = (TextView) convertView.findViewById(R.id.cancelledDateTV);
        TextView tvCourse = (TextView) convertView.findViewById(R.id.cancelledCourseTV);

        tvDate.setText(c.getDateCancelled());
        tvCourse.setText(c.getCourse());

        setRowClickListener(convertView, position);

        return convertView;
    }

    private void setRowClickListener(View row, final int position) {
        Log.d(TAG, "setRowClickListener");
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "row clicked: " + position);
                Log.d(TAG, "related bean: " + classList.get(position).getCourse());
                ShowCancelActivity showCancelFragment = new ShowCancelActivity();

                Bundle args = new Bundle();
                args.putSerializable("class", classList.get(position));
                showCancelFragment.setArguments(args);

                FragmentManager fm = activity.getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.side_frame, showCancelFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
