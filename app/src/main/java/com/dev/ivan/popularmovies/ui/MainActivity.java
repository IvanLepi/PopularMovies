package com.dev.ivan.popularmovies.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dev.ivan.popularmovies.R;
import com.dev.ivan.popularmovies.data.MovieContract;
import com.dev.ivan.popularmovies.sync.MovieSyncAdapter;

/**
 * @author Ivan Lepojevic
 */

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER = 0;
    private MovieAdapter mAdapter;
    private Bundle bundleTop = new Bundle();
    private Bundle bundlePop = new Bundle();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundleTop.putString("sort",MovieContract.MoviesEntry.COLUMN_ID + " ASC limit 20");
        bundlePop.putString("sort",MovieContract.MoviesEntry.COLUMN_ID + " DESC limit 20");
        getSupportLoaderManager().initLoader(MOVIE_LOADER,bundleTop,this);

        mAdapter = new MovieAdapter(this,null,this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        MovieSyncAdapter.syncImmediately(this);
    }

    @Override
    public void onMovieItemClick(Uri uri) {

        Intent intent = new Intent(this,MovieDetailActivity.class);
        intent.setData(uri);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Fetch and display Top Rated movies from themoviedb database.
            case R.id.fetch_top_rated:
                getSupportLoaderManager().restartLoader(MOVIE_LOADER,bundleTop,this);
                return true;

            // Fetch and display Popular movies from themoviedb database.
            case R.id.fetch_popular:
                getSupportLoaderManager().restartLoader(MOVIE_LOADER,bundlePop,this);
                return true;

        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {MovieContract.MoviesEntry.COLUMN_ID,
                MovieContract.MoviesEntry.COLUMN_TITLE,
                MovieContract.MoviesEntry.COLUMN_YEAR,
                MovieContract.MoviesEntry.COLUMN_RATING,
                MovieContract.MoviesEntry.COLUMN_OVERVIEW,
                MovieContract.MoviesEntry.COLUMN_POSTER_URL};

        String sortOrder = args.getString("sort");

        return new CursorLoader(this,
                MovieContract.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("onLoadFinished",Integer.toString(data.getCount()));
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
