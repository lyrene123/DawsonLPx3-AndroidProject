package com.dawsonlpx3.teacher_activity;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dawsonlpx3.R;
import com.dawsonlpx3.data.TeacherDetails;

import java.util.List;

public class TeacherContactFragment extends Fragment {
    private TeacherDetails teacher;
    private TextView fnameTV, lnameTV, emailTV, localTV, positionTV, departmentTV, sectorTV, officeTV;

    private final String TAG = "Dawson-TeacherContact";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate started");
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            // Get back arguments
            if(getArguments() != null) {
                teacher = (TeacherDetails) getArguments().getSerializable("teacher");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_teacher_contact, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.fnameTV = (TextView) view.findViewById(R.id.firstnameTextView);
        this.lnameTV = (TextView) view.findViewById(R.id.lastnameTextView);
        this.emailTV = (TextView) view.findViewById(R.id.emailTextView);
        this.localTV = (TextView) view.findViewById(R.id.localTextView);
        this.positionTV = (TextView) view.findViewById(R.id.positionTextView);
        this.departmentTV = (TextView) view.findViewById(R.id.departmentTextView);
        this.sectorTV = (TextView) view.findViewById(R.id.sectorTextView);
        this.officeTV = (TextView) view.findViewById(R.id.officeTextView);

        displayTeacherDetails();
    }

    private void displayTeacherDetails(){
        Log.d(TAG, "displayTeacherDetails started");
        this.fnameTV.setText(this.teacher.getFirst_name());
        this.lnameTV.setText(this.teacher.getLast_name());
        this.emailTV.setText(this.teacher.getEmail());
        this.localTV.setText(this.teacher.getLocal());
        this.officeTV.setText(this.teacher.getOffice());

        List<Object> positions = this.teacher.getPositions();
        for(int i = 0; i < positions.size(); i++){
            this.positionTV.setText(this.positionTV.getText() + "\n" + positions.get(i));
        }

        List<Object> departments = this.teacher.getDepartments();
        for(int i = 0; i < departments.size(); i++){
            this.departmentTV.setText(this.departmentTV.getText() + "\n" + departments.get(i));
        }

        List<Object> sectors = this.teacher.getSectors();
        for(int i = 0; i < sectors.size(); i++){
            this.sectorTV.setText(this.sectorTV.getText() + "\n" + sectors.get(i));
        }
    }
}
