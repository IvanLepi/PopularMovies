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
import android.view.Menu;
import android.view.MenuItem;

import com.dev.ivan.popularmovies.R;
import com.dev.ivan.popularmovies.data.db.MovieContract;
import com.dev.ivan.popularmovies.sync.MovieSyncAdapter;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

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

        bundleTop.putString("sort",MovieContract.MoviesEntry.COLUMN_ID + " ASC limit 10");
        bundlePop.putString("sort",MovieContract.MoviesEntry.COLUMN_ID + " DESC limit 10");

        mAdapter = new MovieAdapter(this,null,this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new FadeInUpAnimator());
        recyclerView.setAdapter(mAdapter);

        MovieSyncAdapter.initializeSyncAdapter(this);
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(MOVIE_LOADER,bundleTop,this);
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
    // This is called when a new Loader needs to be created.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {MovieContract.MoviesEntry.COLUMN_ID,
                MovieContract.MoviesEntry.COLUMN_TITLE,
                MovieContract.MoviesEntry.COLUMN_YEAR,
                MovieContract.MoviesEntry.COLUMN_RATING,
                MovieContract.MoviesEntry.COLUMN_OVERVIEW,
                MovieContract.MoviesEntry.COLUMN_POSTER_URL};

        String sortOrder = args.getString("sort");

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this,
                MovieContract.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder);

    }
    // This method is called when a previously created loader has finished its load.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);

    }

}
