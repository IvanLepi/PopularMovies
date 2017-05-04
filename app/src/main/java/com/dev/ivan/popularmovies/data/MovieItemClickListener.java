package com.dev.ivan.popularmovies.data;

import android.widget.ImageView;

/**
 * @author Ivan Lepojevic
 */

public interface MovieItemClickListener {
    void onMovieItemClick(int pos, MovieItem movieItem, ImageView sharedImageView);
}

