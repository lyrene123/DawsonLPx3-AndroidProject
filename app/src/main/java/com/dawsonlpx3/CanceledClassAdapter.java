package com.dawsonlpx3;

import android.content.Context;
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

    public CanceledClassAdapter(Context context, List<CanceledClassDetails> classes) {
        super(context, 0, classes);
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

        return convertView;
    }
}
