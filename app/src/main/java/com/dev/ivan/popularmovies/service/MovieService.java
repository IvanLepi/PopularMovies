package com.dev.ivan.popularmovies.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.dev.ivan.popularmovies.data.MovieItem;
import com.dev.ivan.popularmovies.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 * @author Ivan Lepojevic
 */

public class MovieService extends IntentService {

    public static final String MOVIE_URL_EXTRA = "mUrl";
    public static final String MOVIE_RESULT_EXTRA = "mResult";

    public MovieService() {
        super("MovieService");
    }


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String movieUrl = intent.getStringExtra(MOVIE_URL_EXTRA);
            URL url = new URL(movieUrl);
            ArrayList<MovieItem> resultArray = downloadUrl(url);

            // Done with processing
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(MOVIE_RESULT_EXTRA,resultArray);
            sendBroadcast(broadcastIntent);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    private ArrayList<MovieItem> downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        ArrayList<MovieItem> movieItems = new ArrayList<>();
        try {
            connection = (HttpsURLConnection) url.openConnection();
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
            result = readStream(stream);

             // Parse Json String input and return {@link ArrayList} of {@link MovieItem}.
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // Fetch movie trailer string.
                    String trailerUrl = generateTrailerUrl(jsonObject.getInt("id"));
                    movieItems.add(new MovieItem(
                            jsonObject.getString("original_title"),
                            jsonObject.getString("release_date"),
                            jsonObject.getString("vote_average"),
                            jsonObject.getString("overview"),
                            "http://image.tmdb.org/t/p/w185/" + jsonObject.getString("poster_path"),
                            trailerUrl));
                }
            } catch (JSONException e) {
                Log.v("Exception", e.toString());
            }

        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return movieItems;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream) throws IOException {
        String result = null;
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
            result = moviesArray.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Make a request to the /movie/{id}/videos endpoint on themoviesdb.org.
     * */
    private String generateTrailerUrl(int id) {
        String resultString = null;
        String urlString = "https://api.themoviedb.org/3/movie/"+ id + "/videos?api_key=08c6f61acdb2a53238daa2ebb8791e0f&language=en-US";
        InputStream stream = null;
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            urlConnection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            urlConnection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            urlConnection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            urlConnection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            urlConnection.connect();
            // Retrieve the response body as an InputStream.
            stream = urlConnection.getInputStream();
            // Read stream and return a String
            resultString = readStream(stream);
            // Format JSON and grab a trailer url String.
            try {
                JSONArray array = new JSONArray(resultString);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    if(jsonObject.getString("type").equals("Trailer")){
                        resultString = "https://www.youtube.com/watch?v=" + jsonObject.getString("key");
                        break;
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }catch (IOException e){

        }

    return resultString;
    }
}
