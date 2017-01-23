package com.saket.attendanceplus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HP on 22-01-2017.
 */

public class saketDBhelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Jarvis.db";
    public static final String login_table_name = "loginCredentials",
                                course_table_name = "courseDetails";
    public static final String login_key = "ID",
                                login_name = "Name",
                                login_pass = "Password";
    public static final String course_name = "Name",
                                course_link = "Link";

    public saketDBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + login_table_name + " (" +
                        login_key + " INTEGER PRIMARY KEY," +
                        FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                        FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
