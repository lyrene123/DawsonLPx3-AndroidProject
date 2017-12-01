package com.dawsonlpx3;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dawsonlpx3.data.CanceledClassDetails;
import com.dawsonlpx3.db.FirebaseManagerUtil;
import com.dawsonlpx3.find_teacher_feature.FindTeacherFragment;

public class ShowCancelActivity extends Fragment {

    private View view;
    private CanceledClassDetails details;
    private TextView titleText;
    private TextView courseText;
    private TextView timeText;
    private TextView teacherText;
    private final String TAG = "LPx3-ShowCancel";

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
        teacherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTeacherInfo(v);
            }
        });
    }

    public void loadTeacherInfo (View view) {
        //FirebaseManagerUtil fb = FirebaseManagerUtil.getFirebaseManager();
        //FindTeacherFragment teacherFragment = new FindTeacherFragment();
        //fb.retrieveRecordsFromDb(this.getActivity(), teacherFragment, details.getTeacher(),
         //       null, null, true);
        FindTeacherFragment teacherFragment = new FindTeacherFragment();

        String[] names = details.getTeacher().split("\\s");
        String lname = "";
        for (int i = 1; i < names.length; i++) {
            lname = lname + names[i] + " ";
        }
        lname.trim();
        Log.d(TAG, "last name: " + lname);

        Bundle args = new Bundle();
        args.putString("fname", names[0]);
        args.putString("lname", lname);
        teacherFragment.setArguments(args);

        FragmentManager fm = getActivity().getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.side_frame, teacherFragment)
                .addToBackStack(null)
                .commit();

    }
}
