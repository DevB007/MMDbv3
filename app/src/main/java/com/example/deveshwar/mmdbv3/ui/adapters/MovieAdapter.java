package com.example.deveshwar.mmdbv3.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deveshwar.mmdbv3.R;
import com.example.deveshwar.mmdbv3.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Deveshwar on 11-12-2015.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> mDataset;

    private Context mContext;

    private ClickListener mClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setMovie(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) return 0;

        return mDataset.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setDataset(List<Movie> dataset) {
        this.mDataset = dataset;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.movie_card_title_text_view)
        public TextView titleTextView;

        @Bind(R.id.movie_card_backdrop_image_view)
        public ImageView posterImageView;

        private Movie mMovie;

        @Override
        public void onClick(View v) {
            if (mClickListener == null) return;

            mClickListener.onMovieCardClicked(v, mMovie);
        }

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        public void setMovie(Movie movie) {
            this.mMovie = movie;

            titleTextView.setText(movie.getTitle());
            Picasso.with(mContext)
                    .load(movie.getBackdropPath())
                    .placeholder(R.drawable.movie_placeholder)
                    .into(posterImageView);
        }

    }

    public interface ClickListener {

        void onMovieCardClicked(View view, Movie movie);

    }

}
