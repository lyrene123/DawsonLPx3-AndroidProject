package com.dawsonlpx3.notes_feature;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dawsonlpx3.R;
import com.dawsonlpx3.db.NotesContract;
import com.dawsonlpx3.db.SQLiteManagerUtil;

/**
 * Fragment activity which handles the display and the user interaction with a single note of the user
 * retrieved from the list in the NotesFragment activity. Receives the id of the specific note from the
 * arguments of the fragment and retrieves that specific note by id in the SQLite database in a separate thread.
 * Once the whole note record is retrieved from the database, displays the string content into the view.
 *
 * @author Lyrene Labor
 * @autor Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefgleur
 */
public class ItemNoteFragment extends Fragment {

    private final String TAG = "ItemNoteFragment";
    private int id = 0;
    private TextView noteTextView;

    private SQLiteManagerUtil dbh;

    /**
     * Initialize the database helper,
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbh = SQLiteManagerUtil.getDbManager(this.getActivity());
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                id = getArguments().getInt("id", 0);
            }
        } else {
            id = savedInstanceState.getInt("id");
        }
    }

    /**
     * Inflate the activity_item_note
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_item_note, container, false);
    }

    /**
     * When view is created, get the note detail in the background thread and show it once it's
     * done.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated");

        String noteDetail = "";

        noteTextView = (TextView) view.findViewById(R.id.noteDetailTextView);

        if(savedInstanceState != null) {
            Log.d(TAG, "restoring note");
            noteDetail = savedInstanceState.getString("noteDetail");
        }

        GetNoteDetailTask GetNoteDetailTask = new GetNoteDetailTask();
        GetNoteDetailTask.execute(id);

        noteTextView.setText(noteDetail);
    }

    /**
     * Save the noteDetail, so when rotating, it still shows the currect note detail
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(noteTextView != null)
            outState.putString("noteDetail", noteTextView.getText().toString());
        outState.putInt("id", this.id);
    }

    /**
     * Using the background thread to get the noteDetail by the id
     * that was passed in the execute() method
     */
    private class GetNoteDetailTask extends AsyncTask<Integer, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(ItemNoteFragment.this.getActivity());

        /**
         * This will find the noteDetail by id that is passed in the parameter of execute
         * method.
         *
         * @param args - the first argument must be id, so it can be used to find the
         *             its noteDetail
         * @return noteDetail
         */
        protected String doInBackground(Integer... args) {
            Log.i(TAG, "doInBackground of GetNoteDetailTask");
            String noteDetail = "";
            Cursor cursor = dbh.getNoteById(args[0]);
            if(cursor.moveToFirst())
                noteDetail = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COL_TASK_NOTES));

            return noteDetail;
        }

        /**
         * When the operation in doInBackground is done, set the noteTextView to
         * the noteDetail that is in the parameter.
         *
         * @param noteDetail - This will be passed by the return of doInBackground
         */
        protected void onPostExecute(String noteDetail) {
            Log.i(TAG, "onPostExecute of GetNoteDetailTask");
            noteTextView.setText(noteDetail);
        }
    }
}
