package com.dawsonlpx3.teacher_activity;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
    private static boolean isSearched;
    private static List<TeacherDetails> teachers;

    private final String TAG = "DawsonLPx3-FindTeacher";
    private final static String STATIC_TAG = "DawsonLPx3-FindTeacher";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbManager = FirebaseManagerUtil.getFirebaseManager();
        isSearched = false;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        view = inflater.inflate(R.layout.activity_find_teacher, container, false);
        return view;
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
    }

    public void searchTeacher(View view) {
        if(isValidInput()){
            boolean isExact = exactRB.isChecked();
            String fullname = null, fname = null, lname = null;
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
            this.fbManager.retrieveRecordsFromDb(getActivity(), fullname, fname, lname, isExact);
        }
    }

    public static void setTeachersList(List<TeacherDetails> teachersList){
        teachers = teachersList;
        isSearched = false;
        Log.d(STATIC_TAG, "num of teacher records: " + teachers.size());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                Log.d(TAG, "search button clicked started");
                if(!isSearched) {
                    searchTeacher(v);
                }
                isSearched = true;
                break;
        }
    }

    private boolean isValidInput(){
        if((fnameET.getText() == null || fnameET.getText().toString().isEmpty())
                && (lnameET.getText() == null || lnameET.getText().toString().isEmpty())){
            errorMsgTV.setText(R.string.invalidTeacherSearch);
            return false;
        }
        return true;
    }


}
