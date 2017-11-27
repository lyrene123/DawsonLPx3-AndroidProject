package com.dawsonlpx3;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dawsonlpx3.find_teacher_feature.FindTeacherFragment;
import com.thbs.skycons.library.CloudFogView;
import com.thbs.skycons.library.CloudHvRainView;
import com.thbs.skycons.library.CloudMoonView;
import com.thbs.skycons.library.CloudRainView;
import com.thbs.skycons.library.CloudSnowView;
import com.thbs.skycons.library.CloudSunView;
import com.thbs.skycons.library.CloudThunderView;
import com.thbs.skycons.library.CloudView;
import com.thbs.skycons.library.MoonView;
import com.thbs.skycons.library.SunView;
import com.thbs.skycons.library.WindView;

public class HomeFragment extends Fragment {

    /**
     * Inflate the fragment_home layout. This fragment will replace the
     * side_frame in the MainActivity
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton dawsonImageBtn = (ImageButton) view.findViewById(R.id.dawsonImageView);
        dawsonImageBtn.setOnClickListener(showDawsonWeb);

        ImageButton weatherImageBtn = (ImageButton) view.findViewById(R.id.weatherImageButton);
        weatherImageBtn.setOnClickListener(showWeather);

        ImageButton findTeacherImageBtn = (ImageButton) view.findViewById(R.id.findTeacherImageButton);
        findTeacherImageBtn.setOnClickListener(showFindTeacher);

        ImageButton calendarImageBtn = (ImageButton) view.findViewById(R.id.calendarImageButton);
        calendarImageBtn.setOnClickListener(showAddToCalendar);

        ImageButton acedemicCalendarImageBtn = (ImageButton) view.findViewById(R.id.acedemicCalendarImageButton);
        acedemicCalendarImageBtn.setOnClickListener(showAcedemicCalendar);

        ImageButton classCancelledImageBtn = (ImageButton) view.findViewById(R.id.classCancelImageButton);
        classCancelledImageBtn.setOnClickListener(showClassCancelled);

        ImageButton noteImageBtn = (ImageButton) view.findViewById(R.id.noteImageButton);
        noteImageBtn.setOnClickListener(showNote);

        ImageButton teamLogo = (ImageButton) view.findViewById(R.id.teamLogo);
        teamLogo.setOnClickListener(showAboutPage);

        // Example of how to set the weather with temperature
        setWeatherLayout(view, "NIGHT", "30");
    }


    /**
     * Show Dawson Computer Science web page on a browser
     * @param view
     */
    private View.OnClickListener showDawsonWeb = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.dawsoncollege.qc.ca/computer-science-technology/"));
            startActivity(intent);
        }
    };

    /**
     *
     * Show Weather fragment
     */
    private View.OnClickListener showWeather = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new WeatherActivity())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show Find Teacher fragment
     */
    private View.OnClickListener showFindTeacher = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new FindTeacherFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show Add To Calendar fragment
     */
    private View.OnClickListener showAddToCalendar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new AddToCalendarFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show Acedemic Calendar fragment
     */
    private View.OnClickListener showAcedemicCalendar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new FindTeacherFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show class cancelled fragment
     */
    private View.OnClickListener showClassCancelled = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new CanceledActivity())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show Note fragment
     */
    private View.OnClickListener showNote = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new NotesFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Show About Page
     *
     * @param view
     */
    private View.OnClickListener showAboutPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.side_frame, new AboutFragment())
                    .addToBackStack(null)
                    .commit();
        }
    };

    /**
     * Set the view in the FrameLayout with a SkyconView and a TextView. The
     * SkyconView is determined by the weather (2nd parameter), and TextView is the
     * temperature (3rd parameter).
     *
     * @param view - Need this view to find FrameLayout and TextView
     * @param weather - weather in terms of WINDY, SUNNY, ...
     * @param temp - temperature
     */
    private void setWeatherLayout(View view, String weather, String temp) {
        FrameLayout frameLayout = view.findViewById(R.id.weatherFrameLayout);
        TextView tempTextView = view.findViewById(R.id.tempTextView);
        View skyconView = createSkyconView(weather);

        tempTextView.setText(temp);
        frameLayout.addView(skyconView, 0);
    }

    /**
     * The {@link com.thbs.skycons.library.SkyconView} can be get from
     * https://github.com/torryharris/Skycons.
     *
     * Determine SkyconView by the weather
     *
     * @param weather - it can WINDY, SUNNY, CLOUDY, ...
     * @return SkyconView
     */
    private View createSkyconView(String weather) {
        View view = null;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        switch (weather) {
            case "WINDY":
                view = new WindView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "CLOUDY":
                view = new CloudView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "SUNNY":
                view = new SunView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "NIGHT":
                view = new MoonView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "PARTLY_CLOUDY_DAY":
                view = new CloudSunView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "PARTY_CLOUDY_NIGHT":
                view = new CloudMoonView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "HEAVY_RAIN":
                view = new CloudHvRainView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "SNOW":
                view = new CloudSnowView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "LIGHT_RAIN":
                view = new CloudRainView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "FOG":
                view = new CloudFogView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
            case "THUNDER":
                view = new CloudThunderView(this.getActivity(),false,false
                        , Color.rgb(0,0,139), Color.WHITE);
                break;
        }

        params.width = 300;
        params.height = 300;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        view.setLayoutParams(params);

        return view;
    }
}
