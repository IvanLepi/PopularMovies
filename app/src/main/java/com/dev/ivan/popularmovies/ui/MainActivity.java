package com.dev.ivan.popularmovies.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.dev.ivan.popularmovies.R;
import com.dev.ivan.popularmovies.data.MovieItem;
import com.dev.ivan.popularmovies.data.MovieItemClickListener;
import com.dev.ivan.popularmovies.service.MovieService;

import java.util.ArrayList;

/**
 * @author Ivan Lepojevic
 */

public class MainActivity extends AppCompatActivity implements MovieItemClickListener {

    private static final String API_KEY = "PRIVATE";

    // Urls for fetching movies from themoviedb.org API endpoints. It requires API Key.
    private String topRatedUrl = "https://api.themoviedb.org/3/movie/top_rated?api_key=" +
            API_KEY + "&language=en-US&page=1";
    private String popularUrl ="https://api.themoviedb.org/3/movie/popular?api_key=" +
            API_KEY + "&language=en-US&page=1";

    ArrayList<MovieItem> movieItems = new ArrayList<>();
    RecyclerView recyclerView;

    public static final String EXTRA_MOVIE_ITEM = "movie_image_url";
    public static final String EXTRA_MOVIE_TRANSITION_NAME = "movie_image_transition_name";

    private ResponseReceiver responseReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoviesAdapter moviesAdapter = new MoviesAdapter(movieItems, this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(moviesAdapter);

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        responseReceiver = new ResponseReceiver();
        registerReceiver(responseReceiver,filter);

        Intent resultIntent = new Intent(this,MovieService.class);
        resultIntent.putExtra(MovieService.MOVIE_URL_EXTRA, topRatedUrl);
        startService(resultIntent);
    }

    @Override
    public void onMovieItemClick(int pos, MovieItem movieItem, ImageView sharedImageView) {

        Intent intent = new Intent(this,MovieDetailActivity.class);
        intent.putExtra(EXTRA_MOVIE_ITEM,movieItem);
        intent.putExtra(EXTRA_MOVIE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedImageView,
                ViewCompat.getTransitionName(sharedImageView));

        startActivity(intent,options.toBundle());
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
                Intent topIntent = new Intent(this,MovieService.class);
                topIntent.putExtra(MovieService.MOVIE_URL_EXTRA, topRatedUrl);
                startService(topIntent);
                return true;

            // Fetch and display Popular movies from themoviedb database.
            case R.id.fetch_popular:
                Intent popularIntent = new Intent(this,MovieService.class);
                popularIntent.putExtra(MovieService.MOVIE_URL_EXTRA, popularUrl);
                startService(popularIntent);
                return true;

        }
        return false;
    }

    /**
     * Simple {@link BroadcastReceiver} subclass for informing the main application activity that
     * information can be updated.
     */
    public class ResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP = "com.dev.ivan.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            movieItems = intent.getParcelableArrayListExtra(MovieService.MOVIE_RESULT_EXTRA);
            // Refresh the RecyclerView with new data.
            updateView(movieItems);
        }
    }

    /**
     * Simple method used to update RecyclerView with new data.
     * */
    private void updateView(ArrayList items){
        recyclerView.setAdapter(new MoviesAdapter(items,this));
    }
}
