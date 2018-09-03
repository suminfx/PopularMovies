package com.sumin.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mImageViewPoster;
    private TextView mTextViewOriginalTitle;
    private TextView mTextViewOverview;
    private TextView mTextViewReleaseDate;
    private TextView mTextViewRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Getting movie's index
        int indexOfMovie = -1;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MainActivity.EXTRA_POSITION_OF_ITEM)) {
            indexOfMovie = intent.getIntExtra(MainActivity.EXTRA_POSITION_OF_ITEM, -1);
        }
        if (indexOfMovie == -1 || MainActivity.MOVIES.size() <= indexOfMovie || MainActivity.MOVIES.get(indexOfMovie) == null) {
            backToMainActivity();
        }

        //If it's ok getting views
        mImageViewPoster = findViewById(R.id.imageViewPoster);
        mTextViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        mTextViewOverview = findViewById(R.id.textViewOverview);
        mTextViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        mTextViewRating = findViewById(R.id.textViewRating);

        //Getting movie and updatingUI
        Movie movie = MainActivity.MOVIES.get(indexOfMovie);
        updateUIByMovie(movie);
    }

    private void updateUIByMovie(Movie movie) {
        setTitle(movie.getTitle());
        Picasso.with(this).load(movie.getPosterPathOriginalSize()).placeholder(R.drawable.movie_place_holder).into(mImageViewPoster);
        String originalTitle = getString(R.string.original_title) + " " + movie.getOriginalTitle();
        mTextViewOriginalTitle.setText(originalTitle);
        mTextViewOverview.setText(movie.getOverview());
        String rating = movie.getRating();
        mTextViewRating.setText(rating);
        String releaseDate = getString(R.string.release_date) + " " + movie.getReleaseDate();
        mTextViewReleaseDate.setText(releaseDate);
    }

    private void backToMainActivity() {
        Intent backToMainActivity = new Intent(this, MainActivity.class);
        startActivity(backToMainActivity);
    }
}
