package com.example.deveshwar.mmdbv3.ui.movies.list;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.example.deveshwar.mmdbv3.R;
import com.example.deveshwar.mmdbv3.Util;
import com.example.deveshwar.mmdbv3.api.TmdbApi;
import com.example.deveshwar.mmdbv3.api.responses.MoviesResponse;
import com.example.deveshwar.mmdbv3.models.Movie;
import com.example.deveshwar.mmdbv3.ui.adapters.MovieAdapter;

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
public class MovieListFragment extends Fragment implements MovieAdapter.ClickListener {

    public static final String EXTRA_MOVIE_ID = "no.sindrenm.popularmovies.EXTRA_MOVIE_ID";
    public static final String PREFERENCE_FILE = "MovieListPreferenceFile";

    @Bind(R.id.movie_list_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.movies_recycler_view)
    RecyclerView moviesRecyclerView;

    private static final String TAG = MovieListFragment.class.getSimpleName();

    private static final String ORDER_POPULARITY_DESC = "popularity.desc";
    private static final String ORDER_RATING_DESC = "vote_average.desc";
    private static final String FILTER_FAVORITES = "favorites";

    private static final String PREF_SORT_ORDER = "sort_order";

    private RecyclerView.LayoutManager mLayoutManager;
    private MovieAdapter mAdapter;
    private TmdbApi mTmdbApi;
    private List<Movie> mMoviesList;
    private String mCurrentOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                updateMoviesFromApi(mCurrentOrder);
            }

        });

        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        moviesRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MovieAdapter();
        moviesRecyclerView.setAdapter(mAdapter);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(Util.getGsonConverter())
                .setEndpoint(TmdbApi.API_ENDPOINT)
                .build();

        mTmdbApi = restAdapter.create(TmdbApi.class);
        initializeMoviesList();

        mAdapter.setClickListener(this);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        switch (itemId) {
            case R.id.action_sort_popularity:
                listPopularMovies();

                mCurrentOrder = ORDER_POPULARITY_DESC;
                editor.putString(PREF_SORT_ORDER, mCurrentOrder);
                editor.apply();
                return true;

            case R.id.action_sort_rating:
                listHighestRatedMovies();

                mCurrentOrder = ORDER_RATING_DESC;
                editor.putString(PREF_SORT_ORDER, mCurrentOrder);
                editor.apply();
                return true;

            case R.id.action_filter_favorites:
                listFavoriteMovies();

                editor.putString(PREF_SORT_ORDER, FILTER_FAVORITES);
                editor.apply();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMovieCardClicked(View view, Movie movie) {
        ClickCallback callback = (ClickCallback) getActivity();
        callback.onMovieCardClicked(movie);
    }

    private void listPopularMovies() {
        loadCachedMovies(ORDER_POPULARITY_DESC);
        getActivity().setTitle(R.string.title_activity_movie_list_popular);

        if (mMoviesList.isEmpty()) updateMoviesFromApi(ORDER_POPULARITY_DESC);
    }

    private void listHighestRatedMovies() {
        loadCachedMovies(ORDER_RATING_DESC);
        getActivity().setTitle(R.string.title_activity_movie_list_rating);

        if (mMoviesList.isEmpty()) updateMoviesFromApi(ORDER_RATING_DESC);
    }

    private void listFavoriteMovies() {
        getActivity().setTitle(R.string.title_activity_movie_list_favorites);

        mMoviesList = new Select()
                .from(Movie.class)
                .where("is_favorite = ?", true)
                .execute();

        mAdapter.setDataset(mMoviesList);
    }

    private void initializeMoviesList() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCE_FILE, Activity.MODE_PRIVATE);
        mCurrentOrder = preferences.getString(PREF_SORT_ORDER, ORDER_POPULARITY_DESC);

        switch (mCurrentOrder) {
            case ORDER_POPULARITY_DESC:
                listPopularMovies();
                break;
            case ORDER_RATING_DESC:
                listHighestRatedMovies();
                break;
            case FILTER_FAVORITES:
                listFavoriteMovies();
                break;
        }
    }

    private void loadCachedMovies(String order) {
        String dbOrderClause;
        if (order.equals(ORDER_POPULARITY_DESC)) {
            dbOrderClause = "popularity DESC";
        } else {
            dbOrderClause = "vote_average DESC";
        }

        mMoviesList = new Select()
                .from(Movie.class)
                .orderBy(dbOrderClause)
                .execute();

        mAdapter.setDataset(mMoviesList);
    }

    private void updateMoviesFromApi(String order) {
        ConnectivityManager cm = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (!isConnected) {
            String message = getString(R.string.error_no_network);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

            swipeRefreshLayout.setRefreshing(false);

            return;
        }

        mTmdbApi.getMovies(order, new Callback<MoviesResponse>() {

            @Override
            public void success(MoviesResponse responseObject, Response response) {
                mMoviesList = responseObject.results;
                mAdapter.setDataset(mMoviesList);
                mLayoutManager.scrollToPosition(0);

                persistMovies(mMoviesList);

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Unable to fetch movies due to error: " + error.getMessage());
                System.err.println("ERROR: " + error.getMessage());
            }

        });
    }

    private void persistMovies(List<Movie> moviesFromApi) {
        ActiveAndroid.beginTransaction();

        try {
            for (Movie movieFromApi : moviesFromApi) {
                Movie movieFromDb = new Select()
                        .from(Movie.class)
                        .where("external_id = ?", movieFromApi.getExternalId())
                        .executeSingle();

                if (movieFromDb == null) {
                    movieFromApi.save();
                    continue;
                }

                movieFromDb.setExternalId(movieFromApi.getExternalId());
                movieFromDb.setTitle(movieFromApi.getTitle());
                movieFromDb.setOriginalTitle(movieFromApi.getOriginalTitle());
                movieFromDb.setPosterPath(movieFromApi.getPosterPath());
                movieFromDb.setBackdropPath(movieFromApi.getBackdropPath());
                movieFromDb.setOverview(movieFromApi.getOverview());
                movieFromDb.setPopularity(movieFromApi.getPopularity());
                movieFromDb.setVoteAverage(movieFromApi.getVoteAverage());
                movieFromDb.setReleaseDate(movieFromApi.getReleaseDate());

                movieFromDb.save();
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public interface ClickCallback {

        void onMovieCardClicked(Movie movie);

    }
}
