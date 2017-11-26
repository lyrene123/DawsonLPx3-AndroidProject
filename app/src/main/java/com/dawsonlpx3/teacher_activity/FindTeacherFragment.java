package com.dawsonlpx3.teacher_activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dawsonlpx3.R;
import com.dawsonlpx3.data.TeacherDetails;
import com.dawsonlpx3.db.FirebaseManagerUtil;

import java.util.List;

public class FindTeacherFragment extends Fragment implements View.OnClickListener {

    private View view; //view containing the fragment
    private EditText fnameET, lnameET;
    private RadioButton likeRB, exactRB;
    private TextView errorMsgTV;
    private Button searchButton;

    private FirebaseManagerUtil fbManager;
    private  List<TeacherDetails> teachers;

    private String restoredFname, restoredLname, restoredErrorMsg, fullname, fname, lname;
    private boolean isSearched, isExact, isOnSavedInstanceState;

    private GetTeachersTask teachersTask;

    private final String TAG = "DawsonLPx3-FindTeacher";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        fbManager = FirebaseManagerUtil.getFirebaseManager();
        if(savedInstanceState != null){
            Log.d(TAG, "restoring state from bundle");
            this.restoredFname = savedInstanceState.getString("fname");
            this.restoredLname = savedInstanceState.getString("lname");
            this.restoredErrorMsg = savedInstanceState.getString("error");
        } else {
            this.restoredFname = "";
            this.restoredLname = "";
            this.restoredErrorMsg = "";
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        view = inflater.inflate(R.layout.activity_find_teacher, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume started");
        this.isOnSavedInstanceState = false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated started");
        fnameET = (EditText) view.findViewById(R.id.firstnameEditText);
        lnameET = (EditText) view.findViewById(R.id.lastnameEditText);
        likeRB = (RadioButton) view.findViewById(R.id.likeRadioBtn);
        exactRB = (RadioButton) view.findViewById(R.id.exactRadioBtn);
        errorMsgTV = (TextView) view.findViewById(R.id.errorMsgTV);
        searchButton = (Button) view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);

        fnameET.setText(this.restoredFname);
        lnameET.setText(this.restoredLname);
        errorMsgTV.setText(this.restoredErrorMsg);
    }

    public void searchTeacher(View view) {
        if(isValidInput()){
            isSearched = true;
            errorMsgTV.setText("");
            isExact = exactRB.isChecked();
            if(!fnameET.getText().toString().isEmpty() && !lnameET.getText().toString().isEmpty()){
                fullname = fnameET.getText().toString().trim() + " " + lnameET.getText().toString().trim();
                Log.d(TAG, "isExact: " + isExact + "  fullname: " + fullname);
            } else if(!fnameET.getText().toString().isEmpty() && lnameET.getText().toString().isEmpty()){
                fname = fnameET.getText().toString().trim();
                Log.d(TAG, "isExact: " + isExact + " fname: " + fname);
            } else {
                lname = lnameET.getText().toString().trim();
                Log.d(TAG, "isExact: " + isExact + " lname: " + lname);
            }
            //this.fbManager.retrieveRecordsFromDb(getActivity(), this, fullname, fname, lname, isExact);
            this.teachersTask = new GetTeachersTask();
            this.teachersTask.execute();
            Toast.makeText(getActivity(), getResources().getString(R.string.searchTeachers), Toast.LENGTH_SHORT).show();
        }
    }

    public void setTeachersList(List<TeacherDetails> teachersList){
        teachers = teachersList;
        isSearched = false;
        Log.d(TAG, "num of teacher records: " + teachers.size());
        Log.d(TAG, "task: " + teachersTask + " isSaved...: " + isOnSavedInstanceState);
        if(teachersTask != null && !this.isOnSavedInstanceState) {
            //display ChooseTeacher fragment
            if (teachers.size() > 1) {
                displayChooseTeacher();
            } else if (teachers.size() == 1) {
                displayTeacherDetail();
            } else {
                errorMsgTV.setText(R.string.noresult);
            }
        }
    }

    private void displayTeacherDetail(){
        Log.d(TAG, "displayTeacherDetail started");
        TeacherContactFragment teacherContactFragment = new TeacherContactFragment();
        Bundle args = new Bundle();
        args.putSerializable("teacher", teachers.get(0));
        teacherContactFragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, teacherContactFragment)
                .addToBackStack(null)
                .commit();
    }

    private void displayChooseTeacher(){
        Log.d(TAG, "displayChooseTeacher started");
        ChooseTeacherFragment chooseTeacherFragment = new ChooseTeacherFragment();
        chooseTeacherFragment.setTeachersList(teachers);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, chooseTeacherFragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                Log.d(TAG, "search button clicked started");
                if(!isSearched) {
                    searchTeacher(v);
                }
                break;
        }
    }

    private boolean isValidInput(){
        if((fnameET.getText() == null || fnameET.getText().toString().isEmpty())
                && (lnameET.getText() == null || lnameET.getText().toString().isEmpty())){
            errorMsgTV.setText(R.string.invalidTeacherSearch);
            this.isSearched = false;
            return false;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState started");
        this.isOnSavedInstanceState = true;
        if (this.fnameET != null && !this.fnameET.getText().toString().isEmpty()){
            outState.putString("fname", this.fnameET.getText().toString());
        }
        if(lnameET != null && !this.lnameET.getText().toString().isEmpty()){
            outState.putString("lname", this.lnameET.getText().toString());
        }
        if(errorMsgTV != null && !this.errorMsgTV.getText().toString().isEmpty()){
            outState.putString("error", this.errorMsgTV.getText().toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(teachersTask != null) {
            Log.d(TAG, "canceling teachers task");
            this.teachersTask.cancel(true);
            this.teachersTask = null;
        }
    }

    private class GetTeachersTask extends AsyncTask<Void, Void, List<TeacherDetails>> {

        @Override
        protected List<TeacherDetails> doInBackground(Void... voids) {
            Log.d(TAG, "Starting GetTeachersTask async task");
            fbManager.retrieveRecordsFromDb(getActivity(), FindTeacherFragment.this, fullname, fname, lname, isExact);
            return null;
        }
    }
}
