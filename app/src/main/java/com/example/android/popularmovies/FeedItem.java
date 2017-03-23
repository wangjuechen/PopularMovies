package com.example.android.popularmovies;

/**
 * Created by JC on 11/03/17.
 * This class is for configure data list
 */

  class FeedItem {
    private String title;
    private String thumbnail;
    private String overview;
    private String releaseDate;
    private String voteAverage;
    private String popularity;

    String getPopularity() {
        return popularity;
    }

     void setPopularity(String popularity) {
        this.popularity = popularity;
    }

     String getVoteAverage() {
        return voteAverage;
    }

     void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

     String getReleaseDate() {
        return releaseDate;
    }

     void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }



     String getOverview() {
        return overview;
    }

     void setOverview(String overview) {
        this.overview = overview;
    }



     String getTitle() {
        return title;
    }

     void setTitle(String title) {
        this.title = title;
    }

     String getThumbnail() {
        return thumbnail;
    }

     void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}