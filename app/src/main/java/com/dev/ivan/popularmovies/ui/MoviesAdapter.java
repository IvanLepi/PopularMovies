package com.dev.ivan.popularmovies.ui;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dev.ivan.popularmovies.data.MovieItem;
import com.dev.ivan.popularmovies.data.MovieItemClickListener;
import com.dev.ivan.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link MoviesAdapter} exposes a list of movies
 * from {@link ArrayList} to {@link RecyclerView}.
 * @author Ivan Lepojevic
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ImageViewHolder> {

    private ArrayList<MovieItem> movieItems;
    private final MovieItemClickListener movieItemClickListener;

    public MoviesAdapter(ArrayList<MovieItem> movieItems, MovieItemClickListener listener){
        this.movieItems = movieItems;
        this.movieItemClickListener = listener;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView posterImageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.item_poster_image);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_poster,parent,false));
    }

    @Override
    public int getItemCount() {
        return movieItems.size();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        final MovieItem movieItem = movieItems.get(position);

        Picasso.with(holder.itemView.getContext())
                .load(movieItem.posterUrl)
                .into(holder.posterImageView);

        ViewCompat.setTransitionName(holder.posterImageView,movieItem.title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieItemClickListener.onMovieItemClick(holder.getAdapterPosition(), movieItem, holder.posterImageView);
            }
        });
    }
}
