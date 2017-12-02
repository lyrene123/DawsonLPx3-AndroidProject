package com.dawsonlpx3.find_friends_feature;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dawsonlpx3.R;
import com.dawsonlpx3.async_utils.AllFriendsAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class AllFriendsFragment extends Fragment {

    private final String TAG = "Dawson-AllFriendsFrag";
    private onFriendSelectedListener listener;
    private JSONArray jsonResponse = null;
    private List<String> friends_names;
    private ArrayAdapter<String> itemsAdapter;

    public interface onFriendSelectedListener {
        void onFriendSelected(String friendemail, String name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");
        Log.d(TAG, "Retrieved email and passwd from SharedPref: " + email + " " + password);

        AllFriendsAsyncTask allFriendsAsyncTask = new AllFriendsAsyncTask();
        allFriendsAsyncTask.execute(email, password);
        try {
            jsonResponse = allFriendsAsyncTask.get();
            Log.d(TAG, "jsonResponse: " + jsonResponse.toString());
            if(!checkForErrorsOrNoFriends()) {
                buildFriendsNames();
                this.itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friends_names.toArray(new String[0]));
            }
        } catch (InterruptedException | ExecutionException | JSONException e ) {
            //TODO display a popup dialog for problem retrieving friends
            Log.e(TAG, "API Error getting friends: " + Log.getStackTraceString(e));
        }
    }

    private boolean checkForErrorsOrNoFriends() {
        if(jsonResponse == null){
            //TODO display a popup dialog for problem retrieving friends
            Log.d(TAG, "Null jsonResponse");
            return true;
        }

        if(jsonResponse.length() == 0){
            //TODO display a popup dialog for problem retrieving friends
            Log.d(TAG, "no friends found");
            return true;
        }

        if(jsonResponse.length() == 1) {
            String errorMsg = null;
            try {
                errorMsg = jsonResponse.getJSONObject(0).getString("error");
                //TODO display a popup dialog with error message
                Log.d(TAG, "Error message found: " + errorMsg);
                return true;
            } catch (JSONException e) {
                Log.d(TAG, "No error message found");
                return false;
            }
        }
        return false;
    }

    private void buildFriendsNames() throws JSONException {
        friends_names = new ArrayList<>();
        for(int i = 0; i < jsonResponse.length(); i++){
            friends_names.add(jsonResponse.getJSONObject(i).getString("firstname") +
                                    " " + jsonResponse.getJSONObject(i).getString("lastname"));
            Log.d(TAG, "buildFriendsNames: " + friends_names.get(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_friends, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onFriendSelectedListener) {
            this.listener = (onFriendSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onFriendSelectedListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated started");

        ListView listView = (ListView) view.findViewById(R.id.allfriendsLV);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    listener.onFriendSelected(jsonResponse.getJSONObject(position).getString("email"),
                            friends_names.get(position));
                } catch (JSONException e) {
                    Log.e(TAG, "Error getting email for onClick friend: " + Log.getStackTraceString(e));
                    //TODO POP DIALOG
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }
}
