package com.dawsonlpx3;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.provider.CalendarContract.Events;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This fragment will let user add an event to the Google Calendar
 * by completing the form which has Title, Location, Start Date,
 * End Date and Description. When user click addToCalendar button
 * the Google calendar app will show.
 */
public class AddToCalendarFragment extends Fragment {

    private final String TAG = "AddToCalendar";

    private Calendar startDate, endDate, currentDate;
    private SimpleDateFormat formatter;

    private TextView startsTextView, endsTextView;
    private EditText titleEditText, locationEditText, descriptionEditText;
    private Button addToCalendarBtn;

    /**
     * Instantiate the startDate, endDate, currentDate and formatter when
     * this first call
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        currentDate = Calendar.getInstance();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Inflate the fragment_add_to_calendar when this method get called. This
     * is called after onAttach().
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_to_calendar, container, false);
    }

    /**
     * Initialized the views and set its listener.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleEditText = (EditText) view.findViewById(R.id.titleEditText);
        locationEditText = (EditText) view.findViewById(R.id.locationEditText);
        descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);

        startsTextView = (TextView) view.findViewById(R.id.startsTextView);
        startsTextView.setOnClickListener(showStartDatePicker);

        endsTextView = (TextView) view.findViewById(R.id.endsTextView);
        endsTextView.setOnClickListener(showEndDatePicker);

        addToCalendarBtn = (Button) view.findViewById(R.id.addToCalendarBtn);
        addToCalendarBtn.setOnClickListener(addToCalendar);
    }

    /**
     * Save the state of Starts and Ends because they are TextView, so
     * android framework doesn't save by default. The startDate and endDate
     * of type Calendar needs to be saved also, so they can't be overriden
     * in onCreate.
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("startDate", startsTextView.getText().toString());
        outState.putString("endDate", endsTextView.getText().toString());
        outState.putLong("startDateCalendar", startDate.getTimeInMillis());
        outState.putLong("endDateCalendar", endDate.getTimeInMillis());
    }

    /**
     * Restore the state of Starts and Ends because they are TextView, so
     * it won't save by android framework.
     *
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            startsTextView.setText(savedInstanceState.getString("startDate"));
            endsTextView.setText(savedInstanceState.getString("endDate"));
            startDate.setTimeInMillis(savedInstanceState.getLong("startDateCalendar"));
            endDate.setTimeInMillis(savedInstanceState.getLong("endDateCalendar"));
        }
    }

    /**
     * This is a listener to start date
     */
    private View.OnClickListener showStartDatePicker = new View.OnClickListener() {
        /**
         * When user click on the startTextView, a DatePickerDialog will pop up to let
         * user pick a date. The minimum date is today's date.
         *
         * @param view
         */
        @Override
        public void onClick(View view) {
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.DialogTheme
                    , startDatePickerListener, currentDate.get(Calendar.YEAR)
                    , currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
            dialog.show();
        }

        /**
         * Listener for DatePickerDialog
         */
        DatePickerDialog.OnDateSetListener startDatePickerListener
                = new DatePickerDialog.OnDateSetListener() {

            /**
             * When the user pick a date, update the startDate and startsTextView.
             * This also ensure that start date is always before or the same as
             * end date by updating end date.
             *
             * @param view
             * @param selectedYear
             * @param selectedMonth
             * @param selectedDay
             */
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                startDate.set(selectedYear, selectedMonth, selectedDay);
                startsTextView.setText(formatter.format(startDate.getTime()));
                startsTextView.setTextColor(Color.BLACK);

                // if start date is more than end date, set the end date to be
                // the same start date
                if (startDate.compareTo(endDate) > 0) {
                    endDate.setTime(startDate.getTime());
                    endsTextView.setText(formatter.format(endDate.getTime()));
                }
            }
        };
    };

    /**
     * This is a listener for Ends date picker field
     */
    private View.OnClickListener showEndDatePicker = new View.OnClickListener() {

        /**
         * When user click on the endsTextView, a DatePickerDialog will pop up
         * with minimum date is the start date
         * @param view
         */
        @Override
        public void onClick(View view) {
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.DialogTheme
                    , endDatePickerListener, currentDate.get(Calendar.YEAR)
                    , currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
            dialog.show();
        }

        /**
         * Listener of the Ends DatePickerDialog
         */
        DatePickerDialog.OnDateSetListener endDatePickerListener
                = new DatePickerDialog.OnDateSetListener() {
            /**
             * When user pick a date, this will update the endDate and
             * endsTextView
             *
             * @param view
             * @param selectedYear
             * @param selectedMonth
             * @param selectedDay
             */
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                endDate.set(selectedYear, selectedMonth, selectedDay);
                endsTextView.setText(formatter.format(endDate.getTime()));
                endsTextView.setTextColor(Color.BLACK);
            }
        };
    };

    /**
     * This is a listener for addToCalendar button
     */
    private View.OnClickListener addToCalendar = new View.OnClickListener() {

        String title, location, startTime, endTime, description;

        /**
         * Start intent to show Google calendar with the information user
         * input in the fields
         *
         * @param view
         */
        @Override
        public void onClick(View view) {
            if (isValidated()) {
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.getTimeInMillis())
                        .putExtra(Events.TITLE, title)
                        .putExtra(Events.DESCRIPTION, description)
                        .putExtra(Events.EVENT_LOCATION, location);
                startActivity(intent);
            }
        }

        /**
         * Validate the user input. Location and description can be empty, but others
         * can't. The title must be completed, and the dates (stars and ends) must be
         * choosen. If any of them is violated, this will show error messages.
         *
         * @return true if title is filled, Starts and Ends have dates
         */
        private boolean isValidated() {
            title = titleEditText.getText().toString();
            location = locationEditText.getText().toString();
            startTime = startsTextView.getText().toString();
            endTime = endsTextView.getText().toString();
            description = descriptionEditText.getText().toString();

            if (title.isEmpty()) {
                titleEditText.setHint(getResources().getString(R.string.completeThisField));
                titleEditText.setHintTextColor(Color.RED);
                return false;
            }
            if (startTime.equals(getString(R.string.startDate))
                    || startTime.equals(getString(R.string.pickADate))) {
                startsTextView.setText(getResources().getString(R.string.pickADate));
                startsTextView.setTextColor(Color.RED);
                return false;
            }
            if (endTime.equals(getString(R.string.endDate))
                    || endTime.equals(getString(R.string.pickADate))) {
                endsTextView.setText(getResources().getString(R.string.pickADate));
                endsTextView.setTextColor(Color.RED);
                return false;
            }

            return true;
        }
    };

}
