package com.dev.ivan.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.dev.ivan.popularmovies.data.MovieContract.MoviesEntry;


/**
 * @author Ivan Lepojevic
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final String DATABASE_NAME = "PopMovies.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation SQL statement.
    private static final String DATABASE_CREATE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + "( "
            + MoviesEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + MoviesEntry.COLUMN_YEAR + " TEXT NOT NULL, "
            + MoviesEntry.COLUMN_RATING + " TEXT NOT NULL, "
            + MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
            + MoviesEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, "
            + MoviesEntry.COLUMN_TRAILER + " TEXT NOT NULL);";

    public MovieDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
