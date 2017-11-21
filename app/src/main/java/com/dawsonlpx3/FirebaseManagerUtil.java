package com.dawsonlpx3;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

/**
 * ToDo once class is more or less done
 */
public class FirebaseManagerUtil {

    private List<String> teacherNameList;
    private ArrayAdapter<String> adapter;

    //credentials for database authentication
    private final String email = "dawsonlpx3@gmail.com";
    private final String password = "dawsonlpx3";

    //for database access and authentication
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;

    private static String TAG = "DawsonLPx3-FireBaseManager";

    /**
     * Initializes the DatabaseReference object for retrieval of data
     * and the FirebaseAuth for database authentication
     */
    public FirebaseManagerUtil(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void retrieveRecordsFromDb(final Activity activity, final ListView list,
                                      final String dataRequested, final String fullname,
                                      final boolean isExactSearch){
        //sign in into firebase to data records from database
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            switch (dataRequested){
                                //if teacher names are the data you want, call retrieveTeacherNames method
                                case "teacher" :
                                    retrieveTeacherNames(list, activity, fullname, isExactSearch);
                                    break;
                                //if the list of category short quotes are the data to data
                                case "teacher_detail" :
                                   // loadCategoryShortQuoteFromDb(list, activity, categoryID, categoryTitle);
                                    break;
                            }
                        } else {
                            //display en error dialog box if authentication failed
                           // displayErrorAuthentication(task, activity);
                        }
                    }
                });

    }

    private void retrieveTeacherNames(ListView list, Activity activity, final String fullname, final boolean isExactSearch){

        //initialize the list of teacher fullnames
        teacherNameList = new ArrayList<>();

        //create the ValueEventListener listener object
        ValueEventListener listener = new ValueEventListener() {
            /**
             * Retrieves any changes made to the database. In our case
             * this method will be called once at the beginning when
             * retrieving the initial data. Retrieves all the teacher fullnames
             * and notifies the change to the adapter that matches the input fullname
             * depending if the user wants an exact search or not.
             *
             * @param dataSnapshot DataSnapshot object data from db
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){

                    String name = (String)item.child("full_name").getValue();

                    //if user wants an exact search, then only add the fullnames that exactly match the input
                    if(isExactSearch){
                        if(name.equals(fullname)){
                            //add the retrieved teacher names into the list
                            addTeacherNameToList(fullname);
                        }
                    } else {
                        //if the user wants a approx. search, do the following
                        if(name.equalsIgnoreCase(fullname) || name.toLowerCase().contains(fullname.toLowerCase())){
                            //add the retrieved teacher names into the list
                            addTeacherNameToList(fullname);
                        }
                    }

                    adapter.notifyDataSetChanged();
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

        //create the adapter and set it to the ListView to inflate the items
        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, teacherNameList.toArray(new String[0]));
        list.setAdapter(adapter);
    }

    private void addTeacherNameToList(String teacherName){
        this.teacherNameList.add(teacherName);
    }

}
