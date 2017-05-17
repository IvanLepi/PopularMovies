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
import com.dev.ivan.popularmovies.data.MovieContract;
import com.dev.ivan.popularmovies.data.MovieContract.MoviesEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author Ivan Lepojevic
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    // Our data will sync once per day.
    // 60 seconds = 1 minute, times 720 = 24h
    private static final int SYNC_INTERVAL = 60 * 720;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public MovieSyncAdapter(Context context, boolean autoInitialize){
        super(context,autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        String API_KEY = "PRIVATE";

        String topMovies = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY + "&language=en-US&page=1";
        String popMovies = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=en-US&page=1";

        String topMoviesJson = fetchJsonString(topMovies);
        String popMoviesJson = fetchJsonString(popMovies);

        // Parse out JsonString's and insert into the database.
        try {
            JSONArray topMoviesArray = new JSONArray(topMoviesJson);
            JSONArray popMoviesArray = new JSONArray(popMoviesJson);
            for (int i = 0; i < popMoviesArray.length(); i++) {
                topMoviesArray.put(popMoviesArray.getJSONObject(i));
            }

            //Vector that will have new information for our database
            Vector<ContentValues> cVValues = new Vector<>(topMoviesArray.length());

            for (int i = 0; i < topMoviesArray.length(); i++) {
                // Values that are collected
                String title;
                String release;
                String rating;
                String overview;
                String posterUrl;

                // Json Object representing a movie
                JSONObject movieObject = topMoviesArray.getJSONObject(i);

                title = movieObject.getString("original_title");
                release = movieObject.getString("release_date");
                rating = movieObject.getString("vote_average");
                overview = movieObject.getString("overview");
                posterUrl = "http://image.tmdb.org/t/p/w185/" + movieObject.getString("poster_path");

                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesEntry.COLUMN_TITLE,title);
                movieValues.put(MoviesEntry.COLUMN_YEAR,release);
                movieValues.put(MoviesEntry.COLUMN_RATING,rating);
                movieValues.put(MoviesEntry.COLUMN_OVERVIEW,overview);
                movieValues.put(MoviesEntry.COLUMN_POSTER_URL,posterUrl);

                cVValues.add(movieValues);
            }
            //Add to database
            int inserted = 0;
            int deleted = 0;
            if(cVValues.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVValues.size()];
                cVValues.toArray(cvArray);
                deleted = getContext().getContentResolver().delete(MovieContract.CONTENT_URI,null,null);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.CONTENT_URI,cvArray);
            }
            Log.d("MovieSyncAdapter","Sync Complete." + inserted  + " inserted." + deleted + " deleted.");

        }catch (JSONException e){
            Log.e("Error parsing Json.",e.getMessage());
        }
    }

    /**
     * Method that handles network work for our app.
     */
    private String fetchJsonString(String urlString){
        String returnJsonString = null;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        try {
            URL mUrl = new URL(urlString);
            connection = (HttpsURLConnection) mUrl.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            returnJsonString = readStream(stream);
        }catch (IOException e){
            Log.e("fetchJsonString",e.getMessage());
        }finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                try {
                    stream.close();
                }catch (IOException e){
                    Log.e("MovieSyncAdapter","Error closing stream.");
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return returnJsonString;
    }

    /**
     * Helper method that converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream) throws IOException {
        String returnString = null;
        stream = new BufferedInputStream(stream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        String inputString;
        while ((inputString = bufferedReader.readLine()) != null) {
            builder.append(inputString);
        }
        try{
            JSONObject topLevel = new JSONObject(builder.toString());
            JSONArray moviesArray = topLevel.getJSONArray("results");
            returnString = moviesArray.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnString;
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
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     * */
    public static void syncImmediately(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or use new one
     * if hasn't been created yet. If we make a new account we call onAccountCreated method
     * so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context){
        // Get an instance of Android Account Manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and the default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type)
        );

        // If the password doesn't exist the account doesn't exist.
        if(null == accountManager.getPassword(newAccount)){

            /*
            * Add the account and account type, no password or user data
            * If successful, return the Account object otherwise report an error.
            * */
            if(!accountManager.addAccountExplicitly(newAccount,"",null)){
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount,context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        
        MovieSyncAdapter.configurePeriodicSync(context,SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount,context.getString(R.string.content_authority),true);
        
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }

}

