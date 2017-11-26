package com.dawsonlpx3;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class AcedemicCalendarFragment extends Fragment {

    private View view;

    private final String TAG = "Dawson-AcademicCalendar";
    private RadioButton fallRadioBtn, winterRadioBtn;
    private Button searchButton;
    private EditText yearET;
    private WebView webview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_acedemic_calendar, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated started");
        fallRadioBtn = (RadioButton) view.findViewById(R.id.fallRadioBtn);
        winterRadioBtn = (RadioButton) view.findViewById(R.id.winterRadioBtn);
        searchButton = (Button) view.findViewById(R.id.calendarSearch);
        yearET = (EditText) view.findViewById(R.id.yearET);
        webview = (WebView) view.findViewById(R.id.academicWebview);
    }
}
