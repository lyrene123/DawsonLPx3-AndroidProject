package com.dawsonlpx3.teacher_activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dawsonlpx3.Manifest;
import com.dawsonlpx3.R;
import com.dawsonlpx3.data.TeacherDetails;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeacherContactFragment extends Fragment {
    private TeacherDetails teacher;
    private TextView fnameTV, lnameTV, emailTV, localTV, positionTV, departmentTV, sectorTV, officeTV;
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
        this.mainView = view.findViewById(R.id.teacherContactLayout);
        displayTeacherDetails();

        localTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhoneNumber();
            }
        });
    }

    private void displayTeacherDetails(){
        Log.d(TAG, "displayTeacherDetails started");
        this.fnameTV.setText(this.teacher.getFirst_name());
        this.lnameTV.setText(this.teacher.getLast_name());
        this.emailTV.setText(this.teacher.getEmail());
        this.localTV.setText(this.teacher.getLocal());
        this.officeTV.setText(this.teacher.getOffice());

        List<Object> positions = this.teacher.getPositions();
        for(int i = 0; i < positions.size(); i++){
            this.positionTV.setText(this.positionTV.getText() + "\n" + positions.get(i));
        }

        List<Object> departments = this.teacher.getDepartments();
        for(int i = 0; i < departments.size(); i++){
            this.departmentTV.setText(this.departmentTV.getText() + "\n" + departments.get(i));
        }

        List<Object> sectors = this.teacher.getSectors();
        for(int i = 0; i < sectors.size(); i++){
            this.sectorTV.setText(this.sectorTV.getText() + "\n" + sectors.get(i));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("teacher", this.teacher);
    }

    //https://github.com/googlesamples/android-RuntimePermissionsBasic/blob/master/Application/src/main/java/com/example/android/basicpermissions/MainActivity.java
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
        String dawsonNumber = getResources().getString(R.string.dawsonPhoneNum);
        String ext = PhoneNumberUtils.PAUSE+Uri.encode("#")+teacher.getLocal();
        Uri uri = Uri.parse("tel:" + dawsonNumber + ext);
        Log.d(TAG, "phone num: " + uri.toString());
        intent.setData(uri);
        startActivity(intent);
    }

}
