package com.sumin.popularmovies.data;

import com.sumin.popularmovies.Movie;
import com.sumin.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDBHelper {

    private static final String KEY_RESULTS = "results";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_RATING = "vote_average";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String BASE_URL_POSTERS = "http://image.tmdb.org/t/p/";
    private static final String BASE_SIZE_POSTERS = "w185/";
    private static final String ORIGINAL_SIZE_POSTERS = "original";

    public static ArrayList<Movie> getMoviesFromJSONObject(JSONObject jsonObject) {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONArray jsonObjectResults = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonObjectResults.length(); i++) {
                JSONObject jsonObjectMovie = jsonObjectResults.getJSONObject(i);
                String movieId = jsonObjectMovie.getString(KEY_ID);
                String movieTitle = jsonObjectMovie.getString(KEY_TITLE);
                String movieOriginalTitle = jsonObjectMovie.getString(KEY_ORIGINAL_TITLE);
                String moviePosterPath = BASE_URL_POSTERS + BASE_SIZE_POSTERS + jsonObjectMovie.getString(KEY_POSTER_PATH);
                String moviePosterPathOriginalSize = BASE_URL_POSTERS + ORIGINAL_SIZE_POSTERS + jsonObjectMovie.getString(KEY_POSTER_PATH);
                String movieOverview = jsonObjectMovie.getString(KEY_OVERVIEW);
                String movieRating = jsonObjectMovie.getString(KEY_RATING);
                String movieReleaseDate = jsonObjectMovie.getString(KEY_RELEASE_DATE);
                movies.add(new Movie(movieId, movieTitle, movieOriginalTitle, moviePosterPath, moviePosterPathOriginalSize, movieOverview, movieRating, movieReleaseDate));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

}
