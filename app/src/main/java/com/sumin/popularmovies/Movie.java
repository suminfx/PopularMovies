package com.sumin.popularmovies;

public class Movie {
    private String id;
    private String title;
    private String originalTitle;
    private String posterPath;
    private String posterPathOriginalSize;
    private String overview;
    private String rating;
    private String releaseDate;

    public Movie(String id, String title, String originalTitle, String posterPath, String posterPathOriginalSize, String overview, String rating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.posterPathOriginalSize = posterPathOriginalSize;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public String getPosterPathOriginalSize() {
        return posterPathOriginalSize;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

}
