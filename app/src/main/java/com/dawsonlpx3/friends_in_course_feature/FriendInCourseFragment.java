package com.dawsonlpx3.friends_in_course_feature;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import com.dawsonlpx3.async_utils.IsFriendInCourseAsyncTask;
import com.dawsonlpx3.friendBreak_feature.FriendBreakFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    private JSONArray jsonResponse = null;
    private List<String> friendNames;
    private List<String> friendEmails;
    private ArrayAdapter<String> itemsAdapter;
    private String email;
    private String password;

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

    private void buildFriendsNamesAndEmail() throws JSONException {
        friendNames = new ArrayList<>();
        friendEmails = new ArrayList<>();
        for (int i = 0; i < jsonResponse.length(); i++) {
            friendNames.add(jsonResponse.getJSONObject(i).getString("firstname")
                    + " "
                    + jsonResponse.getJSONObject(i).getString("lastname"));
            friendEmails.add(jsonResponse.getJSONObject(i).getString("email"));
            Log.d(TAG, "friend name: " + friendNames.get(i));
            Log.d(TAG, "friend email: " + friendEmails.get(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_class, container, false);
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
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{friendEmails.get(position)});
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.from));
                startActivity(intent);
            }
        });
    }

    private void startFindFriendsTask() {
        IsFriendInCourseAsyncTask task = new IsFriendInCourseAsyncTask();
        task.execute(email, password); //need to have course info as well

        try {
            jsonResponse = task.get();
            Log.d(TAG, "jsonResponse: " + jsonResponse.toString());

            if (!validateJson()) {
                buildFriendsNamesAndEmail();
                this.itemsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
                        friendNames.toArray(new String[0]));
            }
        } catch (InterruptedException ie) {
            Log.e(TAG, "startFindFriendsTask: InterruptedException "
                    + Log.getStackTraceString(ie));
        } catch (ExecutionException ee) {
            Log.e(TAG, "startFindFriendTask: ExecutionException "
                    + Log.getStackTraceString(ee));
        } catch (JSONException je) {
            Log.e(TAG, "startFindFriendTask: JSONException " + Log.getStackTraceString(je));
        }
    }

    private void displayMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle(R.string.warning)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
