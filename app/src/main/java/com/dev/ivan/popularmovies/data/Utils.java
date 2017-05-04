package com.dev.ivan.popularmovies.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Ivan Lepojevic
 */

public class Utils {

    /**
     * Parse Json String input and return {@link ArrayList} of {@link MovieItem}.
     */

    public static ArrayList<MovieItem> generateMovieItems(String moviesArray) {
        ArrayList<MovieItem> movieItems = new ArrayList<>();
        try{
        JSONArray jsonArray = new JSONArray(moviesArray);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            movieItems.add(new MovieItem(
                    jsonObject.getString("original_title"),
                    jsonObject.getString("release_date"),
                    jsonObject.getString("vote_average"),
                    jsonObject.getString("overview"),
                    "http://image.tmdb.org/t/p/w185/" + jsonObject.getString("poster_path")));
        }
        }
        catch (JSONException e){
            Log.v("Exception",e.toString());
        }
        return movieItems;
    }
}
