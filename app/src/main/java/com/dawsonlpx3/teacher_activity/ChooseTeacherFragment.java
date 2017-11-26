package com.dawsonlpx3.teacher_activity;

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

public class ChooseTeacherFragment extends Fragment {

    private final String TAG = "Dawson-ChooseTeacher";
    private List<TeacherDetails> teachers;
    private OnTeacherSelectedListener listener;
    private ArrayAdapter<String> itemsAdapter;
    private List<String> namesList;
    private String[] namesArr;

    public interface OnTeacherSelectedListener {
        void onTeacherSelected(TeacherDetails teacher);
    }

    public void setTeachersList(List<TeacherDetails> teachers){
        Log.d(TAG, "setting teachers list into an array");
        this.teachers = teachers;
        buildTeacherNamesArr();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.d(TAG, "Restoring state from bundle");
            int size = savedInstanceState.getInt("size");
            List<TeacherDetails> teachersCopy = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TeacherDetails td = ( TeacherDetails) savedInstanceState.getSerializable("teachers_" + i);
                teachersCopy.add(td);
            }
            setTeachersList(teachersCopy);
        }
        this.itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, namesArr);
    }

    private void buildTeacherNamesArr(){
        Log.d(TAG, "buildTeacherNamesArr started");
        this.namesList = new ArrayList<>();
        for(int i = 0; i < this.teachers.size(); i++){
            namesList.add(this.teachers.get(i).getFull_name());
        }

        this.namesArr = namesList.toArray(new String[0]);
    }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView started");
        return inflater.inflate(R.layout.activity_choose_teacher, container, false);
    }

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
