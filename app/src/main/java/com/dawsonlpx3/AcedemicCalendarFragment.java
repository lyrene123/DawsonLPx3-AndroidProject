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
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Fragment activity which handles the loading of the academic calendar into the webview for the user
 * to see. Handles as well the feature of letting the user input the term and the year of the
 * academic calendar to display into the webview. Verifies if user's input are valid before loading
 * and building a url for the academic calendar.
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Phil Langloid
 * @author Peter Bellefleur
 */
public class AcedemicCalendarFragment extends Fragment implements View.OnClickListener {

    //widgets retrieved from the view
    private View view;
    private RadioButton fallRadioBtn, winterRadioBtn;
    private Button searchButton;
    private EditText yearET;
    private WebView webview;
    private TextView errorMsgTV;

    private boolean isSearched;
    private String currentYear, currentSem;
    private final String TAG = "Dawson-AcademicCalendar";

    /**
     * Retrieves saved elements from the bundle such as the user's input year and user's choice of
     * winter or fall. If there elements existing in the bundle, then use those data to load
     * the appropriate calendar. If not, then retrieve the current year and month.
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            currentYear = savedInstanceState.getString("year");
            if(savedInstanceState.getBoolean("isFall")){
                currentSem = "fall";
            } else {
                currentSem = "winter";
            }
        } else {
            getCurrentDate();
        }
    }

    /**
     * Inflates the layout of the fragment into the view.
     *
     * @param inflater LayoutInflater onjectg
     * @param container ViewGroup object
     * @param savedInstanceState Bundle object
     * @return The view containing the fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_acedemic_calendar, container, false);
        Log.d(TAG, "onCreateView started");
        return view;
    }

    /**
     * Retrieves the necessary widgets from the view and restores the elements
     * taken from the bundle in onCreate into the view if applicable. Started the loading of the
     * webview.
     *
     * @param view View containing the fragment
     * @param savedInstanceState  Bundle object
     */
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

        //if restored elements from the bundle existing, then use that to start loading the webview
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


    /**
     * Handles the click event happening in the view. Checks the id of the specific view that was clicked
     * and calls the appropriate handler method.
     *
     * @param v View that was clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendarSearch:
                Log.d(TAG, "search button clicked started");
                //if search button was clicked then load webview
                //only do this when webview is not currently loading.
                if(!isSearched) {
                    loadWebview();
                }
                break;
        }
    }

    /**
     * Verifies first if the input year of the user is valid by checking if not empty
     * and if value is an integer. If value is valid, builds the url of the academic calendar
     * in dawson website and loads the url into the webview. All error messages are displayed
     * if input year is invalid.
     */
    private void loadWebview(){
        //verify if input year not empty
        if(yearET.getText() != null && !yearET.getText().toString().isEmpty()){
            isSearched = true;
            errorMsgTV.setText("");
            Toast.makeText(getActivity(), getResources().getString(R.string.loading_calendar), Toast.LENGTH_LONG).show();
            try{
                int inputYear = Integer.parseInt(yearET.getText().toString()); //this will throw an exception if wrong input

                //retrieve chosen semester winter/fall
                String chosenSem;
                if(fallRadioBtn.isChecked()){
                    chosenSem = getResources().getString(R.string.fallurl);
                } else {
                    chosenSem = getResources().getString(R.string.winterurl);
                }
                //retrieve the input year
                String chosenYear = yearET.getText().toString();

                //build url and then load url
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

    /**
     * Retrieves the current year and month and stores the year into a variable and based
     * on the month number, store the appropriate semester.
     */
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

    /**
     * Saves the chosen semester of the user and the user's input year into the Bundle to be
     * restores back in the onCreate method.
     *
     * @param outState Bundle object
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFall", fallRadioBtn.isChecked());
        outState.putString("year", yearET.getText().toString());
    }
}
