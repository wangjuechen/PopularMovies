package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Adapters.ReviewsAdapter;
import com.example.android.popularmovies.Adapters.TrailersAdapter;
import com.example.android.popularmovies.DataList.FeedMovieVideo;
import com.example.android.popularmovies.DataList.FeedUserReviews;
import com.example.android.popularmovies.data.PopularMovieDbHelper;
import com.example.android.popularmovies.utilities.FavoriteMovieUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JC on 12/03/17.
 * This is the Child Activity class
 */

public class ChildActivity extends AppCompatActivity {

    public Toast mToast;

    private static final String mURLDomain = "https://api.themoviedb.org/3/movie/";

    private static final String mKey = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    private List<FeedMovieVideo> feedMovieVideoList = new ArrayList<>();

    private List<FeedUserReviews> feedUserReviews = new ArrayList<>();

    private TrailersAdapter mTrailersAdapter;

    private ReviewsAdapter mReviewsAdapter;

    private FavoriteMovieUtils mFavoriteMovieUtils = new FavoriteMovieUtils();

    @BindView(R.id.tv_releaseDate_child_activity) TextView mReleaseDateText;

    @BindView(R.id.tv_voteReverage_child_activity) TextView mVoteAverage;

    @BindView(R.id.tv_overview) TextView mOverViewText;

    @BindView(R.id.iv_poster_child_activity) ImageView mThumbnailImage;

    @BindView(R.id.tv_runtime_child_activity) TextView mRuntimeText;

    @BindView(R.id.favorite_checkBox) CheckBox mFavoriteCheck;

    @BindView(R.id.rv_trailsForYoutube) RecyclerView mTrailerRecycleView;

    @BindView(R.id.rv_userReviews) RecyclerView mReviewsRecycleView;

    private Context mContext;

    private String mMovieRuntime, mMovieTitle;

    private int mMovieIdInTMBD;

    private PopularMovieDbHelper mPopularMovieHelper;

