package com.dev.ivan.popularmovies.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author Ivan Lepojevic
 */

public interface MoviesAPI {
    @GET("top_rated?api_key=**PRIVATE**")
    Observable<MoviesResponse> getTopRatedMovies();

    @GET("popular?api_key=**PRIVATE**")
    Observable<MoviesResponse> getMostPopularMovies();

    @GET("{id}/videos?api_key=**PRIVATE**")
    Observable<TrailersResponse> getTrailer(@Path("id") int movId);

    @GET("{id}/reviews?api_key=**PRIVATE**")
    Observable<ReviewsResponse> getReview(@Path("id") int movId);

}
