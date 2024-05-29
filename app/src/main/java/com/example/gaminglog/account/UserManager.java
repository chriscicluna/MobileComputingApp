package com.example.gaminglog.account;

import android.util.Log;

public class UserManager {
    private static UserManager instance;
    private int userId = -1;

    private UserManager() {}

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setUser(int userId) {
        this.userId = userId;
        Log.d("UserManager", "User ID set to: " + userId);
    }

    public int getUserId() {
        return userId;
    }

    public void clearUser() {
        userId = -1;
    }


    public void onLoginSuccess(int userId) {
        UserManager.getInstance().setUser(userId);
        Log.d("LoginActivity", "Login success with user ID: " + userId);
    }
}
