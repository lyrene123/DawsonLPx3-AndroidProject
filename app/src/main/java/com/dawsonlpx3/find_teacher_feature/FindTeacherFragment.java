package com.dawsonlpx3.find_teacher_feature;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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

import com.dawsonlpx3.R;
import com.dawsonlpx3.data.TeacherDetails;
import com.dawsonlpx3.db.FirebaseManagerUtil;

import java.util.List;

/**
 * Displays the search form for the user to enter a first name and last name to search
 * teachers. Handles the validation of the user input. Handles the call to the
 * firebase manager util instance to query the database to retrieve the records of teachers
 * that matches the search criteria of the user. Manages the display of the
 * search result which can be a list of teachers or a teacher by inflating the appropriate
 * fragment (ChooseTeacherFragment or TeacherContactFragment).
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefleur
 */
public class FindTeacherFragment extends Fragment implements View.OnClickListener {

    private View view; //view containing the fragment

    //widgets taken from the view
    private EditText fnameET, lnameET;
    private RadioButton likeRB, exactRB;
    private TextView errorMsgTV;
    private Button searchButton;

    private FirebaseManagerUtil fbManager;
    private  List<TeacherDetails> teachers;

    private String restoredFname, restoredLname, restoredErrorMsg, fullname, fname, lname;
    private boolean isSearched, isExact, isOnSavedInstanceState;

    private GetTeachersTask teachersTask;
    private ProgressDialog dialog;

    private final String TAG = "DawsonLPx3-FindTeacher";

