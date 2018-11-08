package com.moraes.igor.zupprocessoseletivo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_MOVIE =
            "CREATE TABLE Movie (" +
                    "imdbID TEXT PRIMARY KEY," +
                    " title TEXT," +
                    " year TEXT," +
                    " rated TEXT," +
                    " released TEXT," +
                    " runtime TEXT," +
                    " genre TEXT," +
                    " director TEXT," +
                    " writer TEXT," +
                    " actors TEXT," +
                    " plot TEXT," +
                    " language TEXT," +
                    " country TEXT," +
                    " awards TEXT," +
                    " poster TEXT," +
                    " metascore TEXT," +
                    " imdbRating TEXT," +
                    " imdbVotes TEXT," +
                    " type TEXT," +
                    " dvd TEXT," +
                    " boxOffice TEXT," +
                    " production TEXT," +
                    " website TEXT," +
                    " response TEXT)";

    private static final String SQL_CREATE_RATING =
            "CREATE TABLE Rating (" +
                    "ratingKey TEXT PRIMARY KEY," +
                    " imdbID TEXT," +
                    " source TEXT," +
                    " value TEXT)";



    private static final String SQL_DELETE_MOVIE ="DROP TABLE IF EXISTS Movie";
    private static final String SQL_DELETE_RATING ="DROP TABLE IF EXISTS Rating";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ZupProcessoSeletivo.db";

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIE);
        db.execSQL(SQL_CREATE_RATING);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_MOVIE);
        db.execSQL(SQL_DELETE_RATING);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
