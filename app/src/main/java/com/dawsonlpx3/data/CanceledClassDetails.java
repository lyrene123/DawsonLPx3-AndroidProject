package com.dawsonlpx3.data;

import java.io.Serializable;

/**
 * A CanceledClassDetails object encapsulates the properties of a canceled class, retrieved from
 * the Dawson College website's class cancellations RSS feed.
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 * @author Phil Langlois
 * @author Pengkim Sy
 */

public class CanceledClassDetails implements Serializable {
    private final String title;
    private final String course;
    private final String teacher;
    private final String dateCancelled;
    private final String notes;

    /**
     * Constructor for a CancelledClassDetails class.
     * @param title     The title given to a class cancellation.
     * @param course    The name of a cancelled course.
     * @param teacher   The name of a cancelled course's teacher.
     * @param dateCancelled The date of a class's cancellation.
     * @param notes     Any notes provided about the class cancellation.
     */
    public CanceledClassDetails (String title, String course, String teacher,
                                  String dateCancelled, String notes) {
        this.title = title;
        this.course = course;
        this.teacher = teacher;
        this.dateCancelled = dateCancelled;
        this.notes = notes;
    }

    /**
     *  Returns the title for the class cancellation.
     *
     * @return  The title given to a class cancellation.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the name of the cancelled course.
     *
     * @return The name of a cancelled course.
     */
    public String getCourse() {
        return this.course;
    }

    /**
     * Returns the name of the cancelled course's teacher.
     *
     * @return The name of a cancelled course's teacher.
     */
    public String getTeacher() {
        return this.teacher;
    }

    /**
     * Returns the date of the class cancellation.
     *
     * @return The date of a class's cancellation.
     */
    public String getDateCancelled() {
        return this.dateCancelled;
    }

    /**
     * Returns the notes given about the class cancellation.
     *
     * @return Any notes provided about the class cancellation.
     */
    public String getNotes() {
        return this.notes;
    }

}
