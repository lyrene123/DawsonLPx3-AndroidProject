package com.dawsonlpx3.rss_parser;

import android.util.Xml;

import com.dawsonlpx3.data.CanceledClassDetails;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1132371 on 11/27/2017.
 */

public class DawsonRssXmlParser {
    private static final String ns = null;

    public List<CanceledClassDetails> parse(InputStream in)
            throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<CanceledClassDetails> readFeed(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        List<CanceledClassDetails> classes = new ArrayList<CanceledClassDetails>();

        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("item")) {
                classes.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return classes;
    }

    private CanceledClassDetails readEntry(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        String title = null;
        String course = null;
        String teacher = null;
        String dateCancelled = null;
        String notes = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readText(parser);
            } else if (name.equals("course")) {
                course = readText(parser);
            } else if (name.equals("teacher")) {
                teacher = readText(parser);
            } else if (name.equals("datecancelled")) {
                dateCancelled = readText(parser);
            } else if (name.equals("notes")) {
                notes = readText(parser);
            } else {
                skip(parser);
            }
        }
        return new CanceledClassDetails(title, course, teacher, dateCancelled, notes);
    }

    //private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
    //    String title = readText(parser);
    //    return title;
    //}

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "No data available.";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
