package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JC on 11/03/17.
 * This class is for configure data list
 */

public class FeedItem implements Parcelable {
    private String title;
    private String thumbnail;
    private String overview;
    private String releaseDate;
    private String voteAverage;
    private String popularity;
    private int MovieIDInTMDB;

    public FeedItem(String title, String thumbnail, String overview, String releaseDate, String voteAverage, String popularity, int MovieIDInTMDB) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
        this.MovieIDInTMDB = MovieIDInTMDB;

    }

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

    int getMovieIDInTMDB() {
        return MovieIDInTMDB;
    }

    void setMovieIDInTMDB(int movieIDInTMDB) {
        MovieIDInTMDB = movieIDInTMDB;
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


    private FeedItem(Parcel in) {
        title = in.readString();
        thumbnail = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readString();
        popularity = in.readString();
        MovieIDInTMDB = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(thumbnail);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(voteAverage);
        dest.writeString(popularity);
        dest.writeInt(MovieIDInTMDB);
    }

    public static final Parcelable.Creator<FeedItem> CREATOR = new Parcelable.Creator<FeedItem>() {
        public FeedItem createFromParcel(Parcel in) {
            return new FeedItem(in);
        }

        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };
}