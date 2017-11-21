package com.dawsonlpx3.db;


import android.provider.BaseColumns;

public class NotesContract {
    public static final String DB_NAME = "com.dawsonlpx3.db";
    public static final int DB_VERSION = 1;

    public class NotesEntry implements BaseColumns {
        public static final String TABLE = "notes";
        public static final String COL_ID = "_id";
        public static final String COL_TASK_NOTES = "fullnotes";
    }
}