    /**
     * Initializes the FirebaseManagerUtil object to be used when retrieving records from the
     * firebase database. If there are existing items in the Bundle, restore those items which are
     * the input strings inside the EditText of the view's search form. If none existing in the Bundle,
     * then initialize the properties to empty strings.
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getActivity());
        Log.d(TAG, "onCreate started");
        fbManager = FirebaseManagerUtil.getFirebaseManager();
        if(savedInstanceState != null){
            Log.d(TAG, "restoring state from bundle");
            this.restoredFname = savedInstanceState.getString("fname");
            this.restoredLname = savedInstanceState.getString("lname");
            this.restoredErrorMsg = savedInstanceState.getString("error");
            this.isExact = savedInstanceState.getBoolean("isExact");
        } else {
            if (getArguments() != null) {
                this.restoredFname = getArguments().getString("fname");
                this.restoredLname = getArguments().getString("lname");
            } else {
                this.restoredFname = "";
                this.restoredLname = "";
            }
            this.restoredErrorMsg = "";
        }
    }

    /**
     * Inflates the layout into the fragment and returns the view containing the fragment.
     *
     * @param inflater LayoutInflater object
     * @param container ViewGroup object
     * @param savedInstanceState Bundle object
     * @return View containing the fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        view = inflater.inflate(R.layout.activity_find_teacher, container, false);
        return view;
    }

    /**
     * Sets the isOnSavedInstanceState boolean back to false once the app is on state Resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume started");
        this.isOnSavedInstanceState = false;
    }

    /**
     * Retrieves the necessary widgets from the view and sets the restored values
     * in the first name, last name EditText if there are any restored values from the
     * Bundle. If none, then the EditTexts will be empty.
     *
     * @param view View of fragment
     * @param savedInstanceState Bundle object
     */
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
        exactRB.setChecked(isExact);
    }

    /**
     * Searches for records of teachers matching the search criteria of the user through the
     * FirebaseManager util class. Validates the input of the user first and if valid,
     * sets the isSearched boolean to true, empties the error message TextView, and retrieves
     * the input of the user (first name and/or last name). Starts a seperate async task for
     * querying the firebase database.
     */
    public void searchTeacher() {
        if(isValidInput()){
            isSearched = true;
            errorMsgTV.setText("");
            isExact = exactRB.isChecked(); //retrieve user's choice of exact/like search

            //retrieve the input of the user for first and/or last name.
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

            //start seperate async task for querying the db
            this.teachersTask = new GetTeachersTask();
            this.teachersTask.execute();
            //Toast.makeText(getActivity(), getResources().getString(R.string.searchTeachers), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sets the teachers list containing the result of the user's search. List contains TeacherDetails
     * objects which will then be used to display as a list in ChooseTeacherFragment if there are
     * more than one teacher in the list. If only one teacher in the result list, then all details
     * of that one teacher will be displayed in TeacherContactFragment.
     *
     * This method is called by the FirebaseManagerUtil after all records have been restored
     *
     * @param teachersList List of TeacherDetails objects
     */
    public void setTeachersList(List<TeacherDetails> teachersList){
        teachers = teachersList;
        isSearched = false;
        Log.d(TAG, "num of teacher records: " + teachers.size());
        Log.d(TAG, "task: " + teachersTask + " isSaved...: " + isOnSavedInstanceState);

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        //inflate the fragments only if the async task is not cancelled and OnSavedInstanceState is not called
        if(teachersTask != null && !this.isOnSavedInstanceState) {
            if (teachers.size() > 1) {
                displayChooseTeacher();
            } else if (teachers.size() == 1) {
                displayTeacherDetail();
            } else {
                errorMsgTV.setText(R.string.noresult);
            }
        }
    }

    /**
     * Inflates the TeacherDetailFragment with a single TeacherDetails object in order to display
     * all information of a teacher. Passes the TeacherDetails object to display in a Bundle and
     * that bundle is passed as arguments to the TeacherDetailFragment fragment.
     */
    private void displayTeacherDetail(){
        Log.d(TAG, "displayTeacherDetail started");
        TeacherContactFragment teacherContactFragment = new TeacherContactFragment();

        //pass the TeacherDetails object to display as an argument to the fragment
        Bundle args = new Bundle();
        args.putSerializable("teacher", teachers.get(0));
        teacherContactFragment.setArguments(args);

        //inflate the fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.side_frame, teacherContactFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Inflates the ChooseTeacherFragment which will display the list of teachers resulting
     * from the search to the database. Sets the list of teachers to display through the
     * setTeachersList method of the ChooseTeacherFragment class.
     */
    private void displayChooseTeacher(){
        Log.d(TAG, "displayChooseTeacher started");
        ChooseTeacherFragment chooseTeacherFragment = new ChooseTeacherFragment();
        chooseTeacherFragment.setTeachersList(teachers); //pass the list to display
        FragmentManager fragmentManager = getFragmentManager(); //inflate fragment
        fragmentManager.beginTransaction()
                .replace(R.id.side_frame, chooseTeacherFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Handles the onclick of the current fragment. Verifies the id of the view that was
     * clicked and calls the appropriate method handler.
     *
     * @param v View that was clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                Log.d(TAG, "search button clicked started");
                //if search button was clicked and not currently already querying the database
                if(!isSearched) {
                    searchTeacher();
                }
                break;
        }
    }

    /**
     * Verifies if the user input has at least entered a first name or a last name
     * in the input of the search form
     *
     * @return boolean if user input is valid
     */
    private boolean isValidInput(){
        if((fnameET.getText() == null || fnameET.getText().toString().isEmpty())
                && (lnameET.getText() == null || lnameET.getText().toString().isEmpty())){
            errorMsgTV.setText(R.string.invalidTeacherSearch);
            this.isSearched = false;
            return false;
        }
        return true;
    }

    /**
     * Saves the user input to the Bundle state if there are any input and the radio button state.
     *
     * @param outState Bundle object
     */
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

        outState.putBoolean("isExact", this.isExact);
    }

    /**
     * If app is on DestroyView state, then stop the async task for searching Teacher and
     * set the task to null only if task is not null which means that there is an existing task.
     * Dismiss the progress dialog if it's still showing and set it to null.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(teachersTask != null) {
            Log.d(TAG, "canceling teachers task");
            this.teachersTask.cancel(true);
            this.teachersTask = null;
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        dialog = null;
    }

    /**
     * Task class extending AsyncTask used to query the firebase database for teacher records
     * matching the search criteria of the user.
     */
    private class GetTeachersTask extends AsyncTask<Void, Void, List<TeacherDetails>> {

        /**
         * Before the execution of the async task, show the progress bar.
         */
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.searching_teachers));
            dialog.show();
        }

        /**
         * Retrieve records from the firebase database matching the search criteria of the user
         * in an async task running in the background.
         *
         * @param voids
         * @return null in this case since the setTeachersList method will be invoked by the firebase
         *          util instance once all records have been retrieved from database.
         */
        @Override
        protected List<TeacherDetails> doInBackground(Void... voids) {
            Log.d(TAG, "Starting GetTeachersTask async task");
            fbManager.retrieveRecordsFromDb(getActivity(), FindTeacherFragment.this, fullname, fname, lname, isExact);
            return null;
        }
    }
}
