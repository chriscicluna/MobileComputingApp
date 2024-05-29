package com.example.gaminglog.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDataManager {
    private UserDbHelper dbHelper;

    public UserDataManager(Context context) {
        dbHelper = new UserDbHelper(context);
    }

    public long addUser(String username, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME_USERNAME, username);
        values.put(UserContract.UserEntry.COLUMN_NAME_EMAIL, email);
        values.put(UserContract.UserEntry.COLUMN_NAME_PASSWORD, password);
        long newRowId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
        return newRowId;
    }

    @SuppressLint("Range")
    public int checkUser(String email, String password) {
        int userId = -1;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                UserContract.UserEntry._ID,
                UserContract.UserEntry.COLUMN_NAME_EMAIL,
                UserContract.UserEntry.COLUMN_NAME_PASSWORD
        };

        String selection = UserContract.UserEntry.COLUMN_NAME_EMAIL + " = ? AND " +
                UserContract.UserEntry.COLUMN_NAME_PASSWORD + " = ?";
        String[] selectionArgs = { email, password };

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry._ID));
        }
        cursor.close();
        db.close();

        return userId;
    }

    @SuppressLint("Range")
    public String getUsername(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String username = null;

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                new String[] {UserContract.UserEntry.COLUMN_NAME_USERNAME},
                UserContract.UserEntry._ID + " = ?",
                new String[] {String.valueOf(userId)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_USERNAME));
        }
        cursor.close();
        db.close();
        return username;
    }
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                UserContract.UserEntry._ID
        };
        String selection = UserContract.UserEntry.COLUMN_NAME_EMAIL + " = ?";
        String[] selectionArgs = { email };
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                UserContract.UserEntry._ID
        };
        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }
}
