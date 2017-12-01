package com.dawsonlpx3;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dawsonlpx3.data.CanceledClassDetails;

public class ShowCancelActivity extends Fragment {

    private View view;
    private CanceledClassDetails details;
    private TextView titleText;
    private TextView courseText;
    private TextView timeText;
    private TextView teacherText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.titleText = (TextView) view.findViewById(R.id.titleTextView);
        titleText.setText(details.getTitle());
        this.courseText = (TextView) view.findViewById(R.id.courseTextView);
        courseText.setText(details.getCourse());
        this.timeText = (TextView) view.findViewById(R.id.timeTextView);
        timeText.setText(details.getDateCancelled());
        this.teacherText = (TextView) view.findViewById(R.id.teacherTextView);
        teacherText.setText(details.getTeacher());
    }
}
