package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.MovieAdapter.ListItemClickListener;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListItemClickListener {

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecycleView;

    private MovieAdapter mMovieAdapter;

    private List<FeedItem> feedsList;

    private Toast mToast;

    private  String MOVIE_POPULAR_URL =
            "http://api.themoviedb.org/3/movie/popular?api_key=d8a3dca970a92dfe743a515a7802a807";

    private  String MOVIE_RATE_URL =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=d8a3dca970a92dfe743a515a7802a807";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetMarginOfGridlayout setMarginOfGridlayout = new SetMarginOfGridlayout(0);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecycleView = (RecyclerView) findViewById(R.id.rv_recycleview_PosterImage);

        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns());

        mRecycleView.setLayoutManager(layoutManager);

        mRecycleView.addItemDecoration(setMarginOfGridlayout);

        mRecycleView.setHasFixedSize(true);

        if (internet_connection()) {
           makeMovieSearchQuery(MOVIE_POPULAR_URL);

        } else {
            mToast = Toast.makeText(this, "This is no Internet", Toast.LENGTH_LONG);
            mToast.show();
        }

        mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);

        mRecycleView.setAdapter(mMovieAdapter);

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

            mMovieAdapter.setWeatherData(feedsList);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.movie, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            int id = item.getItemId();

            if (id == R.id.action_sortByPopular) {

                makeMovieSearchQuery(MOVIE_POPULAR_URL);

                mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);

                mRecycleView.setAdapter(mMovieAdapter);


                return true;
            }

            if (id == R.id.action_sortByRated) {

                makeMovieSearchQuery(MOVIE_RATE_URL);

                mMovieAdapter = new MovieAdapter(MainActivity.this, feedsList, this);

                mRecycleView.setAdapter(mMovieAdapter);

                return true;
            }

            if(id == R.id.action_favorate){

                //TODO Going to use ContentProvider to call database related to favorite movies


                return true;
            }
        } catch (Exception e) {
            mToast = Toast.makeText(this, "This is no Internet", Toast.LENGTH_LONG);
            mToast.show();
        }
        return super.onOptionsItemSelected(item);
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
            final String OWM_LIST = "results";

            final String OWM_TITLE = "original_title";

            final String OWM_OVERVIEW = "overview";

            final String OWM_RELEASEDATE = "release_date";

            final String OWM_VOTEAVERAGE = "vote_average";

            final String OWM_POSTERADRESS = "poster_path";

            final String OWM_POPULARITY = "popularity";

            final String OWM_MovieIDINTMDB = "id";

            JSONObject response = new JSONObject(movieJsonStr);

            JSONArray posts = response.optJSONArray(OWM_LIST);

            feedsList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {

                JSONObject post = posts.optJSONObject(i);

                FeedItem item = new FeedItem();

                item.setTitle(post.optString(OWM_TITLE));

                item.setThumbnail(post.optString(OWM_POSTERADRESS));

                item.setOverview(post.optString(OWM_OVERVIEW));

                item.setReleaseDate(post.optString(OWM_RELEASEDATE));

                item.setVoteAverage(post.optString(OWM_VOTEAVERAGE));

                item.setPopularity(post.optString(OWM_POPULARITY));

                item.setMovieIDInTMDB(post.optInt(OWM_MovieIDINTMDB));

                feedsList.add(item);

            }
        } catch (Exception e) {
            mToast = Toast.makeText(this, "This is no Internet", Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    @Override
    public void onClick(int clickedItemIndex) {

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
        extras.putInt("id",numberMovieIDInTMDB);

        startChildActivityIntent.putExtras(extras);

        startActivity(startChildActivityIntent);
    }


}
