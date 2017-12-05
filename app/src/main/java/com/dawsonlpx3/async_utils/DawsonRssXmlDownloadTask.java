package com.dawsonlpx3.async_utils;

import android.os.AsyncTask;
import android.util.Log;

import com.dawsonlpx3.canceled_class_feature.DawsonRssXmlParser;
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
public class DawsonRssXmlDownloadTask extends AsyncTask<String, Void,
        List<CanceledClassDetails>> {

    //tag for logging
    private final String INNER_TAG = "LPx3-XmlDownloadTask";

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
            return null;
        } catch (XmlPullParserException xppe) {
            return null;
        }
    }

    /**
     * Loads the XML from the RSS feed, then parses it into a List of CanceledClassDetails
     * objects.
     *
     * @param url   The URL for the RSS feed.
     * @return  A List of CanceledClassDetails objects, generated from data in the RSS feed.
     * @throws org.xmlpull.v1.XmlPullParserException if a problem parsing the XML occurs.
     * @throws java.io.IOException if a problem with the connection occurs.
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
     * @throws java.io.IOException  if a problem with the connection occurs.
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
