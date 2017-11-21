package com.dawsonlpx3.db;


import android.provider.BaseColumns;

/**
 * Encapsulates the necessary constants needed to access the SQLite database.
 * Contains the constants that represent a notes record in a database as well.
 *
 * This model class based on:
 * https://www.sitepoint.com/starting-android-development-creating-todo-app/
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Peter Bellefleur
 * @author Phil Langlois
 */
public class NotesContract {
    public static final String DB_NAME = "com.dawsonlpx3.db";
    public static final int DB_VERSION = 1;

    /**
     * Encapsulates the columns of a notes record from the SQLite database.
     */
    public class NotesEntry implements BaseColumns {
        public static final String TABLE = "notes";
        public static final String COL_ID = "_id";
        public static final String COL_TASK_NOTES = "fullnotes";
    }
}
