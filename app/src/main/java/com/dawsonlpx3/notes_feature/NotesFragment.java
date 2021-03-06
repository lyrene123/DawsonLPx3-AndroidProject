package com.dawsonlpx3.notes_feature;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.dawsonlpx3.R;
import com.dawsonlpx3.db.NotesContract;
import com.dawsonlpx3.db.SQLiteManagerUtil;

/**
 * This NotesFragement will show a ListView of all the notes. Handles the retrieval of all notes of
 * the user from the SQLite database in a separate thread. Attaches a click listeners on each item
 * of the notes list in order to inflate the ItemNoteFragment with the selected note.
 *
 * @author Lyrene Labor
 * @autor Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefgleur
 */
public class NotesFragment extends Fragment {

    private final String TAG = "NoteActivity";

    private SQLiteManagerUtil dbh;
    private Cursor cursor;
    private SimpleCursorAdapter sca;
    private GetNotesTask getData;

    private OnNoteSelectedListener listener;

    /**
     * This will be implemented by the activity that show this fragment,
     * so it can handle the what happen when the list item is pressed/selected.
     */
    public interface OnNoteSelectedListener {
        void onNoteSelected(int id);
    }

    /**
     * When this fragment is attached, assign the OnNoteSelectedListener
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.listener = (OnNoteSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnNoteSelectedListener");
        }
    }

    /**
     * Initialize the database helper whech this method get called by the
     * fragement lifecycle.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh = SQLiteManagerUtil.getDbManager(this.getActivity());
//        dbh.deleteAll(); // Must delete this line or use this line to delete all notes in your device
    }

    /**
     * Inflate the activity_notes view when this method get called by the Fragement
     * lifecycle.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_notes, container, false);
    }

    /**
     * When the view is created, display all the notes in a ListView and set its
     * click listener, so it can show its note detail be clicked/pressed. The addNoteButton
     * will be added a click listener to insert the text in the noteEditView into the
     * sqlite database.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated");
        String[] from = { NotesContract.NotesEntry.COL_TASK_NOTES };
        int[] to = { R.id.noteListItem };

        cursor = dbh.getNotes();
        ListView listView = (ListView) view.findViewById(R.id.noteListView);
        sca = new SimpleCursorAdapter(this.getActivity(), R.layout.list_item
                , cursor, from, to, 0);
        listView.setAdapter(sca);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "Note list item clicked");

                Cursor temp = (Cursor) adapterView.getItemAtPosition(position);
                listener.onNoteSelected(temp.getInt(temp.getColumnIndex(NotesContract.NotesEntry.COL_ID)));
            }
        });

        // When the list item is long pressed, pop up a dialog to ask if the user want to delete
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "Note list item long pressed");
                final Cursor temp = (Cursor) adapterView.getItemAtPosition(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(getResources().getString(R.string.delete));
                alert.setMessage(getResources().getString(R.string.deleteQuestion));
                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbh.deleteNote(temp.getInt(temp.getColumnIndex(NotesContract.NotesEntry.COL_ID)));
                        refreshView();
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                return true;
            }
        });

        refreshView();

        final EditText noteEditText = (EditText) view.findViewById(R.id.noteEditText);
        Button addBtn = (Button) view.findViewById(R.id.addNoteButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Add Note Button Clicked");
                String note = noteEditText.getText().toString();
                if(!note.isEmpty()) {
                    noteEditText.setText("");
                    InsertNoteTask insertNote = new InsertNoteTask();
                    insertNote.execute(note);
                    refreshView();
                }
            }
        });
    }

    /**
     * Close the Database Helper when paused
     */
    @Override
    public void onPause() {
        super.onPause();
        dbh.close();
    }

    /**
     * When rotating, call refreshView() so that it will displays the NoteList in
     * the ListView
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    /**
     * Create a background thread to get all the notes and update adapter
     */
    private void refreshView() {
        getData = new GetNotesTask();
        getData.execute();
    }

    /**
     * Get the all notes in the background thread, and the operation is done,
     * change the cursor and notify the adapter.
     */
    private class GetNotesTask extends AsyncTask<Void, Void, Cursor> {
        private final ProgressDialog dialog = new ProgressDialog(NotesFragment.this.getActivity());

        /**
         * Get all the notes in the sqlite database.
         *
         * @param args - nothing should be passed into execute()
         * @return Cursor - this will be passed to the parameter in onPostExecute().
         */
        protected Cursor doInBackground(Void... args) {
            Log.i(TAG, "doInBackground of GetNotesTask");
            return dbh.getNotes();
        }

        /**
         * Change the adapter cursor, and notify data changed
         *
         * @param cursor - when doInBackground is done, this cursor will have the
         *               return of doInBackground method
         */
        protected void onPostExecute(Cursor cursor) {
            Log.i(TAG, "Post execute of GetNoteTask");
            sca.changeCursor(cursor);
            sca.notifyDataSetChanged();
        }
    } // GetNotesTask class (AsyncTask)

    /**
     * Create a background thread that do the insertion of note into the sqlite databse.
     * Accepting a string argument (note), so it can be inserted into the sqlite.
     */
    private class InsertNoteTask extends AsyncTask<String, Void, Long> {
        private final ProgressDialog dialog = new ProgressDialog(NotesFragment.this.getActivity());

        /**
         * The first argument must be a string (note). This note will be inserted
         * into the sqlite database.
         *
         * @param args - array of string
         * @return
         */
        protected Long doInBackground(String... args) {
            Log.d(TAG, "doInBackground of InsertNoteTask");
            return dbh.insertNewNote(args[0]);
        }
    } // InsertNoteTask class (AsyncTask)
}