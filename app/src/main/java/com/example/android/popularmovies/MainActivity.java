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

public class MainActivity extends AppCompatActivity implements ListItemClickListener {
    private String key = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private MainActivity mainActivity;

    private GridLayoutManager mLayoutManager;

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecycleView;

    private final String KEY_INSTANCE_STATE_RV_POSITION = "recycleViewKey";

    private final String OWM_LIST = "results";

    private final String OWM_TITLE = "original_title";

    private final String OWM_OVERVIEW = "overview";

    private final String OWM_RELEASEDATE = "release_date";

    private final String OWM_VOTEAVERAGE = "vote_average";

    private final String OWM_POSTERADRESS = "poster_path";

    private final String OWM_POPULARITY = "popularity";

    private final String OWM_MovieIDINTMDB = "id";

    private MovieAdapter mMovieAdapter;

    private ArrayList<FeedItem> PopMoviesList = new ArrayList<>();

    private ArrayList<FeedItem> TopRatedMoviesList = new ArrayList<>();

    private ArrayList<FeedItem> FavoriteMoviesList = new ArrayList<>();

    private Toast mToast;

    private String MOVIE_POPULAR_URL =
            "http://api.themoviedb.org/3/movie/popular?api_key=" + key + "&language=en-US&page=";


    private String MOVIE_RATE_URL =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=" + key + "&language=en-US&page=";


    private static int MoviesInstanceStateFlag;

    private static boolean ParsingPopMoviesJson = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mainActivity = this;

        SetMarginOfGridlayout setMarginOfGridlayout = new SetMarginOfGridlayout(0);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecycleView = (RecyclerView) findViewById(R.id.rv_recycleview_PosterImage);

        mLayoutManager = new GridLayoutManager(this, numberOfColumns());

        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.addItemDecoration(setMarginOfGridlayout);

        mRecycleView.setHasFixedSize(true);

        selectList(savedInstanceState);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                makeMovieSearchQuery(ParsingPopMoviesJson ? MOVIE_POPULAR_URL + String.valueOf(page) : MOVIE_RATE_URL + String.valueOf(page));

