package com.dawsonlpx3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper utility class that handles database access and operations for the SQLite database.
 * Handles basic CRUD operations to create a new notes record, to retrieve and read notes from
 * the database, to update notes records, and to delete notes records.
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Peter Bellefleur
 * @author Phil Langlois.
 */
public class SQLiteManagerUtil extends SQLiteOpenHelper {

    // static instance to share SQLiteManagerUtil
    private static SQLiteManagerUtil dbmanager = null;

    private final String TAG = "DawsonLPx3-SQLiteMan";

    /**
     * Constructor to create an instance of a SQLiteManager database helper class
     * in order to perform basic read, write, update and delete operations on the notes
     * record from the SQLite database. Constructor is private to avoid multiple instance
     * of the SQLiteManager class to be created and to limit to only one instance for the
     * duration of the application lifetime.
     *
     * @param context Application context
     */
    private SQLiteManagerUtil(Context context) {
        super(context, NotesContract.DB_NAME, null, NotesContract.DB_VERSION);
    }

    /**
     * Factory method that will create an instance of the SQLiteManagerUtil class only if
     * an instance hasn't been previously created. If an instance is already created, then
     * return that existing instance.
     *
     * @param context Application Context
     * @return A SQLiteManagerUtil instance
     */
    public static SQLiteManagerUtil getDbManager(Context context) {
        if (dbmanager == null) {
            dbmanager = new SQLiteManagerUtil(context.getApplicationContext());
        }
        return dbmanager;
    }


    /**
     * Callback method that will create an empty database notes table
     *
     * @param sqLiteDatabase SQLiteDatabase instance
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            Log.d(TAG, "onCreate started");
            String createTable = "CREATE TABLE " + NotesContract.NotesEntry.TABLE + " ( " +
                    NotesContract.NotesEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NotesContract.NotesEntry.COL_TASK_NOTES + " TEXT NOT NULL);";
            sqLiteDatabase.execSQL(createTable);
        } 	catch (SQLException e) {
            Log.e(TAG, "CREATE exception"+Log.getStackTraceString(e));
            throw e;
        }
    }

    /**
     * Callback method that will be called when the database is updated into a new
     * version. In this case, the database will be dropped and recreated again which
     * will delete all old data.
     *
     * NOTE : STILL NEED TO VALIDATE WITH TRICIA IF DROPPING TABLE IS OKAY
     *
     * @param sqLiteDatabase SQLiteDatabase instance
     * @param i previous version number
     * @param i1 new previous version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(TAG, "Upgrading database from version "
                + i + " to " + i1
                + ", which will destroy all old data");
        try {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NotesContract.NotesEntry.TABLE);
        } catch (SQLException e) {
            Log.e(TAG, "DROP exception"+Log.getStackTraceString(e));
            throw e;
        }
    }

    /**
     * Inserts a new note record into the database
     *
     * @param note A string note
     * @return the id of the new record
     */
    public long insertNewNote(String note){
        Log.d(TAG, "insertNewNote started");
        ContentValues cv = new ContentValues();
        cv.put(NotesContract.NotesEntry.COL_TASK_NOTES, note);

        long code = getWritableDatabase().insert(NotesContract.NotesEntry.TABLE, null, cv);
        return code;
    }

    /**
     * Deletes a notes record from the database matching the input id
     *
     * @param id the id of the record to delete
     * @return number records affected
     */
    public int deleteNote(int id){
        Log.d(TAG, "deleteNote started");
        return getWritableDatabase().delete(NotesContract.NotesEntry.TABLE,
                NotesContract.NotesEntry._ID + "=?",
                new String[] { String.valueOf(id) });
    }

    /**
     * Returns all notes records from the database.
     *
     * @return A Cursor containing all records from the database.
     */
    public Cursor getNotes(){
        Log.d(TAG, "getNotes started");

        return getReadableDatabase().query(NotesContract.NotesEntry.TABLE, null, null, null,
                null, null, null);
    }

    /**
     * Updates a notes record with the matching input id.
     *
     * @param id the id of the record to update
     * @param newNote The new string note
     * @return the number of records affected
     */
    public int updateNote(int id, String newNote){
        Log.d(TAG, "updateNote started");
        ContentValues cv = new ContentValues();
        cv.put(NotesContract.NotesEntry.COL_TASK_NOTES, newNote);

        return getWritableDatabase().update(NotesContract.NotesEntry.TABLE, cv,
                NotesContract.NotesEntry._ID + "=?",
                new String[] { String.valueOf(id) });
    }
}
