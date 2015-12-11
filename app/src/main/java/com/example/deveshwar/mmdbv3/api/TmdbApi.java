package com.example.deveshwar.mmdbv3.api;

/**
 * Created by Deveshwar on 11-12-2015.
 */
import com.example.deveshwar.mmdbv3.SampleSecrets;
import com.example.deveshwar.mmdbv3.api.responses.MovieVideosResponse;
import com.example.deveshwar.mmdbv3.api.responses.MoviesResponse;
import com.example.deveshwar.mmdbv3.api.responses.ReviewResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface TmdbApi {

    String API_ENDPOINT = "http://api.themoviedb.org/3";
    String API_KEY = SampleSecrets.TMDB_API_KEY;

    @GET("/discover/movie?api_key=" + API_KEY)
    void getMovies(@Query("sort_by") String order, Callback<MoviesResponse> cb);

    @GET("/movie/{id}/videos?api_key=" + API_KEY)
    void getVideosForMovie(@Path("id") int movieId, Callback<MovieVideosResponse> cb);

    @GET("/movie/{id}/reviews?api_key=" + API_KEY)
    void getReviewsForMovie(@Path("id") int movieId, Callback<ReviewResponse> cb);

}
