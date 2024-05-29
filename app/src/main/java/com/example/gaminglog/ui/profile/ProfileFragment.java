package com.example.gaminglog.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.gaminglog.MainActivity;
import com.example.gaminglog.R;
import com.example.gaminglog.SettingsActivity;
import com.example.gaminglog.data.UserDataManager;
import com.example.gaminglog.account.UserManager;

public class ProfileFragment extends Fragment {
    private TextView usernameTextView;
    private Button settingsButton;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        settingsButton = view.findViewById(R.id.settingsButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        settingsButton.setBackgroundColor(Color.parseColor("#EEEEEE"));

        UserManager userManager = UserManager.getInstance();
        int userId = userManager.getUserId();

        UserDataManager userDataManager = new UserDataManager(getContext());
        String username = userDataManager.getUsername(userId);

        if (username != null) {
            usernameTextView.setText("Logged in as: "+username);
        } else {
            usernameTextView.setText("User not found");
        }

        // start settings activity if button pressed
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        // log user out if log out pressed
        logoutButton.setOnClickListener(v -> {
            userManager.clearUser();
            Log.d("ProfileFragment", "User logged out.");
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}
