package com.dawsonlpx3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * TODO
 */
public class SQLiteManagerUtil extends SQLiteOpenHelper {

    // static instance to share SQLiteManagerUtil
    private static SQLiteManagerUtil dbmanager = null;

    private final String TAG = "DawsonLPx3-SQLiteMan";

    private SQLiteManagerUtil(Context context) {
        super(context, NotesContract.DB_NAME, null, NotesContract.DB_VERSION);
    }

    public static SQLiteManagerUtil getDbManager(Context context) {
        if (dbmanager == null) {
            dbmanager = new SQLiteManagerUtil(context.getApplicationContext());
        }
        return dbmanager;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            String createTable = "CREATE TABLE " + NotesContract.NotesEntry.TABLE + " ( " +
                    NotesContract.NotesEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NotesContract.NotesEntry.COL_TASK_NOTES + " TEXT NOT NULL);";
            sqLiteDatabase.execSQL(createTable);
        } 	catch (SQLException e) {
            Log.e(TAG, "CREATE exception"+Log.getStackTraceString(e));
            throw e;
        }
    }

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

    public long insertNewNote(String note){
        ContentValues cv = new ContentValues();
        cv.put(NotesContract.NotesEntry.COL_TASK_NOTES, note);

        long code = getWritableDatabase().insert(NotesContract.NotesEntry.TABLE, null, cv);
        return code;
    }

    public int deleteNote(int id){
        return getWritableDatabase().delete(NotesContract.NotesEntry.TABLE,
                NotesContract.NotesEntry._ID + "=?",
                new String[] { String.valueOf(id) });
    }

    public Cursor getNotes(){
        return getReadableDatabase().query(NotesContract.NotesEntry.TABLE, null, null, null,
                null, null, null);
    }

    public int updateNote(int id, String newNote){
        ContentValues cv = new ContentValues();
        cv.put(NotesContract.NotesEntry.COL_TASK_NOTES, newNote);

        return getWritableDatabase().update(NotesContract.NotesEntry.TABLE, cv,
                NotesContract.NotesEntry._ID + "=?",
                new String[] { String.valueOf(id) });
    }
}
