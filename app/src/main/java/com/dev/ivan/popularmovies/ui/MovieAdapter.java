package com.dev.ivan.popularmovies.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dev.ivan.popularmovies.R;
import com.dev.ivan.popularmovies.data.db.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * @author Ivan Lepojevic
 */

public class MovieAdapter extends CursorRecyclerAdapter<MovieAdapter.ImageViewHolder> {

    private final MovieItemClickListener mItemClickListener;

    public interface MovieItemClickListener {
        void onMovieItemClick(Uri uri);
    }

    public MovieAdapter(Context context, Cursor cursor, MovieItemClickListener listener){
        super(cursor);
        mItemClickListener = listener;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView posterImageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.item_poster_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mCursor.moveToPosition(getAdapterPosition())){
                int columnIndex = mCursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_ID);
                Uri uri = MovieContract.MoviesEntry.buildMovieUri(mCursor.getInt(columnIndex));
                mItemClickListener.onMovieItemClick(uri);
            }
        }
    }

    @Override
    public MovieAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_poster,parent,false));
    }


    @Override
    public void onBindViewHolder(ImageViewHolder holder, Cursor cursor) {

        Picasso.with(holder.itemView.getContext())
                .load(mCursor.getString(mCursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_POSTER_URL)))
                .into(holder.posterImageView);
    }

}