    private SQLiteDatabase db;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_page);
        ButterKnife.bind(this);

        LinearLayoutManager linearLayoutManagerForTrailer = new LinearLayoutManager(this);

        LinearLayoutManager linearLayoutManagerForReview = new LinearLayoutManager(this);

        linearLayoutManagerForTrailer.setOrientation(LinearLayoutManager.HORIZONTAL);

        linearLayoutManagerForReview.setOrientation(LinearLayoutManager.VERTICAL);

        mTrailerRecycleView.setLayoutManager(linearLayoutManagerForTrailer);

        mReviewsRecycleView.setLayoutManager(linearLayoutManagerForReview);

        mContext = ChildActivity.this;

        Intent intentThatStartedThisActivity = getIntent();

        Bundle extrasForDetails = intentThatStartedThisActivity.getExtras();

        if (extrasForDetails != null) {

            mMovieIdInTMBD = extrasForDetails.getInt("id", 0);

            if (mFavoriteMovieUtils.hasObject(this, String.valueOf(mMovieIdInTMBD))) {
                mFavoriteCheck.setChecked(true);
            }

            mMovieTitle = extrasForDetails.getString("title");

            final ActionBar abar = getSupportActionBar();

            View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar, null);
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);
            TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.toolBar_title);
            textviewTitle.setText(mMovieTitle);
            abar.setCustomView(viewActionBar, params);
            abar.setDisplayShowCustomEnabled(true);
            abar.setDisplayShowTitleEnabled(false);
            abar.setDisplayHomeAsUpEnabled(true);
            abar.setHomeButtonEnabled(true);

            mReleaseDateText.setText(extrasForDetails.getString("releaseDate").substring(0,4));

            mOverViewText.setText(extrasForDetails.getString("overview"));

            mVoteAverage.setText(getString(R.string.vote_count, String.valueOf(extrasForDetails.getDouble("voteAverage"))));

            Picasso.with(mContext).
                    load("http://image.tmdb.org/t/p/w185/" + extrasForDetails.getString("Thumbnail"))
                    .into(mThumbnailImage);
        }

        String mMovieVideoURL = mURLDomain + String.valueOf(mMovieIdInTMBD) +
                "/videos?api_key=" + mKey + "&language=en-US";
        //From movieId, got the movieVideoUrl

        String mMovieRuntimeURL = mURLDomain + String.valueOf(mMovieIdInTMBD) +
                "?api_key=" + mKey;
        //From movieId, got the movieRuntimeUrl

        String mUserReviewsURL = mURLDomain + String.valueOf(mMovieIdInTMBD) +
                "/reviews?api_key=" + mKey + "&language=en-US";
        //From movieId, got the movieReviewUrl

        mGetRuntime(mMovieRuntimeURL);

        mGetYoutubeTrailers(mMovieVideoURL);

        mGetReviews(mUserReviewsURL);

        mTrailersAdapter = new TrailersAdapter(this, feedMovieVideoList);

        mReviewsAdapter = new ReviewsAdapter(this, feedUserReviews);

        mTrailerRecycleView.setAdapter(mTrailersAdapter);

        mReviewsRecycleView.setAdapter(mReviewsAdapter);

        mPopularMovieHelper = new PopularMovieDbHelper(ChildActivity.this);

        db = mPopularMovieHelper.getWritableDatabase();


    }

    public void favoriteClick(View v) {

        boolean checked = mFavoriteCheck.isChecked();

        mFavoriteMovieUtils.favoriteCheck(this, mMovieIdInTMBD, checked);
    }

    private void mGetReviews(String Url) {

        URL MovieReviewsSearchUrl = NetworkUtils.buildUrl(Url);

        new FetchMovieTask().execute(MovieReviewsSearchUrl);

    }

    private void mGetRuntime(String Url) {

        URL MovieRuntimeSearchUrl = NetworkUtils.buildUrl(Url);

        new FetchMovieTask().execute(MovieRuntimeSearchUrl);
    }

    private void mGetYoutubeTrailers(String Url) {

        URL MovieVideoSearchUrl = NetworkUtils.buildUrl(Url);

        new FetchMovieTask().execute(MovieVideoSearchUrl);
    }

    private class FetchMovieTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];

            try {

                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(searchUrl);

                return jsonMovieResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String movieResponse) {
            if (movieResponse.contains("runtime")) {

                parseResultForRuntime(movieResponse);

                mRuntimeText.setText(mMovieRuntime + " " + getString(R.string.runtime_text));
            } else if (movieResponse.contains("author")) {

                parseUserReviews(movieResponse);

                mReviewsAdapter.setMovieData(feedUserReviews);
            } else if (movieResponse.contains("site")) {

                parseResultForYoutube(movieResponse);

                mTrailersAdapter.setMovieData(feedMovieVideoList);
            }
        }
    }

    public void parseResultForYoutube(String Url) {
        final String OWM_LIST = "results";

        final String OWM_KEY = "key";

        final String OWM_NAME = "name";

        final String OWM_SIZE = "size";

        final String OWM_TYPE = "type";

        try {
            JSONObject response = new JSONObject(Url);

            JSONArray posts = response.optJSONArray(OWM_LIST);

            feedMovieVideoList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);

                FeedMovieVideo feedlist = new FeedMovieVideo();

                feedlist.setKeyForTrailerOnYoutube(post.optString(OWM_KEY));

                feedlist.setTrailerName(post.optString(OWM_NAME));

                feedlist.setTrailerSize(post.optString(OWM_SIZE));

                feedlist.setTrailerType(post.optString(OWM_TYPE));

                feedMovieVideoList.add(feedlist);

            }
        } catch (Exception e) {
            Log.v("Wrong:", e.getMessage());
            mToast = Toast.makeText(this, "There is no Trailers", Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    private void parseResultForRuntime(String Url) {

        final String OWM_RUNTIME = "runtime";

        try {
            JSONObject response = new JSONObject(Url);

            mMovieRuntime = response.optString(OWM_RUNTIME);

        } catch (Exception e) {
            mToast = Toast.makeText(this, "There is no Runtime", Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    private void parseUserReviews(String Url) {

        final String OWM_LIST = "results";
        final String OWM_USERNAME = "author";
        final String OWM_CONTENT = "content";

        try {
            JSONObject response = new JSONObject(Url);
            JSONArray posts = response.optJSONArray(OWM_LIST);

            feedUserReviews = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);

                FeedUserReviews feedlist = new FeedUserReviews();

                feedlist.setUserNames(post.optString(OWM_USERNAME));
                feedlist.setReviewsContent(post.optString(OWM_CONTENT));

                feedUserReviews.add(feedlist);

            }
        } catch (Exception e) {
            mToast = Toast.makeText(this, "This is no reviews", Toast.LENGTH_SHORT);
            mToast.show();
        }

    }
}

