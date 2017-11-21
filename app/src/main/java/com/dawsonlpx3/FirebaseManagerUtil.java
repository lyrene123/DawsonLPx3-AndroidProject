package com.dawsonlpx3;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by laborlyrene on 2017-11-20.
 */

public class FirebaseManagerUtil {

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




}
