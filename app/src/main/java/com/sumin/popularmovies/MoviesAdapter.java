package com.sumin.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private final Activity activity;
    private final ArrayList<Movie> movies;
    private OnPosterClickListener onPosterClickListener;

    public MoviesAdapter(Activity context, ArrayList<Movie> movies) {
        this.activity = context;
        this.movies = movies;
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public interface OnPosterClickListener {
        void onPosterClick(int itemPosition);
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = activity.getLayoutInflater().inflate(R.layout.poster_item, viewGroup, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder moviesViewHolder, int i) {
        String urlPoster = movies.get(i).getPosterPath();
        Picasso.with(activity).load(urlPoster).into(moviesViewHolder.imageViewPoster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageViewPoster;

        MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onPosterClickListener.onPosterClick(clickedPosition);
        }
    }


}
