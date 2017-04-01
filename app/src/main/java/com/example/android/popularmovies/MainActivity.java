package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.MovieAdapter.ListItemClickListener;
import com.example.android.popularmovies.data.PopularMovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListItemClickListener, LoaderManager.LoaderCallbacks<String> {
    private String key = BuildConfig.API_KEY;

    private MainActivity mainActivity;

    private GridLayoutManager mlayoutManager;

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecycleView;

    private final String KEY_INSTANCE_STATE_RV_POSITION = "recycleViewKey";

    private MovieAdapter mMovieAdapter;

    private ArrayList<FeedItem> feedsList;

    private ArrayList<FeedItem> feedsFavoriteList;

    private Toast mToast;

    private String MOVIE_POPULAR_URL =
            "http://api.themoviedb.org/3/movie/popular?api_key=" + key;


    private String MOVIE_RATE_URL =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=" + key;

    private static final int GITHUB_SEARCH_LOADER = 22;

    private static final String SEARCH_POPULAR_URL_EXTRA = "popular";

    private static final String SEARCH_TOPRATED_URL_EXTRA = "topRated";

    private static final String SEARCH_FAVORITE_URL_EXTRA = "favorite";

    private static int feedListFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mainActivity = this;

        SetMarginOfGridlayout setMarginOfGridlayout = new SetMarginOfGridlayout(0);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecycleView = (RecyclerView) findViewById(R.id.rv_recycleview_PosterImage);

        mlayoutManager = new GridLayoutManager(this, numberOfColumns());

        mRecycleView.setLayoutManager(mlayoutManager);

        mRecycleView.addItemDecoration(setMarginOfGridlayout);

        mRecycleView.setHasFixedSize(true);


        if (savedInstanceState == null || (!savedInstanceState.containsKey("normalMovieList")
                && !savedInstanceState.containsKey("topRatedMovieList")
                && !savedInstanceState.containsKey("favoriteMovieList"))) {

            if (internet_connection()) {

                makeMovieSearchQuery(MOVIE_POPULAR_URL);

                mainActivity.setTitle(getString(R.string.popular_movie_title));
            } else {
                mToast = Toast.makeText(this, "This is no Internet", Toast.LENGTH_LONG);
                mToast.show();
            }
            mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);

            feedListFlag = 1;

        } else {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_INSTANCE_STATE_RV_POSITION);

            mRecycleView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);

            if (feedListFlag == 1) {

                feedsList = savedInstanceState.getParcelableArrayList("normalMovieList");

                mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);


            } else if (feedListFlag == 2) {

                mainActivity.setTitle(getString(R.string.topRated_movie_title));

                feedsList = savedInstanceState.getParcelableArrayList("topRatedMovieList");

                mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);

            } else {
                mainActivity.setTitle(getString(R.string.favorite_movie_title));

                feedsFavoriteList = savedInstanceState.getParcelableArrayList("favoriteMovieList");

                mMovieAdapter = new MovieAdapter(MainActivity.this, feedsFavoriteList, this);
            }
        }

        getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER, null, this);

        mRecycleView.setAdapter(mMovieAdapter);

    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
            }

            @Override
            public String loadInBackground() {
                return null;
            }


            @Override
            public void deliverResult(String githubJson) {
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        //do not need to anything
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //do not need to anything
    }

    private int numberOfColumns() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;

    }

    private void makeMovieSearchQuery(String Url) {

        URL MovieSearchUrl = NetworkUtils.buildUrl(Url);

        new FetchMovieTask().execute(MovieSearchUrl);

        //Bundle queryBundle = new Bundle();
        //queryBundle.putString(SEARCH_POPULAR_URL_EXTRA, MovieSearchUrl.toString());
    }


    private class FetchMovieTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            parseResult(movieResponse);

            if (feedsFavoriteList == null || feedsFavoriteList.size() == 0) {

                mMovieAdapter.setMovieData(feedsList);
            }

            if (feedsFavoriteList.size() != 0) {

                mMovieAdapter.setMovieData(feedsFavoriteList);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.movie, menu);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            int id = item.getItemId();

            if (id == R.id.action_sortByPopular) {

                mainActivity.setTitle(getString(R.string.popular_movie_title));

                makeMovieSearchQuery(MOVIE_POPULAR_URL);

                mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);

                mRecycleView.setAdapter(mMovieAdapter);

                return true;
            }

            if (id == R.id.action_sortByRated) {

                mainActivity.setTitle(getString(R.string.topRated_movie_title));

                makeMovieSearchQuery(MOVIE_RATE_URL);

                mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);

                mRecycleView.setAdapter(mMovieAdapter);

                return true;
            }

            if (id == R.id.action_favorite) {

                feedsFavoriteList = new ArrayList<>();

                mainActivity.setTitle(getString(R.string.favorite_movie_title));

                List<String> FavoriteUrlList = getAllFavoriteMovieURL();

                for (int i = 0; i < FavoriteUrlList.size(); i++) {

                    makeMovieSearchQuery(FavoriteUrlList.get(i));
                }
                mMovieAdapter = new MovieAdapter(MainActivity.this, feedsFavoriteList, this);

                mRecycleView.setAdapter(mMovieAdapter);

                return true;
            }
        } catch (Exception e) {
            mToast = Toast.makeText(this, "This is errors in menuSelected", Toast.LENGTH_LONG);

            Log.v("Error is : ", "" + e.getMessage());

            mToast.show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        if (feedListFlag == 1) {

            feedsList = savedInstanceState.getParcelableArrayList("normalMovieList");

            mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);


        } else if (feedListFlag == 2) {

            mainActivity.setTitle(getString(R.string.topRated_movie_title));

            feedsList = savedInstanceState.getParcelableArrayList("topRatedMovieList");

            mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);

        } else {
            mainActivity.setTitle(getString(R.string.favorite_movie_title));

            feedsFavoriteList = savedInstanceState.getParcelableArrayList("favoriteMovieList");

            mMovieAdapter = new MovieAdapter(MainActivity.this, feedsFavoriteList, this);
        }

        Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_INSTANCE_STATE_RV_POSITION);

        mRecycleView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
    }


    private boolean internet_connection() {

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private ArrayList<String> getAllFavoriteMovieURL() {
        ArrayList<String> movieUrlList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(PopularMovieContract.PopularMovieEntry.CONTENT_URI, new String[]{PopularMovieContract.PopularMovieEntry.COLUMN_MovieURL}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                movieUrlList.add(cursor.getString(cursor
                        .getColumnIndex(PopularMovieContract.PopularMovieEntry.COLUMN_MovieURL)));
            } while (cursor.moveToNext());
        } else {
            return null;
        }
        return movieUrlList;
    }

    private void parseResult(String movieJsonStr) {
        try {
            final String OWM_LIST = "results";

            final String OWM_TITLE = "original_title";

            final String OWM_OVERVIEW = "overview";

            final String OWM_RELEASEDATE = "release_date";

            final String OWM_VOTEAVERAGE = "vote_average";

            final String OWM_POSTERADRESS = "poster_path";

            final String OWM_POPULARITY = "popularity";

            final String OWM_MovieIDINTMDB = "id";

            JSONObject response = new JSONObject(movieJsonStr);

            JSONArray posts = null;

            posts = response.optJSONArray(OWM_LIST);

            if (posts != null) {

                feedsList = new ArrayList<>();

                feedsFavoriteList = new ArrayList<>();

                for (int i = 0; i < posts.length(); i++) {

                    JSONObject post = posts.optJSONObject(i);

                    FeedItem item = new FeedItem(post.optString(OWM_TITLE), post.optString(OWM_POSTERADRESS), post.optString(OWM_OVERVIEW)
                            , post.optString(OWM_RELEASEDATE).substring(0, 4), post.optString(OWM_VOTEAVERAGE),
                            post.optString(OWM_POPULARITY), post.optInt(OWM_MovieIDINTMDB));

                    item.setTitle(post.optString(OWM_TITLE));

                    item.setThumbnail(post.optString(OWM_POSTERADRESS));

                    item.setOverview(post.optString(OWM_OVERVIEW));

                    item.setReleaseDate(post.optString(OWM_RELEASEDATE).substring(0, 4));

                    item.setVoteAverage(post.optString(OWM_VOTEAVERAGE));

                    item.setPopularity(post.optString(OWM_POPULARITY));

                    item.setMovieIDInTMDB(post.optInt(OWM_MovieIDINTMDB));

                    feedsList.add(item);
                }

            } else {

                FeedItem item = new FeedItem(response.optString(OWM_TITLE), response.optString(OWM_POSTERADRESS),
                        response.optString(OWM_OVERVIEW), response.optString(OWM_RELEASEDATE).substring(0, 4)
                        , response.optString(OWM_VOTEAVERAGE), response.optString(OWM_POPULARITY), response.optInt(OWM_MovieIDINTMDB));

                item.setTitle(response.optString(OWM_TITLE));

                item.setThumbnail(response.optString(OWM_POSTERADRESS));

                item.setOverview(response.optString(OWM_OVERVIEW));

                item.setReleaseDate(response.optString(OWM_RELEASEDATE).substring(0, 4));

                item.setVoteAverage(response.optString(OWM_VOTEAVERAGE));

                item.setPopularity(response.optString(OWM_POPULARITY));

                item.setMovieIDInTMDB(response.optInt(OWM_MovieIDINTMDB));

                feedsFavoriteList.add(item);

            }
        } catch (Exception e) {
            mToast = Toast.makeText(this, "This is error in parseJson", Toast.LENGTH_LONG);
            Log.v("Error:", "" + e.getMessage());
            mToast.show();
        }
    }

    @Override
    public void onClick(int clickedItemIndex) {
        if (mainActivity.getTitle() != getString(R.string.favorite_movie_title)) {

            Context context = MainActivity.this;

            Class destinationActivity = ChildActivity.class;

            Intent startChildActivityIntent = new Intent(context, destinationActivity);

            String textTitle = feedsList.get(clickedItemIndex).getTitle();

            String textReleaseDate = feedsList.get(clickedItemIndex).getReleaseDate();

            String textOverview = feedsList.get(clickedItemIndex).getOverview();

            String textVoteAverage = feedsList.get(clickedItemIndex).getVoteAverage();

            String urlThumbnail = feedsList.get(clickedItemIndex).getThumbnail();

            int numberMovieIDInTMDB = feedsList.get(clickedItemIndex).getMovieIDInTMDB();

            Bundle extras = new Bundle();

            extras.putString("title", textTitle);
            extras.putString("releaseDate", textReleaseDate);
            extras.putString("overview", textOverview);
            extras.putString("voteAverage", textVoteAverage);
            extras.putString("Thumbnail", urlThumbnail);
            extras.putInt("id", numberMovieIDInTMDB);

            startChildActivityIntent.putExtras(extras);

            startActivity(startChildActivityIntent);
        } else {

            Context context = MainActivity.this;

            Class destinationActivity = ChildActivity.class;

            Intent startChildActivityIntent = new Intent(context, destinationActivity);

            String textTitle = feedsFavoriteList.get(clickedItemIndex).getTitle();

            String textReleaseDate = feedsFavoriteList.get(clickedItemIndex).getReleaseDate();

            String textOverview = feedsFavoriteList.get(clickedItemIndex).getOverview();

            String textVoteAverage = feedsFavoriteList.get(clickedItemIndex).getVoteAverage();

            String urlThumbnail = feedsFavoriteList.get(clickedItemIndex).getThumbnail();

            int numberMovieIDInTMDB = feedsFavoriteList.get(clickedItemIndex).getMovieIDInTMDB();

            Bundle extras = new Bundle();

            extras.putString("title", textTitle);
            extras.putString("releaseDate", textReleaseDate);
            extras.putString("overview", textOverview);
            extras.putString("voteAverage", textVoteAverage);
            extras.putString("Thumbnail", urlThumbnail);
            extras.putInt("id", numberMovieIDInTMDB);

            startChildActivityIntent.putExtras(extras);

            startActivity(startChildActivityIntent);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        if (mainActivity.getTitle() == getString(R.string.popular_movie_title)) {

            outState.putParcelableArrayList("normalMovieList", feedsList);

            feedListFlag = 1;

        } else if (mainActivity.getTitle() == getString(R.string.topRated_movie_title)) {

            outState.putParcelableArrayList("topRatedMovieList", feedsList);

            feedListFlag = 2;

        } else {
            outState.putParcelableArrayList("favoriteMovieList", feedsFavoriteList);

            feedListFlag = 3;

        }

        outState.putParcelable(KEY_INSTANCE_STATE_RV_POSITION, mlayoutManager.onSaveInstanceState());

        super.onSaveInstanceState(outState);
    }

}
