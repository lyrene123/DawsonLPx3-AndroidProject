package com.dawsonlpx3.canceled_class_feature;

import android.util.Log;
import android.util.Xml;

import com.dawsonlpx3.data.CanceledClassDetails;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The DawsonRssXmlParser class represents the logic needed to parse the XML retrieved from the
 * Dawson College RSS feed of cancelled classes, and store the data in a list of
 * CanceledClassDetails objects.
 *
 * Code adapted from the "Parsing XML Data" tutorial on the Android Developers website:
 * https://developer.android.com/training/basics/network-ops/xml.html
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 * @author Phil Langlois
 * @author Pengkim Sy
 */

public class DawsonRssXmlParser {
    private static final String ns = null;
    private final String TAG = "LPx3-XmlParser";

    /**
     * Given an InputStream containing the XML data from a web page, parses the XML and stores the
     * relevant data into a list of CanceledClassDetails objects.
     *
     * @param in An InputStream, containing the XML data downloaded from a site.
     * @return  A List of CanceledClassDetails objects, populated using the parsed XML data.
     * @throws XmlPullParserException   if a problem occurs while parsing the XML.
     * @throws IOException  if a problem occurs with the network connection.
     */
    public List<CanceledClassDetails> parse(InputStream in)
            throws XmlPullParserException, IOException {
        Log.d(TAG, "parse");
        try {
            //use XmlPullParser to parse XML
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            Log.d(TAG, "Attempting to read feed...");
            //read XML using the XmlPullParser
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Reads through the elements of an XML file, and stores all data relevant to class
     * cancellations in a list.
     *
     * @param parser The XMlPullParser associated with the XML file.
     * @return  A List of CanceledClassDetails objects.
     * @throws XmlPullParserException   if a problem occurs while parsing the XML.
     * @throws IOException  if a problem occurs with the network connection.
     */
    private List<CanceledClassDetails> readFeed(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        Log.d(TAG, "readFeed");
        List<CanceledClassDetails> classes = new ArrayList<CanceledClassDetails>();
        //define the start tag of the XML file; in this case, "rss"
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        //continue parsing until the end tag is reached
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("item")) {
                //cancelled class information stored in "item" tags
                Log.d(TAG, "Parsing tag " + name);
                classes.add(readItem(parser));
            } else if (name.equals("channel")) {
                //"channel" tag contains all other tags, aside from "rss" tag
                //"channel" tag MUST NOT BE SKIPPED, or else all tags inside it are ignored
                Log.d(TAG, "Entering tag " + name);
                parser.next();
            } else {
                //all other tags can be ignored
                Log.d(TAG, "Skipping tag " + name);
                skip(parser);
            }
        }
        return classes;
    }

    /**
     * Parses the data inside an "item" tag in the XML file, and stores it in a
     * CanceledClassDetails object.
     *
     * @param parser    The XmlPullParser associated with the XML file.
     * @return  A CanceledClassDetails object, containing information about a cancelled class.
     * @throws XmlPullParserException   if a problem occurs while parsing the XML.
     * @throws IOException  if a problem occurs with the network connection.
     */
    private CanceledClassDetails readItem(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        //define the start and end tags to work with; in this case, "item"
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String course = null;
        String teacher = null;
        String dateCancelled = null;
        String notes = null;
        //continue parsing until the end tag is reached
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(TAG, "Found tag " + name);
            switch (name) {
                case "title":
                    title = readText(parser);
                    break;
                case "course":
                    course = readText(parser);
                    break;
                case "teacher":
                    teacher = readText(parser);
                    break;
                case "pubDate":
                    //pubDate is chosen over datecancelled because it also displays time
                    dateCancelled = readText(parser);
                    break;
                case "notes":
                    notes = readText(parser);
                    break;
                default:
                    //other tags do not need to be saved for our purposes, can be skipped
                    skip(parser);
                    break;
            }
        }
        return new CanceledClassDetails(title, course, teacher, dateCancelled, notes);
    }

    /**
     * Reads and returns the text in a single XML tag.
     *
     * @param parser    The XmlPullParser associated with the XML file.
     * @return  The text retrieved from the tag.
     * @throws IOException  if a problem occurs with the network connection.
     * @throws XmlPullParserException   if a problem occurs while parsing the XML.
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        //default string in case tag is empty
        String result = "No data available.";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        Log.d(TAG, "tag contained: " + result);
        return result;
    }

    /**
     * Skips over an XML tag and all its contents, rather than parsing it and examining its
     * contents.
     *
     * @param parser    The XmlPullParser associated with the XML file.
     * @throws XmlPullParserException   if a problem occurs while parsing the XML.
     * @throws IOException  if a problem occurs with the network connection.
     */
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
