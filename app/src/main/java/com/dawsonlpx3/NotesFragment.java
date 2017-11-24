package com.dawsonlpx3;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.dawsonlpx3.db.NotesContract;
import com.dawsonlpx3.db.SQLiteManagerUtil;

public class NotesFragment extends Fragment {

    private final String TAG = "NoteActivity";

    private SQLiteManagerUtil dbh;
    private Cursor cursor;
    private SimpleCursorAdapter sca;

    private OnNoteSelectedListener listener;

    public interface OnNoteSelectedListener {
        void onNoteSelected(int id);
    }

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh = SQLiteManagerUtil.getDbManager(this.getActivity());
//        dbh.deleteAll(); // Must delete line
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_notes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated");
        String[] from = { NotesContract.NotesEntry.COL_TASK_NOTES };
        int[] to = { R.id.noteListItem };

        cursor = dbh.getNotes();
        ListView listView = (ListView) view.findViewById(R.id.noteListView);
        sca = new SimpleCursorAdapter(this.getActivity(), R.layout.note_list_item
                , cursor, from, to, 0);
        listView.setAdapter(sca);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "Note list item clicked");

                Cursor temp = (Cursor) adapterView.getItemAtPosition(position);
                listener.onNoteSelected(temp.getInt(temp.getColumnIndex(NotesContract.NotesEntry.COL_ID))); // first column is id
            }
        });

        final EditText noteEditText = (EditText) view.findViewById(R.id.noteEditText);
        Button addBtn = (Button) view.findViewById(R.id.addNoteButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Add Note Button Clicked");
                String note = noteEditText.getText().toString();
                if(!note.isEmpty()) {
                    dbh.insertNewNote(note);
                    refreshView();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        dbh.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    private void refreshView() {
        cursor = dbh.getNotes();
        sca.changeCursor(cursor);
        sca.notifyDataSetChanged();
    }
}