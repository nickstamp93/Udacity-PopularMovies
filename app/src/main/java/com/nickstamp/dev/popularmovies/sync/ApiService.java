package com.nickstamp.dev.popularmovies.sync;

import com.nickstamp.dev.popularmovies.model.MovieResults;
import com.nickstamp.dev.popularmovies.model.ReviewResults;
import com.nickstamp.dev.popularmovies.model.TrailerResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Dev on 25/1/2017.
 */

public interface ApiService {

    @GET("movie/{sortOrder}")
    Call<MovieResults> getMovieList(@Path("sortOrder") String sortOrder, @Query("api_key") String apikey);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewResults> getMovieReviews (@Path("movie_id") int id , @Query("api_key") String apikey);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResults> getMovieVideos (@Path("movie_id") int id , @Query("api_key") String apikey);

}
