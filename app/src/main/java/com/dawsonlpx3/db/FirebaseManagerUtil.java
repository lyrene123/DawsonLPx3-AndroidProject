package com.dawsonlpx3.db;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.dawsonlpx3.find_teacher_feature.FindTeacherFragment;
import com.dawsonlpx3.R;
import com.dawsonlpx3.data.TeacherDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper utility class that handles database access operations and authentication
 * for the firebase database. This DAO class only handles read operations against the database and user
 * authentication before any data retrieval from database. Handles the retrieval of list of teacher
 * records that exactly or approximately matches a full name input string depending on a boolean
 * isExactSearch.
 *
 * @author Lyrene Labor
 * @author Peter Bellefleur
 * @author Phil Langlois
 * @author Pengkim Sy
 */
public class FirebaseManagerUtil {

    //list that will hold the list of teachers from the db search
    private List<TeacherDetails> teacherList;

    //credentials for database authentication
    private final String email = "dawsonlpx3@gmail.com";
    private final String password = "dawsonlpx3";

    //for database access and authentication
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;

    private static FirebaseManagerUtil fbManager;


    private static String TAG = "DawsonLPx3-FireBaseManager";

    /**
     * Initializes the DatabaseReference object for retrieval of data
     * and the FirebaseAuth for database authentication
     */
    private FirebaseManagerUtil(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Factory method that will instantiate a FirebaseManagerUtil instance if does not exists yet
     * and returns it. This method assures that only one instance of FirebaseManagerUtil exists
     * for the duration of the activity lifetime.
     *
     * @return FirebaseManagerUtil instance
     */
    public static FirebaseManagerUtil getFirebaseManager(){
        if(fbManager == null){
            fbManager = new FirebaseManagerUtil();
        }
        return fbManager;
    }

    /**
     * Retrieves the teachers records from the firebase that matches the input fullname.
     * Handles database authentication first before retrieving any database records.
     * If user authentication failed, a dialog will display with an error message letting the user know
     * that an error has occurred.
     *
     * @param activity Activity that needs access to the database
     * @param fullname Full name of the teacher we want to retrieve from firebase
     * @param isExactSearch True or false whether to search for exact full name or approximate
     */
    public void retrieveRecordsFromDb(final Activity activity, final Fragment fragment, final String fullname,
                                      final String fname, final String lname,
                                      final boolean isExactSearch){

        //sign in into firebase to retrieve records from database
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Authentication successful, retrieving records...");
                            //only if authentication is successfull, retrieve teachers records
                            retrieveTeachersList(fragment, fullname, fname, lname, isExactSearch);
                        } else {
                            Log.w(TAG, "Authentication problem!: " + task.getException().getMessage());
                            //display en error dialog box if authentication failed
                            displayErrorAuthentication(task, activity);
                        }
                    }
                });

    }


    /**
     * Creates and displays an alert dialog that will display an error message if
     * the authentication sign in to the firebase has failed.
     *
     * @param task Authentication task
     * @param activity The activity trying to access the database
     */
    private void displayErrorAuthentication(Task<AuthResult> task, Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(task.getException().getMessage())
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Retrieves the teacher records from the database that matches the input full name. Search
     * for exact full name or approximate search depending on the isExactSearch boolean. Loads
     * each matching teacher record into a list of TeacherDetails object. Once all records from
     * firebase was iterated through, return the list to the calling activity by calling a method
     * from the activity passing it the list.
     *
     * @param fullname
     * @param isExactSearch
     */
    private void retrieveTeachersList(final Fragment fragment, final String fullname, final String fname,
                                      final String lname, final boolean isExactSearch){

        //initialize the list of teacher fullnames
        teacherList = new ArrayList<>();

        //create the ValueEventListener listener object
        ValueEventListener listener = new ValueEventListener() {
            /**
             * Retrieves any changes made to the database. In our case
             * this method will be called once at the beginning when
             * retrieving the initial data. Retrieves all the teacher fullnames,
             * first and last names.
             *
             * @param dataSnapshot DataSnapshot object data from db
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){

                    String full = (String)item.child("full_name").getValue();
                    String firstname = (String)item.child("first_name").getValue();
                    String lastname = (String)item.child("last_name").getValue();

                    //if user wants an exact search, then only add the fullnames, first name or last name that exactly match the input
                    if(isExactSearch){
                        //find matches depending if the user inputed a fullname, a first name only, or a last name only
                        if(fullname != null){
                            if(full.equals(fullname)){
                                addTeacherToList(item.getValue(TeacherDetails.class));
                            }
                        } else if (fname != null && lname == null){
                            if(firstname.equals(fname)){
                                addTeacherToList(item.getValue(TeacherDetails.class));
                            }
                        } else {
                            if(lastname.equals(lname)){
                                addTeacherToList(item.getValue(TeacherDetails.class));
                            }
                        }
                    } else {
                        String pattern = "";
                        Pattern r = null;
                        Matcher m = null;

                        //with the user of regex, find records beginning with the input fullname, firstname or lastname
                        if(fullname != null){
                            pattern = "^"+fullname.toLowerCase();
                            r = Pattern.compile(pattern);
                            m = r.matcher(full.toLowerCase());
                        } else if (fname != null && lname == null){
                            pattern = "^"+fname.toLowerCase();
                            r = Pattern.compile(pattern);
                            m = r.matcher(firstname.toLowerCase());
                        } else {
                            pattern = "^"+lname.toLowerCase();
                            r = Pattern.compile(pattern);
                            m = r.matcher(lastname.toLowerCase());
                        }

                        if(m.find()){
                            addTeacherToList(item.getValue(TeacherDetails.class));
                        }
                    }
                }
                if(teacherList != null && teacherList.size() > 0){
                    ((FindTeacherFragment) fragment).setTeachersList(teacherList);
                }

            }

            /**
             * onCancelled called when an error has occurred and the data cannot be retrieved
             *
             * @param databaseError DatabaseError containing the error that occured
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        };

        //set the listener
        mDatabase.addValueEventListener(listener);
    }


    /**
     * Helper method to add a TeacherDetails object into the teachers list.
     *
     * @param teacher TeacherDetails object to add into the list
     */
    private void addTeacherToList(TeacherDetails teacher){
        Log.d(TAG, "Added teacher: " + teacher.getFull_name());
        this.teacherList.add(teacher);
    }
}
