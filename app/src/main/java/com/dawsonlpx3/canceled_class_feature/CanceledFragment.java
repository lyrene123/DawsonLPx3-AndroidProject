package com.dawsonlpx3.canceled_class_feature;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.dawsonlpx3.R;
import com.dawsonlpx3.data.CanceledClassDetails;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * The CanceledFragment class represents a Fragment that displays a list of class cancellations,
 * obtained from the Dawson College RSS feed of class cancellations.
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 * @author Phil Langlois
 * @author Pengkim Sy
 */
public class CanceledFragment extends Fragment {

    //tag for logging
    private final String TAG = "LPx3-Canceled";
    //dawson rss feed url
    private final String RSS_URL =
            "https://www.dawsoncollege.qc.ca/wp-content/external-includes/cancellations/feed.xml";
    View view;
    Context context;
    ListView list;
    List<CanceledClassDetails> canceled;

    /**
     * Creates and returns the View hierarchy associated with this fragment. Inflates the layout
     * for this Fragment as defined in its layout resource.
     *
     * @param inflater  A LayoutInflater.
     * @param container The parent View containing the Fragment.
     * @param savedInstanceState    The Bundle passed to the Fragment.
     * @return  The inflated View.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.activity_canceled, container, false);
        return view;
    }

    /**
     * Retrieves handles to View objects to be modified programmatically and the current
     * application context once the fragment's parent Activity is created. Additionally, starts a
     * DawsonRssXmlDownloadTask to retrieve information from the RSS feed in the background.
     *
     * @param savedInstanceState    The Bundle passed to the Fragment.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = (ListView) view.findViewById(R.id.classCancelListView);
        context = view.getContext();
        new DawsonRssXmlDownloadTask().execute(RSS_URL);
    }

    /**
     * Ensures the XML download task completed successfully, and if so, creates an adapter to
     * populate the ListView with the data retrieved.
     *
     * @param canceled  A List of CanceledClassDetails objects, generated by the XML download task.
     * @param isIOError A Boolean, representing if an IOException occurred during the task's
     *                  runtime. True if an error occurred, false otherwise.
     * @param isXMLError    A Boolean, representing if an XML exception occurred during the task's
     *                      runtime. True if an error occurred, false otherwise.
     */
    private void populateListView(List<CanceledClassDetails> canceled, boolean isIOError,
                                  boolean isXMLError) {
        this.canceled = canceled;
        //display relevant errors to user in Toast as needed
        if (isIOError){
            Toast.makeText(context, getResources().getString(R.string.connection_error),
                    Toast.LENGTH_LONG).show();
        } else if (isXMLError) {
            Toast.makeText(context, getResources().getString(R.string.xml_error),
                    Toast.LENGTH_LONG).show();
        } else if (checkIfNoCancelled()) {
            Toast.makeText(context, getResources().getString(R.string.empty_list_error),
                    Toast.LENGTH_LONG).show();
        } else {
            //if list generated and not empty, send to adapter, assign adapter to list
            CanceledClassAdapter adapter = new CanceledClassAdapter(context, canceled);
            list.setAdapter(adapter);
        }
    }

    /**
     * Checks the data retrieved from the RSS feed to see if it represents cancelled classes, or
     * contains a message stating no classes are cancelled. If no classes are cancelled, the first
     * item in the list of CanceledClassDetails will have its title contain the string "No classes
     * cancelled." If this string is found, then no classes are found to be cancelled.
     * Additionally, no classes are found cancelled if the list is empty or null.
     *
     * @return true if no class is cancelled, false otherwise.
     */
    private boolean checkIfNoCancelled() {
        boolean isNoClassCancelled = true;
        if (canceled != null && ! canceled.isEmpty()) {
            if (! canceled.get(0).getTitle().equals("No classes cancelled.")) {
                isNoClassCancelled = false;
            }
        }
        return isNoClassCancelled;
    }

    /**
     * The DawsonRssXmlDownloadTask is a Task that will retrieve data from the Dawson College RSS
     * feed of cancelled classes in a background thread.
     */
    private class DawsonRssXmlDownloadTask extends AsyncTask<String, Void,
            List<CanceledClassDetails>> {

        //tag for logging
        private final String INNER_TAG = "LPx3-XmlDownloadTask";
        //flags to track any exceptions that occur
        private boolean isIOError = false;
        private boolean isXMLError = false;

        /**
         * Retrieves a List of CanceledClassDetails objects from the Dawson College RSS feed. The
         * List may be empty if no classes are found to be cancelled.
         *
         * @param urls  URLs associated with XML to parse.
         * @return  A List of CanceledClassDetails objects (may be empty).
         */
        @Override
        protected List<CanceledClassDetails> doInBackground(String... urls) {
            Log.d(INNER_TAG, "doInBackground");
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException ioe) {
                this.isIOError = true;
                return null;
            } catch (XmlPullParserException xppe) {
                this.isXMLError = true;
                return null;
            }
        }

        /**
         * Populates the List in the CanceledFragment once the XML has been parsed.
         *
         * @param result    A List of CanceledClassDetails objects generated from the RSS feed (may
         *                  be empty).
         */
        @Override
        protected void onPostExecute(List<CanceledClassDetails> result) {
            Log.d(INNER_TAG, "onPostExecute");
            populateListView(result, isIOError, isXMLError);
        }

        /**
         * Loads the XML from the RSS feed, then parses it into a List of CanceledClassDetails
         * objects.
         *
         * @param url   The URL for the RSS feed.
         * @return  A List of CanceledClassDetails objects, generated from data in the RSS feed.
         * @throws XmlPullParserException if a problem parsing the XML occurs.
         * @throws IOException if a problem with the connection occurs.
         */
        private List<CanceledClassDetails> loadXmlFromNetwork(String url)
                throws XmlPullParserException, IOException{
            Log.d(INNER_TAG, "loadXmlFromNetwork");
            InputStream stream = null;
            DawsonRssXmlParser parser = new DawsonRssXmlParser();
            List<CanceledClassDetails> classes = null;
            //try to download the RSS feed into the InputStream, then send to parser class
            try {
                stream = downloadUrl(url);
                classes = parser.parse(stream);
                Log.d(INNER_TAG, "XML parsed.");
            } finally {
                //close stream if needed
                if (stream != null) {
                    stream.close();
                }
            }
            return classes;
        }

        /**
         * Downloads the HTML contents of a web page, and places them in an InputStream.
         *
         * @param urlString The URL to download from.
         * @return  An InputStream, containing the contents of the downloaded web page.
         * @throws IOException  if a problem with the connection occurs.
         */
        private InputStream downloadUrl(String urlString) throws IOException {
            Log.d(INNER_TAG, "Attempting to download XML from URL...");
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            Log.d(INNER_TAG, "XML downloaded.");
            return conn.getInputStream();
        }
    }
}
