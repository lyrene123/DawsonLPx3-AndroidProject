package com.dawsonlpx3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.provider.CalendarContract.Events;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.datatype.Duration;

/**
 * This fragment will let user add an event to the Google Calendar
 * by completing the form which has Title, Location, Start Date,
 * End Date and Description. When user click addToCalendar button
 * the Google calendar app will show.
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Phil Langloid
 * @author Peter Bellefleur
 */
public class AddToCalendarFragment extends Fragment {

    private final String TAG = "AddToCalendar";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 1;
    private ContentResolver cr;
    private ContentValues values;

    private Calendar startDateTime, endDateTime, currentDate;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    private TextView startsTextView, endsTextView, startTimeTextView, endTimeTextView;
    private EditText titleEditText, locationEditText, descriptionEditText;
    private Button addToCalendarBtn;

    /**
     * Instantiate the startDateTime, endDateTime, currentDate and dateFormatter when
     * this first call
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDateTime = Calendar.getInstance();
        endDateTime = Calendar.getInstance();
        currentDate = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        timeFormatter = new SimpleDateFormat("hh:mm a");
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

        startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
        startTimeTextView.setOnClickListener(showStartTimePicker);

        endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);
        endTimeTextView.setOnClickListener(showEndTimePicker);

        addToCalendarBtn = (Button) view.findViewById(R.id.addToCalendarBtn);
        addToCalendarBtn.setOnClickListener(addToCalendar);
    }

    /**
     * Save the state of Starts and Ends because they are TextView, so
     * android framework doesn't save by default. The startDateTime and endDateTime
     * of type Calendar needs to be saved also, so they can't be overriden
     * in onCreate.
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("startDateTime", startsTextView.getText().toString());
        outState.putString("endDateTime", endsTextView.getText().toString());
        outState.putString("startTime", startTimeTextView.getText().toString());
        outState.putString("endTime", endTimeTextView.getText().toString());
        outState.putLong("startDateCalendar", startDateTime.getTimeInMillis());
        outState.putLong("endDateCalendar", endDateTime.getTimeInMillis());
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
            startDateTime.setTimeInMillis(savedInstanceState.getLong("startDateCalendar"));
            endDateTime.setTimeInMillis(savedInstanceState.getLong("endDateCalendar"));
            startsTextView.setText(savedInstanceState.getString("startDateTime"));
            endsTextView.setText(savedInstanceState.getString("endDateTime"));
            startTimeTextView.setText(savedInstanceState.getString("startTime"));
            endTimeTextView.setText(savedInstanceState.getString("endTime"));
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
                    , startDatePickerListener, startDateTime.get(Calendar.YEAR)
                    , startDateTime.get(Calendar.MONTH), startDateTime.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
            dialog.show();
        }

        /**
         * Listener for DatePickerDialog
         */
        DatePickerDialog.OnDateSetListener startDatePickerListener
                = new DatePickerDialog.OnDateSetListener() {

            /**
             * When the user pick a date, update the startDateTime and startsTextView.
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
                startDateTime.set(selectedYear, selectedMonth, selectedDay);
                startsTextView.setText(dateFormatter.format(startDateTime.getTime()));
                startsTextView.setTextColor(Color.BLACK);

                // if start date is more than end date, set the end date to be
                // the same start date
                if (startDateTime.compareTo(endDateTime) > 0) {
                    endDateTime.setTime(startDateTime.getTime());
                    endsTextView.setText(dateFormatter.format(endDateTime.getTime()));
                    endTimeTextView.setText(timeFormatter.format(endDateTime.getTime()));
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
                    , endDatePickerListener, endDateTime.get(Calendar.YEAR)
                    , endDateTime.get(Calendar.MONTH), endDateTime.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(startDateTime.getTimeInMillis());
            dialog.show();
        }

        /**
         * Listener of the Ends DatePickerDialog
         */
        DatePickerDialog.OnDateSetListener endDatePickerListener
                = new DatePickerDialog.OnDateSetListener() {
            /**
             * When user pick a date, this will update the endDateTime and
             * endsTextView
             *
             * @param view
             * @param selectedYear
             * @param selectedMonth
             * @param selectedDay
             */
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                endDateTime.set(selectedYear, selectedMonth, selectedDay);
                endsTextView.setText(dateFormatter.format(endDateTime.getTime()));
                endsTextView.setTextColor(Color.BLACK);
            }
        };
    };

    /**
     * Show the TimePicker for start
     */
    private View.OnClickListener showStartTimePicker = new View.OnClickListener() {

        /**
         * When user click on the startTimeTextView, a TimePickerDialog will pop up
         * to let the user pick the time.
         * @param view
         */
        @Override
        public void onClick(View view) {
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), R.style.DialogTheme
                    , startTimePickerListner, startDateTime.get(Calendar.HOUR_OF_DAY)
                    , startDateTime.get(Calendar.MINUTE), false);
            dialog.show();
        }

        /**
         * Listener of the Stat Time picker dialog. If the start time is after the end time
         * update the end time to be the same as end time, and toast to notify the user.
         */
        TimePickerDialog.OnTimeSetListener startTimePickerListner
                = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                startDateTime.set(Calendar.HOUR_OF_DAY, hour);
                startDateTime.set(Calendar.MINUTE, minute);
                startTimeTextView.setText(timeFormatter.format(startDateTime.getTime()));
                if (startDateTime.compareTo(endDateTime) > 0) {
                    Toast.makeText(getActivity(), getString(R.string.startBeforeEnd)
                            , Toast.LENGTH_LONG).show();
                    endDateTime.setTimeInMillis(startDateTime.getTimeInMillis());
                    endTimeTextView.setText(timeFormatter.format(startDateTime.getTime()));
                }
            }
        };
    };

    /**
     * Listener for the end time picker
     */
    private View.OnClickListener showEndTimePicker = new View.OnClickListener() {

        /**
         * When user click on the endsTextView, a TimePickerDialog will pop up and
         * let the user choose a time in 12h format
         * @param view
         */
        @Override
        public void onClick(View view) {
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), R.style.DialogTheme
                    , endTimePickerListener, endDateTime.get(Calendar.HOUR_OF_DAY)
                    , endDateTime.get(Calendar.MINUTE), false);

            dialog.show();
        }

        /**
         * Listener of the EndTimePickerDialog. If the end time is before the start time
         * set the end time to be the same as end time and toast to notify the user.
         */
        TimePickerDialog.OnTimeSetListener endTimePickerListener
                = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                endDateTime.set(Calendar.HOUR_OF_DAY, hour);
                endDateTime.set(Calendar.MINUTE, minute);
                if (startDateTime.compareTo(endDateTime) > 0) {
                    Toast.makeText(getActivity(), getString(R.string.startBeforeEnd)
                            , Toast.LENGTH_LONG).show();
                } else {
                    endDateTime.setTimeInMillis(startDateTime.getTimeInMillis());
                    endTimeTextView.setText(timeFormatter.format(endDateTime.getTime()));
                }
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
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
            if (isValidated()) {
//                Intent intent = new Intent(Intent.ACTION_INSERT)
//                        .setData(Events.CONTENT_URI)
//                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDateTime.getTimeInMillis())
//                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDateTime.getTimeInMillis())
//                        .putExtra(Events.TITLE, title)
//                        .putExtra(Events.DESCRIPTION, description)
//                        .putExtra(Events.EVENT_LOCATION, location);
//                startActivity(intent);
                cr = getActivity().getContentResolver();
                values = new ContentValues();
                values.put(Events.CALENDAR_ID, 1);
                values.put(Events.DTSTART, startDateTime.getTimeInMillis());
                values.put(Events.DTEND, endDateTime.getTimeInMillis());
                values.put(Events.TITLE, title);
                values.put(Events.DESCRIPTION, description);
                values.put(Events.EVENT_LOCATION, location);
                values.put(Events.EVENT_TIMEZONE, startDateTime.getTimeZone().getDisplayName());


                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_CALENDAR}
                            , MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                    return;
                }
                Uri uri = cr.insert(Events.CONTENT_URI, values);
                Toast.makeText(getActivity(), getResources().getString(R.string.insertToCalendar)
                        , Toast.LENGTH_SHORT).show();
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS_REQUEST_WRITE_CALENDAR) {
            @SuppressLint("MissingPermission") Uri uri = cr.insert(Events.CONTENT_URI, values);
            Toast.makeText(getActivity(), getResources().getString(R.string.insertToCalendar)
                    , Toast.LENGTH_SHORT).show();
        }
    }
}
