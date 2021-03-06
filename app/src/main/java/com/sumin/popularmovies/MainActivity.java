package com.sumin.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sumin.popularmovies.data.MovieDBHelper;
import com.sumin.popularmovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final ArrayList<Movie> MOVIES = new ArrayList<>();
    public static final String EXTRA_POSITION_OF_ITEM = "position of item";

    //Views
    private MoviesAdapter mMoviesAdapter;
    @BindView(R.id.recyclerViewPosters) RecyclerView mRecyclerViewPosters;
    @BindView(R.id.switchSortBy) Switch mSwitchSortBy;
    @BindView(R.id.textViewTopRated) TextView mTextViewTopRated;
    @BindView(R.id.textViewMostPopular) TextView mTextViewMostPopular;
    @BindView(R.id.progressBarLoading) ProgressBar mProgressBarLoading;

    private GridLayoutManager layoutManager;
    private boolean isLoading = false;
    private int page = 1;
    private String language;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int spanCount = calculateNoOfColumns();
        layoutManager = new GridLayoutManager(this, spanCount);
        Configuration configuration = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= 24) {
            language = configuration.getLocales().get(0).getLanguage();
        } else {
            language = configuration.locale.getLanguage();
        }
        mRecyclerViewPosters.setLayoutManager(layoutManager);
        mMoviesAdapter = new MoviesAdapter(this, MOVIES);
        mMoviesAdapter.setOnPosterClickListener(new MoviesAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int clickedPosition) {
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra(EXTRA_POSITION_OF_ITEM, clickedPosition);
                startActivity(intent);
            }
        });

        mRecyclerViewPosters.setAdapter(mMoviesAdapter);
        mRecyclerViewPosters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        isLoading = true;
                        loadNewMovies();
                    }
                }
            }
        });

        mSwitchSortBy.setChecked(true);
        mSwitchSortBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean sortByTopRated) {
                page = 1;
                if (sortByTopRated) {
                    MOVIES.clear();
                    MOVIES.addAll(getMoviesFromNetworkSortedBy(NetworkUtils.SortBy.TOP_RATED_DESC));
                    mTextViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
                    mTextViewMostPopular.setTextColor(Color.WHITE);
                } else {
                    MOVIES.clear();
                    MOVIES.addAll(getMoviesFromNetworkSortedBy(NetworkUtils.SortBy.MOST_POPULAR_DESC));
                    mTextViewMostPopular.setTextColor(getResources().getColor(R.color.colorAccent));
                    mTextViewTopRated.setTextColor(Color.WHITE);
                }
                mMoviesAdapter.notifyDataSetChanged();
            }
        });
        mSwitchSortBy.setChecked(false);
    }

    private void loadNewMovies() {
        page++;
        NetworkUtils.SortBy sortBy = mSwitchSortBy.isChecked() ? NetworkUtils.SortBy.TOP_RATED_DESC : NetworkUtils.SortBy.MOST_POPULAR_DESC;
        MOVIES.addAll(getMoviesFromNetworkSortedBy(sortBy, page));
        mMoviesAdapter.notifyDataSetChanged();
        isLoading = false;
    }

    private ArrayList<Movie> getMoviesFromNetworkSortedBy(NetworkUtils.SortBy sortBy) {
        return getMoviesFromNetworkSortedBy(sortBy, page);
    }

    private ArrayList<Movie> getMoviesFromNetworkSortedBy(NetworkUtils.SortBy sortBy, int page) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (NetworkUtils.isConnected(this)) {
            URL url = NetworkUtils.createURLByMethodOfSort(sortBy, page, language);
            if (url != null) {
                JSONObject jsonObject = NetworkUtils.getJSONObjectFromURL(url, new NetworkUtils.AsyncProcess() {
                    @Override
                    public void onProcessStart() {
                        mProgressBarLoading.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onProcessFinish() {
                        mProgressBarLoading.setVisibility(View.INVISIBLE);
                    }
                });
                if (jsonObject != null) {
                    movies = MovieDBHelper.getMoviesFromJSONObject(jsonObject);
                } else if (!NetworkUtils.isInternetConnection) {
                    Toast.makeText(this, R.string.warning_slow_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, R.string.warning_no_internet_connection, Toast.LENGTH_SHORT).show();
        }
        return movies;
    }

    public void onClickSwitchToMostPopular(View view) {
        mSwitchSortBy.setChecked(false);
    }

    public void onClickSwitchToTopRated(View view) {
        mSwitchSortBy.setChecked(true);
    }

    public int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }
}
