package com.dawsonlpx3.find_teacher_feature;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dawsonlpx3.R;
import com.dawsonlpx3.data.TeacherDetails;

import java.util.List;

/**
 * Fragment class which handles the display of all contact details of a Dawson college teacher.
 * Handles the click events of the local and the email address of the teacher which starts a phone call
 * to contact the teacher or creates an email to send to the teacher's email address. Handles as
 * well the saving and restoring of the teacher object when app is being stopped or paused. Requires
 * the necessary permissions to the user when trying to access the phone of their device.
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefleur
 */
public class TeacherContactFragment extends Fragment {
    private TeacherDetails teacher; //TeacherDetails object to display

    //widgets taken from the view
    private TextView fnameTV, lnameTV, emailTV, localTV, positionTV, departmentTV, sectorTV, officeTV,
            websiteTV, bioTV;
    private ImageView imageView;
    private View mainView;

    private final String TAG = "Dawson-TeacherContact";
    private static final int PERMISSION_REQUEST_CALL = 0;

    /**
     * Retrieves the teacher object to be displayed from the arguments passed to the fragment if there is. If not, then
     * retrieve the teacher object from the Bundle
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate started");
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            // Get back arguments
            if(getArguments() != null) {
                teacher = (TeacherDetails) getArguments().getSerializable("teacher");
            }
        } else {
            this.teacher = (TeacherDetails) savedInstanceState.getSerializable("teacher");
        }
    }

    /**
     * Inflates the layout into the fragment and returns the view containing the fragment
     *
     * @param inflater LayoutInflater object
     * @param parent ViewGroup object
     * @param savedInstanceState Bundle object
     * @return View containing the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_teacher_contact, parent, false);
    }

    /**
     * Retrieves the necessary widgets from the view and then display the teacher details into those
     * widgets
     *
     * @param view View of the fragment
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.fnameTV = (TextView) view.findViewById(R.id.firstnameTextView);
        this.lnameTV = (TextView) view.findViewById(R.id.lastnameTextView);
        this.emailTV = (TextView) view.findViewById(R.id.emailTextView);
        this.localTV = (TextView) view.findViewById(R.id.localTextView);
        this.positionTV = (TextView) view.findViewById(R.id.positionTextView);
        this.departmentTV = (TextView) view.findViewById(R.id.departmentTextView);
        this.sectorTV = (TextView) view.findViewById(R.id.sectorTextView);
        this.officeTV = (TextView) view.findViewById(R.id.officeTextView);
        this.websiteTV = (TextView) view.findViewById(R.id.websiteTextView);
        this.bioTV = (TextView) view.findViewById(R.id.bioTextView);
        this.imageView = (ImageView) view.findViewById(R.id.imageImageView);
        this.mainView = view.findViewById(R.id.teacherContactLayout);
        displayTeacherDetails();
    }

    /**
     * Creates an intent for sending an email and starts the intent and display an email to be sent
     * to the teacher in display
     */
    private void sendEmail(){
        Log.d(TAG, "Sending email....");
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Html.fromHtml(this.teacher.getEmail()).toString()});
        Log.d(TAG, "Decoded email: " + Html.fromHtml(this.teacher.getEmail()).toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.from));
        startActivity(intent);
    }

    /**
     * Displays the teacher's information to the appropriate widget in the view. Verifies first if the
     * detail to display is not null nor empty in order to display the detail. If detail is null, then
     * the default string in the widgets is 'unavailable'. If this check if not done, then some
     * text views will be completely empty.
     */
    private void displayTeacherDetails(){
        Log.d(TAG, "displayTeacherDetails started");
        //display first name
        if(this.teacher.getFirst_name() != null && !this.teacher.getFirst_name().isEmpty()) {
            this.fnameTV.setText(this.teacher.getFirst_name());
        }
        //display last name
        if(this.teacher.getLast_name() != null && !this.teacher.getLast_name().isEmpty()) {
            this.lnameTV.setText(this.teacher.getLast_name());
        }
        //display teacher office
        if(this.teacher.getOffice() != null && !this.teacher.getOffice().isEmpty()) {
            this.officeTV.setText(this.teacher.getOffice());
        }
        //display website
        if(this.teacher.getWebsite() != null && !this.teacher.getWebsite().isEmpty()) {
            this.websiteTV.setText(this.teacher.getWebsite());
        }
        //display bio
        if(this.teacher.getBio() != null && !this.teacher.getBio().isEmpty()) {
            this.bioTV.setText(this.teacher.getBio());
        }

        this.imageView.setImageResource(R.drawable.unknows); //default image since teacher image not provided

        //the following is to underline the email and make it clickable only if email is not null nor empty
        if(this.teacher.getEmail() != null && !this.teacher.getEmail().isEmpty()){
            SpannableString content = new SpannableString(Html.fromHtml(this.teacher.getEmail()).toString());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            this.emailTV.setText(content);
            //only make the email clickable if there is an existing email
            emailTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendEmail();
                }
            });
        }

        //the following is to underline the local and make it clickable only if local is not null nor empty
        if(this.teacher.getLocal() != null && !this.teacher.getLocal().isEmpty()){
            //the following is to underline the attributed name
            SpannableString content = new SpannableString(this.teacher.getLocal());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            this.localTV.setText(content);
            //only make the local clickable if there is an existing local
            localTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialPhoneNumber();
                }
            });
        }

        //display the positions
        List<Object> positions = this.teacher.getPositions();
        if(positions != null && positions.size() > 0) {
            this.positionTV.setText("");
            for (int i = 0; i < positions.size(); i++) {
                this.positionTV.setText(positions.get(i)+ " " + this.positionTV.getText());
            }
        }
        //display the departments
        List<Object> departments = this.teacher.getDepartments();
        if(departments != null && departments.size() > 0) {
            this.departmentTV.setText("");
            for (int i = 0; i < departments.size(); i++) {
                this.departmentTV.setText(departments.get(i) + " " + this.departmentTV.getText());
            }
        }
        //display the sectors
        List<Object> sectors = this.teacher.getSectors();
        if(sectors != null && sectors.size() > 0) {
            this.sectorTV.setText("");
            for (int i = 0; i < sectors.size(); i++) {
                this.sectorTV.setText(sectors.get(i) + " " + this.sectorTV.getText());
            }
        }
    }

    /**
     * Saves the teacher object that is being displayed so that even after device rotation,
     * the data being displayed will be restored back to the view.
     *
     * @param outState Bundle object
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("teacher", this.teacher);
    }

    /**
     * Starts a phone call by checking if app has already the right permissions to open the phone
     * of the device. If not, then the permission will be requested to the user.
     *
     * Solution found: https://github.com/googlesamples/android-RuntimePermissionsBasic
     */
    private void dialPhoneNumber() {
        Log.d(TAG, "Calling a teacher....");
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            startCall();
        } else {
            requestCallPermission();
        }
    }

    /**
     * Requests permission to the user to open phone of their device and process the answer of the
     * user. A message will be displayed to the user about why the permission is necessary if applicable.
     * If a message is not needed, then proceeds to acquire the permission.
     */
    private void requestCallPermission() {
        Log.d(TAG, "Requesting for phone call permission");
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.CALL_PHONE)) {
            Snackbar.make(this.mainView, getResources().getString(R.string.phone_access_required),
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_CALL);
                }
            }).show();
        } else {
            Snackbar.make(this.mainView,
                    getResources().getString(R.string.permission_unavailable),
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE},
                    PERMISSION_REQUEST_CALL);
        }
    }

    /**
     * Process the user's answer of whether or not to grant permission to let the app access the phone
     * of their device. If permission granted, then the phone call will be started. If not,
     * then a message will be displayed letting the user know that the phone call is denied.
     *
     * @param requestCode int request code
     * @param permissions string array of permissions requested
     * @param grantResults int array of the result code
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_CALL) {
            // Request for call permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start phone call.
                Snackbar.make(this.mainView, getResources().getString(R.string.permission_granted),
                        Snackbar.LENGTH_SHORT)
                        .show();
                startCall();
            } else {
                // Permission request was denied.
                Snackbar.make(this.mainView, getResources().getString(R.string.permission_denied),
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Starts a phone call by creating and launching an intent and passing it the phone call and the extension
     * of the teacher to contact.
     */
    private void startCall(){
        Log.d(TAG, "startCall");
        Intent intent = new Intent(Intent.ACTION_CALL);
        String dawsonNumber;
        String ext = "";

        //if retrieved local is only an extension number, append the dawson number
        if(teacher.getLocal().length() <= 4) {
            dawsonNumber  = getResources().getString(R.string.dawsonPhoneNum);
            ext = ","+teacher.getLocal();
        } else {
            //if retrieved local is a complete phone number, dont append the dawson number
            dawsonNumber = teacher.getLocal();
        }

        Uri uri = Uri.parse("tel:" + dawsonNumber + ext);
        Log.d(TAG, "phone num: " + uri.toString());
        intent.setData(uri);
        startActivity(intent);
    }



}
