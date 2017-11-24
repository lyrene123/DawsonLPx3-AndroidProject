package com.dawsonlpx3;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Calendar;
import static android.content.Context.MODE_PRIVATE;

/**
 * Class that will show the current latitude, longitude and temperature
 *
 * Created by Philippe on 2017-11-23.
 */

public class TemperatureActivity extends Fragment{

    private View view;
    private static final String TAG = "TemperatureActivity";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_settings, container, false);




        return view;
    } // onCreateView()


} // TemperatureActivity
