package com.dawsonlpx3.friends_in_course_feature;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.dawsonlpx3.friendBreak_feature.FriendBreakFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 * @author Philippe Langlois
 * @author Pengkim Sy
 */

public class FriendInCourseFragment extends Fragment {

    private final String TAG = "LPx3-FriendInCourse";
    private onFriendSelectListener listener;
    private JSONArray jsonResponse = null;
    private List<String> friendNames;
    private ArrayAdapter<String> itemsAdapter;
    private String email;
    private String password;

    public interface onFriendSelectListener {
        void onFriendSelected(String email, String name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getActivity().getSharedPreferences("userInfo",
                Context.MODE_PRIVATE);
        email = prefs.getString("email", "");
        password = prefs.getString("password", "");
        Log.d(TAG, "Retrieved email and password from SharedPrefs: " + email + ", " + password);
    }

    private boolean validateJson() {
        if (jsonResponse == null) {
            Log.e(TAG, "null JSON response");
            displayMessage(getResources().getString(R.string.problem_retrieving_friends));
            return true;
        }

        if (jsonResponse.length() == 0) {
            Log.d(TAG, "no friends found");
            displayMessage(getResources().getString(R.string.no_friends_cancelled));
            return true;
        }

        if (jsonResponse.length() == 1) {
            String errorMessage = null;
            try {
                errorMessage = jsonResponse.getJSONObject(0).getString("error");
                Log.d(TAG, "Error message found: " + errorMessage);
                displayMessage(getResources().getString(R.string.invalid_creds));
                return true;
            } catch (JSONException je) {
                Log.d(TAG, "no error found, proceeding as normal");
                return false;
            }
        }
        return false;
    }

    private void buildFriendsNames() throws JSONException {
        friendNames = new ArrayList<>();
        for (int i = 0; i < jsonResponse.length(); i++) {
            friendNames.add(jsonResponse.getJSONObject(i).getString("firstname")
                    + " "
                    + jsonResponse.getJSONObject(i).getString("lastname"));
            Log.d(TAG, "buildFriendsNames: " + friendNames.get(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_class, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onFriendSelectListener) {
            this.listener = (onFriendSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onFriendSelectedListener");
        }
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated");
        startFindFriendsTask();

        ListView listView = (ListView) view.findViewById(R.id.friend_cancel_LV);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    listener.onFriendSelected(jsonResponse.getJSONObject(position)
                            .getString("email"), friendNames.get(position));
                } catch (JSONException je) {
                    Log.e(TAG, "error getting email for onClick friend: " + Log.getStackTraceString(je));
                    displayMessage(getResources().getString(R.string.problem_retrieving_location));
                }
            }
        });
    }

}