                final int curSize = mMovieAdapter.getItemCount();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        // Notify adapter with appropriate notify methods
                        mMovieAdapter.notifyItemRangeInserted(curSize, ParsingPopMoviesJson? PopMoviesList.size() - 1 : TopRatedMoviesList.size() - 1);
                    }
                });

            }
        };
        mRecycleView.addOnScrollListener(endlessRecyclerViewScrollListener);

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

            if (PopMoviesList.size() != 0) {

                mMovieAdapter.setMovieData(PopMoviesList);
            }

            if (TopRatedMoviesList.size() != 0) {

                mMovieAdapter.setMovieData(TopRatedMoviesList);
            }

            if (FavoriteMoviesList.size() != 0) {

                mMovieAdapter.setMovieData(FavoriteMoviesList);
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
        PopMoviesList.clear();
        TopRatedMoviesList.clear();
        FavoriteMoviesList.clear();
        try {
            int id = item.getItemId();

            if (id == R.id.action_sortByPopular) {

                ParsingPopMoviesJson = true;

                mainActivity.setTitle(getString(R.string.popular_movie_title));

                makeMovieSearchQuery(MOVIE_POPULAR_URL);

                mMovieAdapter = new MovieAdapter(MainActivity.this, PopMoviesList, this);

                mRecycleView.setAdapter(mMovieAdapter);

                return true;
            }

            if (id == R.id.action_sortByRated) {

                ParsingPopMoviesJson = false;

                mainActivity.setTitle(getString(R.string.topRated_movie_title));

                makeMovieSearchQuery(MOVIE_RATE_URL);

                mMovieAdapter = new MovieAdapter(MainActivity.this, TopRatedMoviesList, this);

                mRecycleView.setAdapter(mMovieAdapter);

                return true;
            }

            if (id == R.id.action_favorite) {

                FavoriteMoviesList = new ArrayList<>();

                List<String> FavoriteUrlList = getAllFavoriteMovieURL();

                if (FavoriteUrlList != null && FavoriteUrlList.size() > 0) {

                    for (int i = 0; i < FavoriteUrlList.size(); i++) {

                        makeMovieSearchQuery(FavoriteUrlList.get(i));
                    }

                    mainActivity.setTitle(getString(R.string.favorite_movie_title));

                    mMovieAdapter = new MovieAdapter(MainActivity.this, FavoriteMoviesList, this);

                    mRecycleView.setAdapter(mMovieAdapter);
                } else {
                    Toast.makeText(this, "This is no favorite movie in list", Toast.LENGTH_LONG).show();
                }

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

        if (MoviesInstanceStateFlag == 1) {

            PopMoviesList = savedInstanceState.getParcelableArrayList("normalMovieList");

            mMovieAdapter = new MovieAdapter(MainActivity.this, PopMoviesList, this);


        } else if (MoviesInstanceStateFlag == 2) {

            mainActivity.setTitle(getString(R.string.topRated_movie_title));

            PopMoviesList = savedInstanceState.getParcelableArrayList("topRatedMovieList");

            mMovieAdapter = new MovieAdapter(MainActivity.this, PopMoviesList, this);

        } else {
            mainActivity.setTitle(getString(R.string.favorite_movie_title));

            FavoriteMoviesList = savedInstanceState.getParcelableArrayList("favoriteMovieList");

            mMovieAdapter = new MovieAdapter(MainActivity.this, FavoriteMoviesList, this);
        }

        Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_INSTANCE_STATE_RV_POSITION);

        mRecycleView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
    }


    @Override
    public void onClick(int clickedItemIndex) {
        if (mainActivity.getTitle() != getString(R.string.favorite_movie_title)) {

            Context context = MainActivity.this;

            Class destinationActivity = ChildActivity.class;

            Intent startChildActivityIntent = new Intent(context, destinationActivity);

            String textTitle = PopMoviesList.get(clickedItemIndex).getTitle();

            String textReleaseDate = PopMoviesList.get(clickedItemIndex).getRelease_date();

            String textOverview = PopMoviesList.get(clickedItemIndex).getOverview();

            String textVoteAverage = PopMoviesList.get(clickedItemIndex).getVote_count();

            String urlThumbnail = PopMoviesList.get(clickedItemIndex).getPoster_path();

            int numberMovieIDInTMDB = PopMoviesList.get(clickedItemIndex).getId();

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

            String textTitle = FavoriteMoviesList.get(clickedItemIndex).getTitle();

            String textReleaseDate = FavoriteMoviesList.get(clickedItemIndex).getRelease_date();

            String textOverview = FavoriteMoviesList.get(clickedItemIndex).getOverview();

            String textVoteAverage = FavoriteMoviesList.get(clickedItemIndex).getVote_count();

            String urlThumbnail = FavoriteMoviesList.get(clickedItemIndex).getPoster_path();

            int numberMovieIDInTMDB = FavoriteMoviesList.get(clickedItemIndex).getId();

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

            outState.putParcelableArrayList("normalMovieList", PopMoviesList);

            MoviesInstanceStateFlag = 1;

        } else if (mainActivity.getTitle() == getString(R.string.topRated_movie_title)) {

            outState.putParcelableArrayList("topRatedMovieList", TopRatedMoviesList);

            MoviesInstanceStateFlag = 2;

        } else {
            outState.putParcelableArrayList("favoriteMovieList", FavoriteMoviesList);

            MoviesInstanceStateFlag = 3;

        }

        outState.putParcelable(KEY_INSTANCE_STATE_RV_POSITION, mLayoutManager.onSaveInstanceState());

        super.onSaveInstanceState(outState);
    }


    private boolean internet_connection() {

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void parseResult(String movieJsonStr) {
        try {

            JSONObject response = new JSONObject(movieJsonStr);

            JSONArray posts;

            posts = response.optJSONArray(OWM_LIST);

            if (posts != null) {

                //PopMoviesList = new ArrayList<>();

                //FavoriteMoviesList = new ArrayList<>();

                for (int i = 0; i < posts.length(); i++) {

                    JSONObject post = posts.optJSONObject(i);

                    FeedItem item = new FeedItem(post.optString(OWM_TITLE), post.optString(OWM_POSTERADRESS), post.optString(OWM_OVERVIEW)
                            , post.optString(OWM_RELEASEDATE).substring(0, 4), post.optString(OWM_VOTEAVERAGE),
                            post.optString(OWM_POPULARITY), post.optInt(OWM_MovieIDINTMDB));

                    if (ParsingPopMoviesJson) {
                        PopMoviesList.add(item);
                    } else {
                        TopRatedMoviesList.add(item);
                    }
                }

            } else {

                FeedItem item = new FeedItem(response.optString(OWM_TITLE), response.optString(OWM_POSTERADRESS),
                        response.optString(OWM_OVERVIEW), response.optString(OWM_RELEASEDATE).substring(0, 4)
                        , response.optString(OWM_VOTEAVERAGE), response.optString(OWM_POPULARITY), response.optInt(OWM_MovieIDINTMDB));

                FavoriteMoviesList.add(item);

            }
        } catch (Exception e) {
            mToast = Toast.makeText(this, "This is error in parseJson", Toast.LENGTH_LONG);
            Log.v("Error:", "" + e.getMessage());
            mToast.show();
        }
    }

    private void makeMovieSearchQuery(String Url) {

        if (Url.contains("top_rated")) {
            ParsingPopMoviesJson = false;
        }
        URL MovieSearchUrl = NetworkUtils.buildUrl(Url);

        new FetchMovieTask().execute(MovieSearchUrl);

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private ArrayList<String> getAllFavoriteMovieURL() {
        ArrayList<String> movieUrlList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(PopularMovieContract.PopularMovieEntry.CONTENT_URI, new String[]{PopularMovieContract.PopularMovieEntry.COLUMN_MOVIE_URL}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                movieUrlList.add(cursor.getString(cursor
                        .getColumnIndex(PopularMovieContract.PopularMovieEntry.COLUMN_MOVIE_URL)));
            } while (cursor.moveToNext());
        } else {
            return null;
        }
        return movieUrlList;
    }

    private void selectList(Bundle savedInstanceState) {

        if (savedInstanceState == null || (!savedInstanceState.containsKey("normalMovieList")
                && !savedInstanceState.containsKey("topRatedMovieList")
                && !savedInstanceState.containsKey("favoriteMovieList"))) {

            if (internet_connection()) {

                makeMovieSearchQuery(MOVIE_POPULAR_URL + "1");

                mainActivity.setTitle(getString(R.string.popular_movie_title));
            } else {
                mToast = Toast.makeText(this, "This is no Internet", Toast.LENGTH_LONG);
                mToast.show();
            }
            mMovieAdapter = new MovieAdapter(MainActivity.this, PopMoviesList, this);

            MoviesInstanceStateFlag = 1;


        } else {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_INSTANCE_STATE_RV_POSITION);

            mRecycleView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);

            if (MoviesInstanceStateFlag == 1) {

                PopMoviesList = savedInstanceState.getParcelableArrayList("normalMovieList");

                mMovieAdapter = new MovieAdapter(MainActivity.this, PopMoviesList, this);


            } else if (MoviesInstanceStateFlag == 2) {

                mainActivity.setTitle(getString(R.string.topRated_movie_title));

                TopRatedMoviesList = savedInstanceState.getParcelableArrayList("topRatedMovieList");

                mMovieAdapter = new MovieAdapter(MainActivity.this, TopRatedMoviesList, this);

            } else {
                mainActivity.setTitle(getString(R.string.favorite_movie_title));

                FavoriteMoviesList = savedInstanceState.getParcelableArrayList("favoriteMovieList");

                mMovieAdapter = new MovieAdapter(MainActivity.this, FavoriteMoviesList, this);
            }
        }

        mRecycleView.setAdapter(mMovieAdapter);

    }


}
