package com.dawsonlpx3.find_teacher_feature;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dawsonlpx3.R;
import com.dawsonlpx3.data.TeacherDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment activity that handles the display of the teacher search results done by
 * the user. Receives a list of teachers and displays the full names of each teacher
 * matching the search criteria into a list view in the fragment. Handles attaching the
 * click event of each item of the list.
 *
 * @author Lyrene Labor
 * @author Pengkim Sy
 * @author Phil Langlois
 * @author Peter Bellefleur
 */
public class ChooseTeacherFragment extends Fragment {

    private final String TAG = "Dawson-ChooseTeacher";
    private List<TeacherDetails> teachers;
    private OnTeacherSelectedListener listener;
    private ArrayAdapter<String> itemsAdapter;
    private List<String> namesList;
    private String[] namesArr;

    /**
     * Interface that must be implemented by any activity that is inflating this fragment.
     * This interface is used as an event handler for any item clicked from the list view
     * inside this fragment.
     */
    public interface OnTeacherSelectedListener {
        void onTeacherSelected(TeacherDetails teacher);
    }

    /**
     * Sets the list of TeacherDetail objects to be used for the list view.
     *
     * @param teachers List of TeacherDetail objects
     */
    public void setTeachersList(List<TeacherDetails> teachers){
        Log.d(TAG, "setting teachers list into an array");
        this.teachers = teachers;
        buildTeacherNamesArr();
    }

    /**
     * Instantiates the adapter of the list view and if applicable, restore the list of
     * TeacherDetails objects from the Bundle.
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.d(TAG, "Restoring teacher list from bundle");
            int size = savedInstanceState.getInt("size");
            List<TeacherDetails> teachersCopy = new ArrayList<>();
            //retrieve all teacher records from the Bundle and store into a list
            for (int i = 0; i < size; i++) {
                TeacherDetails td = ( TeacherDetails) savedInstanceState.getSerializable("teachers_" + i);
                teachersCopy.add(td);
            }
            setTeachersList(teachersCopy);
        }
        this.itemsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, namesArr);
    }

    /**
     * Builds an array of strings containing the full names of each teacher in the teachers list.
     */
    private void buildTeacherNamesArr(){
        Log.d(TAG, "buildTeacherNamesArr started");
        this.namesList = new ArrayList<>();
        for(int i = 0; i < this.teachers.size(); i++){
            namesList.add(this.teachers.get(i).getFull_name());
        }
        this.namesArr = namesList.toArray(new String[0]);
    }

    /**
     * Verifies if the current context of the fragment is implementing the  OnTeacherSelectedListener
     * interface and if not, throws an exception.
     *
     * @param context Context of fragment
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.listener = (OnTeacherSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTeacherSelectedListener");
        }
    }

    /**
     * Inflates the layout into the current fragment and returns the view containing the fragment
     *
     * @param inflater LayoutInflater object
     * @param container ViewGroup object
     * @param savedInstanceState Bundle object
     * @return View containing the fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView started");
        return inflater.inflate(R.layout.activity_choose_teacher, container, false);
    }

    /**
     * Instantiates the ListView of the fragment with the array of string of teacher full names.
     * Sets an onclick event on each item of the list which will invoke the onTeacherSelected
     * method from the OnTeacherSelectedListener interface.
     *
     * @param view View containing the fragment
     * @param savedInstanceState Bundle object
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated started");

        ListView listView = (ListView) view.findViewById(R.id.teachersLV);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onTeacherSelected(teachers.get(position));
            }
        });
    }

    /**
     * Saves the current state of the fragment by saving each item of the list
     * and the total count of items in the list.
     *
     * @param outState Bundle object
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState started");
        //loop through teachers list, save each teacher and their order
        for (int i = 0; i < this.teachers.size(); i++) {
            outState.putSerializable("teachers_" + i, this.teachers.get(i));
        }
        outState.putInt("size", teachers.size());
    }
}
