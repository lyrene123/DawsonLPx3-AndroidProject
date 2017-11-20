package com.example.kimhyonh.dawsonlpx3;

import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends MenuActivity {

    private static final String TAG = "SettignsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.d(TAG, "Start Activity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
