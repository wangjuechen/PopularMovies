package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.PopularMovieContract;
import com.example.android.popularmovies.data.PopularMovieDbHelper;
import com.example.android.popularmovies.databinding.DetailedPageBinding;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.data.PopularMovieContract.PopularMovieEntry.TABLE_NAME;

/**
 * Created by JC on 12/03/17.
 * This is the Child Activity class
 */

public class ChildActivity extends AppCompatActivity {

    public Toast mToast;

    private static final String mURLDomain = "https://api.themoviedb.org/3/movie/";

    private static final String mkey = BuildConfig.API_KEY;

    private List<FeedMovieVideo> feedMovieVideoList = new ArrayList<>();

    private List<FeedUserReviews> feedUserReviewses = new ArrayList<>();

    private TrailersAdapter mTrailersAdapter;

    private ReviewsAdapter mReviewsAdapter;

    private TextView mRuntimeText;

    private ImageView mThumbnailImage;

    private Context mContext;

    private RecyclerView mTrailerRecycleView, mReviewsRecycleView;

    private String mMovieRuntime, mMovieTitle;

    private int mMoiveIdInTMBD;

    private CheckBox mFavoriteCheck;

    private PopularMovieDbHelper mPopularMovieHelper;

    private SQLiteDatabase db;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_page);

        DetailedPageBinding binding = DataBindingUtil.setContentView(this, R.layout.detailed_page);

        mThumbnailImage = (ImageView) findViewById(R.id.iv_poster_child_astivity);
        mRuntimeText = (TextView) findViewById(R.id.tv_runtime_child_activity);
        mFavoriteCheck = (CheckBox) findViewById(R.id.favorite_checkBox);

        mTrailerRecycleView = (RecyclerView) findViewById(R.id.rv_trailsForYoutube);

        mReviewsRecycleView = (RecyclerView) findViewById(R.id.rv_userReviews);

        LinearLayoutManager linearLayoutManagerForTrailer = new LinearLayoutManager(this);

        LinearLayoutManager linearLayoutManagerForReview = new LinearLayoutManager(this);

        linearLayoutManagerForTrailer.setOrientation(LinearLayoutManager.HORIZONTAL);

        linearLayoutManagerForReview.setOrientation(LinearLayoutManager.VERTICAL);

        mTrailerRecycleView.setLayoutManager(linearLayoutManagerForTrailer);

        mReviewsRecycleView.setLayoutManager(linearLayoutManagerForReview);

        mContext = ChildActivity.this;

        Intent intentThatStartedThisActivity = getIntent();

        mMoiveIdInTMBD = intentThatStartedThisActivity.getIntExtra("id", 0);

        if (hasObject(String.valueOf(mMoiveIdInTMBD))) {
            mFavoriteCheck.setChecked(true);
        }

        mMovieTitle = intentThatStartedThisActivity.getStringExtra("title");
        binding.tvTitleChildActivity.setText(mMovieTitle);

        binding.tvReleaseDateChildActivity.setText(intentThatStartedThisActivity.getStringExtra("releaseDate"));

        binding.tvOverview.setText(intentThatStartedThisActivity.getStringExtra("overview"));

        binding.tvVoteReverageChildActivity.setText(getString(R.string.voteAverage, intentThatStartedThisActivity.getStringExtra("voteAverage")));

        Picasso.with(mContext).
                load("http://image.tmdb.org/t/p/w185/" + intentThatStartedThisActivity.getStringExtra("Thumbnail"))
                .into(mThumbnailImage);

        String mMovieVedioURL = mURLDomain + String.valueOf(mMoiveIdInTMBD) +
                "/videos?api_key=" + mkey + "&language=en-US";
        //From movieId, got the movieVedioUrl

        String mMovieRuntimeURL = mURLDomain + String.valueOf(mMoiveIdInTMBD) +
                "?api_key=" + mkey;
        //From movieId, got the movieRuntimeUrl

        String mUserReviewsURL = mURLDomain + String.valueOf(mMoiveIdInTMBD) +
                "/reviews?api_key=" + mkey + "&language=en-US";
        //From movieId, got the movieReviewUrl

        mGetRuntime(mMovieRuntimeURL);

        mGetYoutubeTrailers(mMovieVedioURL);

        mGetReviews(mUserReviewsURL);

        mTrailersAdapter = new TrailersAdapter(this, feedMovieVideoList);

        mReviewsAdapter = new ReviewsAdapter(this, feedUserReviewses);

        mTrailerRecycleView.setAdapter(mTrailersAdapter);

        mReviewsRecycleView.setAdapter(mReviewsAdapter);

        mPopularMovieHelper = new PopularMovieDbHelper(ChildActivity.this);

        db = mPopularMovieHelper.getWritableDatabase();


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean hasObject(String searchItem) {

        String[] columns = {PopularMovieContract.PopularMovieEntry.COLUMN_MovieID};
        String selection = PopularMovieContract.PopularMovieEntry.COLUMN_MovieID + " =?";
        String[] selectionArgs = {searchItem};

        Cursor cursor = getContentResolver().query(PopularMovieContract.PopularMovieEntry.CONTENT_URI, columns, selection, selectionArgs, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void favoriteClick(View v) {

        if (mFavoriteCheck.isChecked()) {

            addFaroviteToSQLite();

            long number = DatabaseUtils.queryNumEntries(db, TABLE_NAME);

            Log.v("Test 1", "Text for add " + String.valueOf(mMoiveIdInTMBD) + "Size of Database: " + String.valueOf(number));
        } else if (!mFavoriteCheck.isChecked()) {

            deleteFaroviteFromSQLite();

            long number = DatabaseUtils.queryNumEntries(db, TABLE_NAME);

            Log.v("Test 1", "Text for add " + String.valueOf(mMoiveIdInTMBD) + "Size of Database: " + String.valueOf(number));
        }
    }

    private void addFaroviteToSQLite() {

        ContentValues FavoriteMovie = new ContentValues();

        FavoriteMovie.put(PopularMovieContract.PopularMovieEntry.COLUMN_MovieID, String.valueOf(mMoiveIdInTMBD));

        FavoriteMovie.put(PopularMovieContract.PopularMovieEntry.COLUMN_MovieURL, mURLDomain + String.valueOf(mMoiveIdInTMBD)
                + "?api_key=" + mkey + "&language=en-US");

        getContentResolver().insert(PopularMovieContract.PopularMovieEntry.CONTENT_URI, FavoriteMovie);

    }

    private void deleteFaroviteFromSQLite() {
        String selection = PopularMovieContract.PopularMovieEntry.COLUMN_MovieID + " =?";
        String[] selectionArgs = {String.valueOf(mMoiveIdInTMBD)};
        getContentResolver().delete(PopularMovieContract.PopularMovieEntry.CONTENT_URI, selection, selectionArgs);

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

        URL MovieVedioSearchUrl = NetworkUtils.buildUrl(Url);

        new FetchMovieTask().execute(MovieVedioSearchUrl);
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

                mReviewsAdapter.setMovieData(feedUserReviewses);
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

                feedlist.setmKeyForTrailerOnYoutube(post.optString(OWM_KEY));

                feedlist.setmTrailerName(post.optString(OWM_NAME));

                feedlist.setmTrailerSize(post.optString(OWM_SIZE));

                feedlist.setmTrailerType(post.optString(OWM_TYPE));

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

            feedUserReviewses = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);

                FeedUserReviews feedlist = new FeedUserReviews();

                feedlist.setmUserNames(post.optString(OWM_USERNAME));
                feedlist.setmReviewsContent(post.optString(OWM_CONTENT));

                feedUserReviewses.add(feedlist);

            }
        } catch (Exception e) {
            mToast = Toast.makeText(this, "This is no reviews", Toast.LENGTH_SHORT);
            mToast.show();
        }

    }
}

