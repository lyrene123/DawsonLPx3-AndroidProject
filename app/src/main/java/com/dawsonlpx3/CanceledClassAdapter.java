package com.dawsonlpx3;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
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

    public CanceledClassAdapter(Context context, List<CanceledClassDetails> classes) {
        super(context, 0, classes);
        this.activity = (Activity) context;
        this.classList = classes;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ShowCancelActivity();
                Bundle bundle = new Bundle();
                bundle.putSerializable("class", classList.get(position));
                FragmentManager fm = activity.getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.side_frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
