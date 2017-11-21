package com.dawsonlpx3;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

    public void retrieveRecordsFromDb(final Activity activity, final ListView list,
                                      final String dataRequested, boolean isExactSearch){
        //sign in into firebase to data records from database
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            switch (dataRequested){
                                //if category names are the data you want to data
                                case "category" :
                                   // loadCategoriesFromDb(list, activity);
                                    break;
                                //if the list of category short quotes are the data to data
                                case "quote_short" :
                                   // loadCategoryShortQuoteFromDb(list, activity, categoryID, categoryTitle);
                                    break;
                                //if a quote and its related info are the data to data
                                case "quote_item" :
                                   // loadQuoteItemFromDb(activity, categoryID, quoteID);
                                    break;
                                //if request to authenticate to the database before retrieving image from storage
                                case "cat_img" :
                                   // ((QuoteActivity) activity).loadImageIntoImageView();
                                    break;
                            }
                        } else {
                            //display en error dialog box if authentication failed
                           // displayErrorAuthentication(task, activity);
                        }
                    }
                });

    }



}
