package com.dev.ivan.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Ivan Lepojevic
 */

public class MovieItem implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>(){
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    public String title;
    public String year;
    public String rating;
    public String overview;
    public String posterUrl;


    public MovieItem(String title,String year, String rating, String overview,String posterUrl){
        this.title = title;
        this.year = year;
        this.rating = rating;
        this.overview = overview;
        this.posterUrl = posterUrl;

    }

    protected MovieItem(Parcel in){
        title = in.readString();
        year = in.readString();
        rating = in.readString();
        overview = in.readString();
        posterUrl = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(rating);
        dest.writeString(overview);
        dest.writeString(posterUrl);

    }
}
