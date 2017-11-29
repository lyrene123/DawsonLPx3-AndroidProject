package com.dawsonlpx3;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.dawsonlpx3.data.CanceledClassDetails;
import com.dawsonlpx3.rss_parser.DawsonRssXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CanceledActivity extends Fragment {

    private final String TAG = "LPx3-Canceled";
    private final String RSS_URL =
            "https://www.dawsoncollege.qc.ca/wp-content/external-includes/cancellations/feed.xml";
    View view;
    Context context = getActivity();
    ListView list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.activity_canceled, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = (ListView) view.findViewById(R.id.classCancelListView);
        new DawsonRssXmlDownloadTask().execute(RSS_URL);
    }

    private void populateListView(List<CanceledClassDetails> canceled) {
        CanceledClassAdapter adapter = new CanceledClassAdapter(context, canceled);
        list.setAdapter(adapter);
    }

    private class DawsonRssXmlDownloadTask extends AsyncTask<String, Void,
            List<CanceledClassDetails>> {

        private final String INNER_TAG = "LPx3-XmlDownloadTask";

        @Override
        protected List<CanceledClassDetails> doInBackground(String... urls) {
            Log.d(INNER_TAG, "doInBackground");
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException ioe) {
                Toast.makeText(context, getResources().getString(R.string.connection_error),
                        Toast.LENGTH_LONG).show();
                return null;
            } catch (XmlPullParserException xppe) {
                Toast.makeText(context, getResources().getString(R.string.xml_error),
                        Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<CanceledClassDetails> result) {
            Log.d(INNER_TAG, "onPostExecute");
            populateListView(result);

        }

        private List<CanceledClassDetails> loadXmlFromNetwork(String url)
                throws XmlPullParserException, IOException{
            Log.d(INNER_TAG, "loadXmlFromNetwork");
            InputStream stream = null;
            DawsonRssXmlParser parser = new DawsonRssXmlParser();
            List<CanceledClassDetails> classes = null;

            try {
                stream = downloadUrl(url);
                classes = parser.parse(stream);
                Log.d(INNER_TAG, "XML parsed.");
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return classes;
        }

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
