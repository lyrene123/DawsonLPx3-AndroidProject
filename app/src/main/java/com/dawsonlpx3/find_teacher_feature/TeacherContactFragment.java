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

public class TeacherContactFragment extends Fragment {
    private TeacherDetails teacher;
    private TextView fnameTV, lnameTV, emailTV, localTV, positionTV, departmentTV, sectorTV, officeTV,
            websiteTV, bioTV;
    private ImageView imageView;
    private View mainView;
    private final String TAG = "Dawson-TeacherContact";
    private static final int PERMISSION_REQUEST_CALL = 0;

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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_teacher_contact, parent, false);
    }

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

    private void sendEmail(){
        Log.d(TAG, "Sending email....");
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Html.fromHtml(this.teacher.getEmail()).toString()});
        Log.d(TAG, "Decoded email: " + Html.fromHtml(this.teacher.getEmail()).toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.from));
        startActivity(intent);
    }

    private void displayTeacherDetails(){
        Log.d(TAG, "displayTeacherDetails started");

        if(this.teacher.getFirst_name() == null || this.teacher.getFirst_name().isEmpty()){
            this.fnameTV.setText(getResources().getString(R.string.unavailable));
        }else{
            this.fnameTV.setText(this.teacher.getFirst_name());
        }

        if(this.teacher.getLast_name() == null || this.teacher.getLast_name().isEmpty()){
            this.lnameTV.setText(getResources().getString(R.string.unavailable));
        }else {
            this.lnameTV.setText(this.teacher.getLast_name());
        }

        if(this.teacher.getEmail() == null || this.teacher.getEmail().isEmpty()){
            this.emailTV.setText(getResources().getString(R.string.unavailable));
        }else {
            //the following is to underline the attributed name
            SpannableString content = new SpannableString(Html.fromHtml(this.teacher.getEmail()).toString());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            this.emailTV.setText(content);

            emailTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendEmail();
                }
            });
        }

        if(this.teacher.getOffice() == null || this.teacher.getOffice().isEmpty()){
            this.officeTV.setText(getResources().getString(R.string.unavailable));
        } else {
            this.officeTV.setText(this.teacher.getOffice());
        }

        if(this.teacher.getWebsite() == null || this.teacher.getWebsite().isEmpty()){
            this.websiteTV.setText(getResources().getString(R.string.unavailable));
        } else {
            this.websiteTV.setText(this.teacher.getWebsite());
        }

        if(this.teacher.getBio() == null || this.teacher.getBio().isEmpty()){
            this.bioTV.setText(getResources().getString(R.string.unavailable));
        } else {
            this.bioTV.setText(this.teacher.getBio());
        }

        if(this.teacher.getLocal() == null || this.teacher.getLocal().isEmpty()){
            this.localTV.setText(getResources().getString(R.string.unavailable));
        }else {
            //the following is to underline the attributed name
            SpannableString content = new SpannableString(this.teacher.getLocal());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            this.localTV.setText(content);

            localTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialPhoneNumber();
                }
            });
        }

        this.imageView.setImageResource(R.mipmap.ic_launcher);

        List<Object> positions = this.teacher.getPositions();
        if(positions != null && positions.size() > 0) {
            for (int i = 0; i < positions.size(); i++) {
                this.positionTV.setText(this.positionTV.getText() + "\n" + positions.get(i));
            }
        } else {
            this.positionTV.setText(getResources().getString(R.string.unavailable));
        }

        List<Object> departments = this.teacher.getDepartments();
        if(departments != null && departments.size() > 0) {
            for (int i = 0; i < departments.size(); i++) {
                this.departmentTV.setText(this.departmentTV.getText() + "\n" + departments.get(i));
            }
        }else {
            this.departmentTV.setText(getResources().getString(R.string.unavailable));
        }

        List<Object> sectors = this.teacher.getSectors();
        if(sectors != null && sectors.size() > 0) {
            for (int i = 0; i < sectors.size(); i++) {
                this.sectorTV.setText(this.sectorTV.getText() + "\n" + sectors.get(i));
            }
        } else {
            this.sectorTV.setText(getResources().getString(R.string.unavailable));
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("teacher", this.teacher);
    }

    //https://github.com/googlesamples/android-RuntimePermissionsBasic
    private void dialPhoneNumber() {
        Log.d(TAG, "Calling a teacher....");

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            startCall();
        } else {
            requestCallPermission();
        }
    }

    private void requestCallPermission() {
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

    //https://stackoverflow.com/questions/18216563/making-a-phone-call-with-a-number-extension
    private void startCall(){
        Log.d(TAG, "startCall");
        Intent intent = new Intent(Intent.ACTION_CALL);
        String dawsonNumber;
        String ext = "";
        if(teacher.getLocal().length() <= 4) {
            dawsonNumber  = getResources().getString(R.string.dawsonPhoneNum);
            ext = ","+teacher.getLocal();
        } else {
            dawsonNumber = teacher.getLocal();
        }

        Uri uri = Uri.parse("tel:" + dawsonNumber + ext);
        Log.d(TAG, "phone num: " + uri.toString());
        intent.setData(uri);
        startActivity(intent);
    }



}
