package com.dev.ivan.popularmovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.ivan.popularmovies.R;
import com.dev.ivan.popularmovies.data.MovieItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * @author Ivan Lepojevic
 */

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        supportPostponeEnterTransition();



        Bundle extras = getIntent().getExtras();
        final MovieItem movieItem = extras.getParcelable(MainActivity.EXTRA_MOVIE_ITEM);

        ImageView imageView = (ImageView) findViewById(R.id.movie_detail_image_view);
        TextView titleView = (TextView) findViewById(R.id.movie_detail_title);
        TextView dateView = (TextView) findViewById(R.id.movie_detail_year);
        TextView scoreView = (TextView) findViewById(R.id.movie_detail_score);
        TextView overView = (TextView) findViewById(R.id.movie_detail_overview);

        Button trailerButton = (Button) findViewById(R.id.trailer_button);
        trailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent watchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieItem.trailerUrl));
                startActivity(watchIntent);
            }
        });

        titleView.setText(movieItem.title);
        dateView.setText(movieItem.year.substring(0,4));
        scoreView.setText(String.format(getString(R.string.movie_rating),movieItem.rating));
        overView.setText(movieItem.overview);

        String imageUrl = movieItem.posterUrl;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = extras.getString(MainActivity.EXTRA_MOVIE_TRANSITION_NAME);
            imageView.setTransitionName(imageTransitionName);
        }

        Picasso.with(this)
                .load(imageUrl)
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();}

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });
    }


}
