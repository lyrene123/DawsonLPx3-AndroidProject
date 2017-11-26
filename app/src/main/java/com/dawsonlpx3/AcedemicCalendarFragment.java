package com.dawsonlpx3;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AcedemicCalendarFragment extends Fragment implements View.OnClickListener {

    private View view;

    private final String TAG = "Dawson-AcademicCalendar";
    private RadioButton fallRadioBtn, winterRadioBtn;
    private Button searchButton;
    private EditText yearET;
    private WebView webview;
    private TextView errorMsgTV;

    private boolean isSearched;
    private String currentYear, currentSem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCurrentDate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_acedemic_calendar, container, false);
        Log.d(TAG, "onCreateView started");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated started");
        fallRadioBtn = (RadioButton) view.findViewById(R.id.fallRadioBtn);
        winterRadioBtn = (RadioButton) view.findViewById(R.id.winterRadioBtn);
        searchButton = (Button) view.findViewById(R.id.calendarSearch);
        searchButton.setOnClickListener(this);
        yearET = (EditText) view.findViewById(R.id.yearET);
        webview = (WebView) view.findViewById(R.id.academicWebview);
        errorMsgTV = (TextView) view.findViewById(R.id.calendarErrorMsg);

        if(!currentSem.isEmpty()){
            if(currentSem.equals("fall")){
                fallRadioBtn.setChecked(true);
            } else {
                winterRadioBtn.setChecked(true);
            }
            yearET.setText(this.currentYear);
            loadWebview();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume started");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendarSearch:
                Log.d(TAG, "search button clicked started");
                //if search button was clicked and not currently already querying the database
                if(!isSearched) {
                    loadWebview();
                }
                break;
        }
    }

    private void loadWebview(){
        if(yearET.getText() != null && !yearET.getText().toString().isEmpty()){
            isSearched = true;
            errorMsgTV.setText("");
            Toast.makeText(getActivity(), getResources().getString(R.string.loading_calendar), Toast.LENGTH_LONG).show();
            try{
                int inputYear = Integer.parseInt(yearET.getText().toString()); //this will throw an exception if wrong input
                String chosenSem;
                if(fallRadioBtn.isChecked()){
                    chosenSem = getResources().getString(R.string.fallurl);
                } else {
                    chosenSem = getResources().getString(R.string.winterurl);
                }
                String chosenYear = Html.escapeHtml(yearET.getText().toString());
                String url = getResources().getString(R.string.academicurl) + "/" + chosenSem + "-" + chosenYear + "-day-division/";
                webview.setWebViewClient(new WebViewClient());
                webview.loadUrl(url);
            } catch(NumberFormatException ex){
                errorMsgTV.setText(R.string.bad_year);
            }
        } else {
            errorMsgTV.setText(R.string.bad_year);
        }
        isSearched = false;
    }

    private void getCurrentDate(){
        Calendar c = Calendar.getInstance();
        this.currentYear = String.valueOf(c.get(Calendar.YEAR));
        int month = c.get(Calendar.MONTH)+1;
        if(month >= 1 && month <= 5){
            currentSem = "winter";
        } else if (month >= 8 && month <= 12){
            currentSem = "fall";
        } else {
            currentSem = "";
        }
    }
}
