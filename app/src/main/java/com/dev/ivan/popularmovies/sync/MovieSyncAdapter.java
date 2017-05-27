package com.dev.ivan.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.dev.ivan.popularmovies.R;
import com.dev.ivan.popularmovies.api.Movie;
import com.dev.ivan.popularmovies.api.MoviesAPI;
import com.dev.ivan.popularmovies.api.MoviesResponse;
import com.dev.ivan.popularmovies.api.ReviewsResponse;
import com.dev.ivan.popularmovies.api.TrailersResponse;
import com.dev.ivan.popularmovies.data.db.MovieContract;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Ivan Lepojevic
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    // Our data will sync once per day.
    private static final int SYNC_INTERVAL = 24 * 3600;
    private static final int SYNC_FLEXTIME = 3600;
    private static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    private Vector<ContentValues> cVValues = new Vector<>(20);
    private MoviesAPI api;
    private ArrayList<Movie> moviesList = new ArrayList<>();


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        // Create a Retrofit adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        api = retrofit.create(MoviesAPI.class);

        fetchMovies();
    }

    private void fetchMovies(){
        Observable.zip(api.getTopRatedMovies(), api.getMostPopularMovies(),
                (moviesResponse, moviesResponse2) -> allMovies(moviesResponse,moviesResponse2))
                .flatMap(movies-> Observable.fromIterable(movies)) // return movies one by one
                .flatMap(movie -> Observable.zip(api.getTrailer(movie.getId()),
                                api.getReview(movie.getId()),
                                Observable.just(movie),
                                (trailers,reviews, movie1) -> new ImmutableTriple<>(trailers,reviews, movie1)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImmutableTriple<TrailersResponse,ReviewsResponse, Movie>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImmutableTriple<TrailersResponse,ReviewsResponse, Movie> triple) {
                        TrailersResponse trailersResponse = triple.getLeft();
                        String trailerUrl = trailersResponse.getResults().get(0).getKey();
                        Movie movie = triple.getRight();
                        ReviewsResponse reviewsResponse = triple.getMiddle();
                        String review;
                        if(reviewsResponse.getTotalResults() != 0) {
                            review = reviewsResponse.getResults().get(0).getUrl();
                        }else{
                            review = "No reviews.";
                        }
                        ContentValues movieValues = new ContentValues();
                        movieValues.put(MovieContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
                        movieValues.put(MovieContract.MoviesEntry.COLUMN_YEAR, movie.getReleaseDate());
                        movieValues.put(MovieContract.MoviesEntry.COLUMN_RATING, movie.getVoteAverage());
                        movieValues.put(MovieContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
                        movieValues.put(MovieContract.MoviesEntry.COLUMN_POSTER_URL, "http://image.tmdb.org/t/p/w185/" + movie.getPosterPath());
                        movieValues.put(MovieContract.MoviesEntry.COLUMN_TRAILER, "https://www.youtube.com/watch?v=" + trailerUrl);
                        movieValues.put(MovieContract.MoviesEntry.COLUMN_REVIEW, review);
                        cVValues.add(movieValues);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        //Add to database
                        int inserted = 0;
                        int deleted = 0;
                        if (cVValues.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[cVValues.size()];
                            cVValues.toArray(cvArray);
                            deleted = getContext().getContentResolver().delete(MovieContract.CONTENT_URI, null, null);
                            inserted = getContext().getContentResolver().bulkInsert(MovieContract.CONTENT_URI, cvArray);
                        }
                        Log.d("MovieSyncAdapter", "Sync Complete." + inserted + " inserted." + deleted + " deleted.");
                    }
                });
    }



    private List<Movie> allMovies(MoviesResponse moviesResponse, MoviesResponse moviesResponse2) {
        List<Movie> topMovies = moviesResponse.getResults().subList(0,8);  // themoviedb.org allows
        List<Movie> popMovies = moviesResponse2.getResults().subList(0,8); // 40 requests / 10 sec.
        List<Movie> allMovies = new ArrayList<>();
        allMovies.addAll(topMovies);
        allMovies.addAll(popMovies);
        return allMovies;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int syncFlextime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        SyncRequest request = new SyncRequest.Builder()
                .syncPeriodic(syncInterval, syncFlextime)
                .setSyncAdapter(account, authority)
                .setExtras(new Bundle()).build();
        ContentResolver.requestSync(request);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or use new one
     * if hasn't been created yet. If we make a new account we call onAccountCreated method
     * so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of Android Account Manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and the default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type)
        );

        // If the password doesn't exist the account doesn't exist.
        if (null == accountManager.getPassword(newAccount)) {

            /*
            * Add the account and account type, no password or user data
            * If successful, return the Account object otherwise report an error.
            * */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}

