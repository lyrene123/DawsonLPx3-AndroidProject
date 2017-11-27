package com.dawsonlpx3.rss_parser;

import android.os.AsyncTask;

import com.dawsonlpx3.data.CanceledClassDetails;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by 1132371 on 11/27/2017.
 */

public class DawsonRssXmlDownloadTask extends AsyncTask<String, Void, List<CanceledClassDetails>> {

    @Override
    protected List<CanceledClassDetails> doInBackground(String... urls) {
        try {
            return loadXmlFromNetwork(urls[0]);
        } catch (IOException ioe) {
            return getResources.getString(R.string.connection_error);
        } catch (XmlPullParserException xppe) {
            return getResources().getString(R.string.xml_error);
        }
    }

    @Override
    protected void onPostExecute(List<CanceledClassDetails> result) {

    }

    private List<CanceledClassDetails> loadXmlFromNetwork(String url)
            throws XmlPullParserException, IOException{
        InputStream stream = null;
        DawsonRssXmlParser parser = new DawsonRssXmlParser();
        List<CanceledClassDetails> classes = null;

        try {
            stream = downloadUrl(url);
            classes = parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return classes;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }
}
