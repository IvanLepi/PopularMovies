package com.dev.ivan.popularmovies.data.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Ivan Lepojevic
 */

public class MovieProvider extends ContentProvider {
    private MovieDbHelper movieDbHelper;

    // Used for the UriMatcher
    private static final int MOVIES = 10;
    private static final int MOVIE_ID = 20;

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.BASE_PATH,MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.BASE_PATH + "/#",MOVIE_ID);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_ID: {
                retCursor = movieDbHelper.getWritableDatabase().rawQuery("SELECT * FROM movies WHERE _id = ?",new String[] {uri.getLastPathSegment()});
                break;
            }
            case MOVIES: {
                retCursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);

        }
        //noinspection ConstantConditions
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        Uri returnUri;
        long _id = db.insert(MovieContract.MoviesEntry.TABLE_NAME,null,contentValues);
        if(_id > 0)
            returnUri = MovieContract.MoviesEntry.buildMovieUri(_id);
        else
            throw new android.database.SQLException("Failed to insert new row into " + uri);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values){
                long _id = db.insert(MovieContract.MoviesEntry.TABLE_NAME,null,value);
                if(_id != -1){
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
        //noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri,null);
        return returnCount;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        return db.delete(MovieContract.MoviesEntry.TABLE_NAME,selection,selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
