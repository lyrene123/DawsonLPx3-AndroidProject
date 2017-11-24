package com.dawsonlpx3.db;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.dawsonlpx3.R;
import com.dawsonlpx3.data.TeacherDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    public void retrieveRecordsFromDb(final Activity activity, final String fullname,
                                      final boolean isExactSearch){
        //sign in into firebase to retrieve records from database
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Authentication successful, retrieving records...");
                            //only if authentication is successfull, retrieve teachers records
                            retrieveTeachersList(activity, fullname, isExactSearch);
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
     * @param activity
     * @param fullname
     * @param isExactSearch
     */
    private void retrieveTeachersList(Activity activity, final String fullname, final boolean isExactSearch){
        Log.d(TAG, "retrieveTeachersList started");

        //initialize the list of teacher fullnames
        teacherList = new ArrayList<>();

        Query queryRef;
        if(isExactSearch){
            queryRef = mDatabase.orderByChild("full_name").equalTo(fullname);
        } else {
            queryRef = mDatabase.orderByChild("full_name").startAt(fullname).endAt(fullname+"\uf8ff");
        }

        //create the ValueEventListener listener object
        ChildEventListener listener = new ChildEventListener() {
            /**
             * Retrieves any changes made to the database. In our case
             * this method will be called once at the beginning when
             * retrieving the initial data. Retrieves all the teacher fullnames
             * and notifies the change to the adapter that matches the input fullname
             * depending if the user wants an exact search or not.
             *
             */
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
               /* for(DataSnapshot item : snapshot.getChildren()){

                    String name = (String)item.child("full_name").getValue();

                    //if user wants an exact search, then only add the fullnames that exactly match the input
                    if(isExactSearch){
                        if(name.equals(fullname)){
                            Log.d(TAG, "Adding a teacher to the list");
                            //add the retrieved teacher into the list
                            addTeacherToList(item.getValue(TeacherDetails.class));
                        }
                    } else {
                        //if the user wants a approx. search, do the following
                        if(name.equalsIgnoreCase(fullname) || name.toLowerCase().contains(fullname.toLowerCase())){
                            Log.d(TAG, "Adding a teacher to the list");
                            //add the retrieved teacher names into the list
                            addTeacherToList(item.getValue(TeacherDetails.class));
                        }
                    }
                }*/
                Log.d(TAG, "Adding a teacher to the list");
                addTeacherToList(snapshot.getValue(TeacherDetails.class));
                //NOTE: AFTER THIS FOR LOOP IMPLEMENT THE LINE THAT WOULD SEND BACK THE LIST OF TEACHERS OBJECT BACK TO CALLING ACTIVITY
                /* ex: if(teacherList != null){
                        ((FindTeacherActivity) activity).displayTeacherSearchResult(teacherList);
                        //where displayTeacherSearchResult is a method in FindTeacherActivity
                       }
                */
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
        queryRef.addChildEventListener(listener);
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
