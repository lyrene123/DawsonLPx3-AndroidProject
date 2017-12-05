package com.dawsonlpx3.friends_in_course_feature;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dawsonlpx3.R;
import com.dawsonlpx3.friendBreak_feature.FriendBreakFragment;

/**
 *
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 * @author Philippe Langlois
 * @author Pengkim Sy
 */

public class FriendInCourseFragment extends Fragment {

    public FriendInCourseFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_class, container, false);
    }

}
