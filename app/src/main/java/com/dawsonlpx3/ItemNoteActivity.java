package com.dawsonlpx3;

import android.app.Fragment;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dawsonlpx3.db.NotesContract;
import com.dawsonlpx3.db.SQLiteManagerUtil;

public class ItemNoteActivity extends Fragment {

    private final String TAG = "ItemNoteActivity";
    private int id = 0;
    private TextView noteTextView;

    private SQLiteManagerUtil dbh;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbh = SQLiteManagerUtil.getDbManager(this.getActivity());
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                id = getArguments().getInt("id", 0);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_item_note, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated");

        noteTextView = (TextView) view.findViewById(R.id.noteDetailTextView);
        Cursor cursor = dbh.getNoteById(id);
        String noteDetail = "";
        if(cursor.moveToFirst())
            noteDetail = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COL_TASK_NOTES));

        noteTextView.setText(noteDetail);
    }

    // Activity is calling this to update view on Fragment
    public void updateView(int id){
        Cursor cursor = dbh.getNoteById(id);
        String noteDetail = cursor.getString(1); // second column is note

        noteTextView.setText(noteDetail);
    }
}
