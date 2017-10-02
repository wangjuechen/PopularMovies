package com.jcMobile.android.popularmovies.DataList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by JC on 11/03/17.
 * This class is for configure data list
 */

public class FeedItem implements Parcelable {

        @SerializedName("vote_count")
        @Expose
        private Integer voteCount;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("video")
        @Expose
        private Boolean video;
        @SerializedName("vote_average")
        @Expose
        private Double voteAverage;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("popularity")
        @Expose
        private Double popularity;
        @SerializedName("poster_path")
        @Expose
        private String posterPath;
        @SerializedName("original_language")
        @Expose
        private String originalLanguage;
        @SerializedName("original_title")
        @Expose
        private String originalTitle;
        @SerializedName("genre_ids")
        @Expose
        private List<Integer> genreIds = null;
        @SerializedName("backdrop_path")
        @Expose
        private String backdropPath;
        @SerializedName("adult")
        @Expose
        private Boolean adult;
        @SerializedName("overview")
        @Expose
        private String overview;
        @SerializedName("release_date")
        @Expose
        private String releaseDate;

        public Integer getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(Integer voteCount) {
            this.voteCount = voteCount;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Boolean getVideo() {
            return video;
        }

        public void setVideo(Boolean video) {
            this.video = video;
        }

        public Double getVoteAverage() {
            return voteAverage;
        }

        public void setVoteAverage(Double voteAverage) {
            this.voteAverage = voteAverage;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Double getPopularity() {
            return popularity;
        }

        public void setPopularity(Double popularity) {
            this.popularity = popularity;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public String getOriginalLanguage() {
            return originalLanguage;
        }

        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }

        public String getOriginalTitle() {
            return originalTitle;
        }

        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        public List<Integer> getGenreIds() {
            return genreIds;
        }

        public void setGenreIds(List<Integer> genreIds) {
            this.genreIds = genreIds;
        }

        public String getBackdropPath() {
            return backdropPath;
        }

        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        public Boolean getAdult() {
            return adult;
        }

        public void setAdult(Boolean adult) {
            this.adult = adult;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

    public FeedItem(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readDouble();
        popularity = in.readDouble();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeDouble(voteAverage);
        dest.writeDouble(popularity);
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