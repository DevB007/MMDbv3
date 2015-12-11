package com.example.deveshwar.mmdbv3.ui.movies.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.example.deveshwar.mmdbv3.R;
import com.example.deveshwar.mmdbv3.models.Movie;
import com.example.deveshwar.mmdbv3.ui.movies.detail.MovieDetailActivity;
import com.example.deveshwar.mmdbv3.ui.movies.detail.MovieDetailFragment;



public class MovieListActivity extends AppCompatActivity implements MovieListFragment.ClickCallback {

    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MDF_TAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_list);

        mTwoPane = findViewById(R.id.movie_detail_container) != null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public void onMovieCardClicked(Movie movie) {
        Bundle arguments = new Bundle();
        arguments.putInt(MovieListFragment.EXTRA_MOVIE_ID, movie.getExternalId());

        if (mTwoPane) {
            Fragment newDetailFragment = new MovieDetailFragment();
            newDetailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, newDetailFragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieListFragment.EXTRA_MOVIE_ID, movie.getExternalId());
            startActivity(intent);
        }
    }
}
