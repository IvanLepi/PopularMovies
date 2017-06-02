package com.dev.ivan.popularmovies.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.ivan.popularmovies.R;
import com.dev.ivan.popularmovies.data.db.MovieContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * @author Ivan Lepojevic
 */

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAILS_LOADER = 0;
    private Uri mUri;

    private ImageView imageView;
    private TextView titleView;
    private TextView dateView;
    private TextView scoreView;
    private TextView overView;
    private Button trailerButton;
    private Button reviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the URI from Intent
        Intent intent = getIntent();
        mUri = intent.getData();

        // Prepare the loader.
        getSupportLoaderManager().initLoader(DETAILS_LOADER,null,this);

        imageView = (ImageView) findViewById(R.id.movie_detail_image_view);
        titleView = (TextView) findViewById(R.id.movie_detail_title);
        dateView = (TextView) findViewById(R.id.movie_detail_year);
        scoreView = (TextView) findViewById(R.id.movie_detail_score);
        overView = (TextView) findViewById(R.id.movie_detail_overview);
        trailerButton = (Button) findViewById(R.id.trailer_button);
        reviewButton = (Button) findViewById(R.id.review_button);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }
    // This is called when a new Loader needs to be created.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {MovieContract.MoviesEntry.COLUMN_ID,
                MovieContract.MoviesEntry.COLUMN_TITLE,
                MovieContract.MoviesEntry.COLUMN_YEAR,
                MovieContract.MoviesEntry.COLUMN_RATING,
                MovieContract.MoviesEntry.COLUMN_OVERVIEW,
                MovieContract.MoviesEntry.COLUMN_POSTER_URL,
                MovieContract.MoviesEntry.COLUMN_TRAILER,
                MovieContract.MoviesEntry.COLUMN_REVIEW};
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this,
                mUri,
                projection,
                null,
                null,
                null);
    }
    // This method is called when a previously created loader has finished its load.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()){
            titleView.setText(data.getString(data.getColumnIndex(MovieContract.MoviesEntry.COLUMN_TITLE)));
            dateView.setText(data.getString(data.getColumnIndex(MovieContract.MoviesEntry.COLUMN_YEAR)).substring(0,4));
            scoreView.setText(String.format(getString(R.string.movie_rating),
                    data.getString(data.getColumnIndex(MovieContract.MoviesEntry.COLUMN_RATING))));
            overView.setText(data.getString(data.getColumnIndex(MovieContract.MoviesEntry.COLUMN_OVERVIEW)));

            final String trailerUrl = data.getString(data.getColumnIndex(MovieContract.MoviesEntry.COLUMN_TRAILER));
            trailerButton.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(trailerUrl));
                startActivity(intent);
            });

            final String reviewUrl = data.getString(data.getColumnIndex(MovieContract.MoviesEntry.COLUMN_REVIEW));
            if (!reviewUrl.equals("No reviews.")) {
                reviewButton.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewUrl));
                    startActivity(intent);
                });
            }else {
                reviewButton.setVisibility(View.INVISIBLE);
            }

            String imageUrl = data.getString(data.getColumnIndex(MovieContract.MoviesEntry.COLUMN_POSTER_URL));

            Picasso.with(this)
                    .load(imageUrl)
                    .noFade()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();}

                        @Override
                        public void onError() {
                            supportStartPostponedEnterTransition();
                        }
                    });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
