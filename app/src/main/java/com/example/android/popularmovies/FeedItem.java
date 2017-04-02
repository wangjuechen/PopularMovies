package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JC on 11/03/17.
 * This class is for configure data list
 */

public class FeedItem implements Parcelable {
    private String title;
    private String poster_path;
    private String overview;
    private String release_date;
    private String vote_count;
    private String popularity;
    private int id;

    public FeedItem(String title, String poster_path, String overview, String release_date, String vote_count, String popularity, int id) {
        this.title = title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.vote_count = vote_count;
        this.popularity = popularity;
        this.id = id;

    }

    String getPopularity() {
        return popularity;
    }

    void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    String getVote_count() {
        return vote_count;
    }

    void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    String getRelease_date() {
        return release_date;
    }

    void setRelease_date(String release_date) {
        this.release_date = release_date;
    }


    String getOverview() {
        return overview;
    }

    void setOverview(String overview) {
        this.overview = overview;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getPoster_path() {
        return poster_path;
    }

    void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }


    private FeedItem(Parcel in) {
        title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        vote_count = in.readString();
        popularity = in.readString();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(vote_count);
        dest.writeString(popularity);
        dest.writeInt(id);
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