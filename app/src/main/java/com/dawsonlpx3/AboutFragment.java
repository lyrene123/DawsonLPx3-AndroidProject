package com.dawsonlpx3;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Fragment activity that handles the display of the about activity for the user and the interaction
 * with the user. Handles the onclick events of the images of the team members and the click event
 * of the course id. When a team member is clicked on, then their respective blurb will be displayed
 * in a dialog. If the course if is clicked, then the CompSci dawson web page will display.
 *
 * @author Lyrene Labor
 * @autor Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefgleur
 */
public class AboutFragment extends Fragment implements View.OnClickListener{

    private final String TAG = "Dawson-AboutFragment";
    private ImageView lyreneImg, pengImg, philImg, peterImg;
    private TextView courseId;


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
        return inflater.inflate(R.layout.activity_about, container, false);
    }

    /**
     * Retrieves the necessary widgets from the view such as the images and the dawson college
     * course id. Sets onclick listeners on each of those views.
     *
     * @param view View containing the fragment
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        lyreneImg = (ImageView) view.findViewById(R.id.lyreneIV);
        lyreneImg.setOnClickListener(this);

        pengImg = (ImageView) view.findViewById(R.id.pengIV);
        pengImg.setOnClickListener(this);

        philImg = (ImageView) view.findViewById(R.id.philIV);
        philImg.setOnClickListener(this);

        peterImg = (ImageView) view.findViewById(R.id.peterIV);
        peterImg.setOnClickListener(this);

        courseId = (TextView) view.findViewById(R.id.courseTitle);
        courseId.setOnClickListener(this);
    }

    /**
     * Handles the onclick event of the View containing the fragment.
     * Verifies the id of the view that was selected and calls the appropriate
     * event handler method.
     *
     * @param v View that was selected
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyreneIV:
                Log.d(TAG, "lyrene image clicked started");
                displayBlurbDialog(1);
                break;
            case R.id.pengIV:
                Log.d(TAG, "peng image clicked started");
                displayBlurbDialog(2);
                break;
            case R.id.philIV:
                Log.d(TAG, "phil image clicked started");
                displayBlurbDialog(3);
                break;
            case R.id.peterIV:
                Log.d(TAG, "peter image clicked started");
                displayBlurbDialog(4);
                break;
            case R.id.courseTitle:
                Log.d(TAG, "course clicked started");
                launchCompSciWeb();
                break;
        }
    }

    /**
     * Launches the computer science dawson web page
     */
    private void launchCompSciWeb(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dawsoncollege.qc.ca/computer-science-technology/"));
        startActivity(browserIntent);
    }

    /**
     * Displays the dialog containing the team member's blurb. Based on the index, the selected
     * person's blurb will display.
     *
     * @param index index of the person selected
     */
    private void displayBlurbDialog(int index){
        Log.i(TAG, "displayBlurbDialog");
        String message = "";
        String person = "";

        //retrieve the appropriate message based on the index
        switch (index){
            case 1 :
                message = getResources().getString(R.string.lyrene_des);
                person = getResources().getString(R.string.lyrene);
                break;
            case 2 :
                message = getResources().getString(R.string.peng_des);
                person = getResources().getString(R.string.peng);
                break;
            case 3 :
                message = getResources().getString(R.string.phil_des);
                person = getResources().getString(R.string.phil);
                break;
            case 4 :
                message = getResources().getString(R.string.peter_des);
                person = getResources().getString(R.string.peter);
                break;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.blurb_title) + " " + person);
        builder.setMessage(message)
                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog ad = builder.create();
        ad.show();
    }
}
