package com.dev.ivan.popularmovies.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * @author Ivan Lepojevic
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final String DATABASE_NAME = "PopMovies.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation SQL statement.
    private static final String DATABASE_CREATE = "CREATE TABLE " + MovieContract.MoviesEntry.TABLE_NAME + "( "
            + MovieContract.MoviesEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MovieContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + MovieContract.MoviesEntry.COLUMN_YEAR + " TEXT NOT NULL, "
            + MovieContract.MoviesEntry.COLUMN_RATING + " TEXT NOT NULL, "
            + MovieContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
            + MovieContract.MoviesEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, "
            + MovieContract.MoviesEntry.COLUMN_TRAILER + " TEXT NOT NULL, "
            + MovieContract.MoviesEntry.COLUMN_REVIEW + " TEXT NOT NULL);";

    public MovieDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
