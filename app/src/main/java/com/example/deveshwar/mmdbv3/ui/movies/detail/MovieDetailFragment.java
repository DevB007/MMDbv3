package com.example.deveshwar.mmdbv3.ui.movies.detail;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.example.deveshwar.mmdbv3.R;
import com.example.deveshwar.mmdbv3.Util;
import com.example.deveshwar.mmdbv3.api.TmdbApi;
import com.example.deveshwar.mmdbv3.api.responses.MovieVideosResponse;
import com.example.deveshwar.mmdbv3.api.responses.ReviewResponse;
import com.example.deveshwar.mmdbv3.models.Movie;
import com.example.deveshwar.mmdbv3.models.MovieVideo;
import com.example.deveshwar.mmdbv3.models.Review;
import com.example.deveshwar.mmdbv3.ui.movies.list.MovieListFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();

    @Bind(R.id.movie_detail_backdrop_image_view)
    ImageView backdropImageView;

    @Bind(R.id.movie_detail_poster_image_view)
    ImageView posterImageView;

    @Bind(R.id.movie_detail_title_text_view)
    TextView titleTextView;

    @Bind(R.id.movie_detail_release_date_text_view)
    TextView releaseDateTextView;

    @Bind(R.id.movie_detail_overview_text_view)
    TextView overviewTextView;

    @Bind(R.id.movie_detail_rating_text_view)
    TextView ratingTextView;

    @Bind(R.id.movie_detail_favorite_image_button)
    ImageButton favoriteButton;

    @Bind(R.id.movie_detail_videos_container)
    LinearLayout videosContainer;

    @Bind(R.id.movie_detail_videos_header)
    TextView videosHeader;

    @Bind(R.id.movie_detail_reviews_container)
    LinearLayout reviewsContainer;

    @Bind(R.id.movie_detail_reviews_header)
    TextView reviewsHeader;

    private TmdbApi mTmdbApi;

    private Movie mMovie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        ButterKnife.bind(this, view);

        Intent intent = getActivity().getIntent();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TmdbApi.API_ENDPOINT)
                .setConverter(Util.getGsonConverter())
                .build();

        mTmdbApi = restAdapter.create(TmdbApi.class);

        int movieId;
        Bundle arguments = getArguments();
        if (arguments != null) {
            movieId = arguments.getInt(MovieListFragment.EXTRA_MOVIE_ID);
        } else {
            movieId = intent.getIntExtra(MovieListFragment.EXTRA_MOVIE_ID, -1);
        }

        mMovie = new Select()
                .from(Movie.class)
                .where("external_id = ?", movieId)
                .executeSingle();

        getActivity().setTitle(mMovie.getTitle());

        populateFields();

        setFavoriteButtonListener();

        fetchTrailers();

        loadReviews();

        return view;
    }

    private void populateFields() {
        Date movieReleaseDate = mMovie.getReleaseDate();

        String releaseDate;
        if (movieReleaseDate == null) {
            releaseDate = "";
        } else {
            releaseDate = DateFormat.getLongDateFormat(getActivity()).format(movieReleaseDate);
        }

        Picasso.with(getActivity())
                .load(mMovie.getBackdropPath())
                .placeholder(R.drawable.movie_placeholder)
                .into(backdropImageView);

        Picasso.with(getActivity())
                .load(mMovie.getPosterPath())
                .placeholder(R.drawable.movie_placeholder)
                .into(posterImageView);

        titleTextView.setText(mMovie.getOriginalTitle());
        releaseDateTextView.setText(releaseDate);
        overviewTextView.setText(mMovie.getOverview());
        ratingTextView.setText(getString(R.string.movie_detail_rating, mMovie.getVoteAverage()));

        updateFavoriteStar();
    }

    private void updateFavoriteStar() {
        if (mMovie.getIsFavorite()) {
            favoriteButton.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        } else {
            favoriteButton.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);

        }
    }

    private void setFavoriteButtonListener() {
        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean newValue = !mMovie.getIsFavorite();
                mMovie.setIsFavorite(newValue);
                mMovie.save();

                updateFavoriteStar();
            }

        });
    }

    private void fetchTrailers() {
        mTmdbApi.getVideosForMovie(mMovie.getExternalId(), new Callback<MovieVideosResponse>() {

            @Override
            public void success(MovieVideosResponse movieVideosResponse, Response response) {
                List<MovieVideo> videos = movieVideosResponse.results;

                List<MovieVideo> youtubeTrailers = new ArrayList<>();

                for (MovieVideo video : videos) {
                    if (video.site.equals("YouTube") && video.type.equals("Trailer")) {
                        youtubeTrailers.add(video);
                    }
                }

                populateVideosList(youtubeTrailers);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Failure loading trailer: " + error.getMessage());
            }

        });
    }

    private void populateVideosList(List<MovieVideo> videos) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        for (int i = 0; i < videos.size(); i++) {
            MovieVideo video = videos.get(i);

            View videoView = inflater.inflate(R.layout.movie_video_button, videosContainer, false);
            Button openTrailerButton = ButterKnife.findById(videoView, R.id.video_start_button);

            openTrailerButton.setText(getString(R.string.movie_detail_open_trailer_button_text, i + 1));
            setViewTrailerListener(openTrailerButton, video);

            videosContainer.addView(videoView);
        }

        if (videos.isEmpty()) videosHeader.setVisibility(View.INVISIBLE);
    }

    private void setViewTrailerListener(Button videoButton, final MovieVideo video) {
        videoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri youtubeUrl = Uri.parse("http://www.youtube.com/watch?v=" + video.key);
                Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUrl);

                startActivity(intent);
            }

        });
    }

    private void loadReviews() {
        mTmdbApi.getReviewsForMovie(mMovie.getExternalId(), new Callback<ReviewResponse>() {

            @Override
            public void success(ReviewResponse reviewResponse, Response response) {
                List<Review> reviews = reviewResponse.results;
                populateReviewsList(reviews);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error loading reviews: " + error.getMessage());
            }

        });
    }

    private void populateReviewsList(List<Review> reviews) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        for (Review review : reviews) {
            View reviewView = inflater.inflate(R.layout.movie_review_detail, reviewsContainer, false);

            TextView authorTextView = ButterKnife.findById(reviewView, R.id.review_author_text_view);
            TextView contentTextView = ButterKnife.findById(reviewView, R.id.review_content_text_view);

            authorTextView.setText(review.author);
            contentTextView.setText(review.content);

            reviewsContainer.addView(reviewView);
        }

        if (reviews.isEmpty()) reviewsHeader.setVisibility(View.INVISIBLE);
    }

}
