package com.example.gaminglog.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gaminglog.data.UserContract.UserEntry;

import java.util.ArrayList;
import java.util.List;

public class UserDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "User.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    UserEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    UserEntry.COLUMN_NAME_PASSWORD + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    public final class LibraryEntry {
        private LibraryEntry() {}

        // Table name
        public static final String TABLE_NAME = "library";

        // Column names
        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_GAME_ID = "game_id";
    }


    private static final String SQL_CREATE_LIBRARY_ENTRIES =
            "CREATE TABLE " + LibraryEntry.TABLE_NAME + " (" +
                    LibraryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LibraryEntry.COLUMN_USER_ID + " INTEGER," +
                    LibraryEntry.COLUMN_GAME_ID + " INTEGER)";

    private static final String SQL_DELETE_LIBRARY_ENTRIES =
            "DROP TABLE IF EXISTS " + LibraryEntry.TABLE_NAME;

    public void addGameToLibrary(int userId, int gameId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LibraryEntry.COLUMN_USER_ID, userId);
        values.put(LibraryEntry.COLUMN_GAME_ID, gameId);

        db.insert(LibraryEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void removeGameFromLibrary(int userId, int gameId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // remove entry for user id and chosen game id
        String selection = LibraryEntry.COLUMN_USER_ID + "=? AND " + LibraryEntry.COLUMN_GAME_ID + "=?";
        String[] selectionArgs = { String.valueOf(userId), String.valueOf(gameId) };
        db.delete(LibraryEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public boolean isGameInLibrary(int userId, int gameId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + LibraryEntry.TABLE_NAME + " WHERE "
                + LibraryEntry.COLUMN_USER_ID + "=" + userId + " AND "
                + LibraryEntry.COLUMN_GAME_ID + "=" + gameId;
        Cursor cursor = db.rawQuery(query, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public List<Integer> getGamesInLibrary(int userId) {
        List<Integer> gameIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                LibraryEntry.TABLE_NAME,
                new String[]{LibraryEntry.COLUMN_GAME_ID},
                LibraryEntry.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        while (cursor.moveToNext()) {
            @SuppressLint("Range") int gameId = cursor.getInt(cursor.getColumnIndex(LibraryEntry.COLUMN_GAME_ID));
            gameIds.add(gameId);
        }
        cursor.close();
        db.close();
        return gameIds;
    }



    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_LIBRARY_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(SQL_CREATE_LIBRARY_ENTRIES);
        }
    }



    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
