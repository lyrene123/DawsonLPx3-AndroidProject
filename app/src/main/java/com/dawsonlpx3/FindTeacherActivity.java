package com.dawsonlpx3;

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

import com.dawsonlpx3.db.FirebaseManagerUtil;

public class FindTeacherActivity extends Fragment implements View.OnClickListener {

    private View view; //view containing the fragment
    private EditText fnameET, lnameET;
    private RadioButton likeRB, exactRB;
    private TextView errorMsgTV;
    private Button searchButton;

    private FirebaseManagerUtil fbManager;

    private final String TAG = "DawsonLPx3-FindTeacher";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbManager = FirebaseManagerUtil.getFirebaseManager();

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
            String fullname = (fnameET.getText().toString() + " " + lnameET.getText().toString()).trim();
            Log.d(TAG, "isExact: " + isExact + "  fullname: " + fullname);
            this.fbManager.retrieveRecordsFromDb(getActivity(), fullname, isExact);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                Log.d(TAG, "search button clicked started");
                searchTeacher(v);
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
