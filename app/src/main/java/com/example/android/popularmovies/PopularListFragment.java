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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies.data.PopularMovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PopularListFragment extends Fragment implements MovieAdapter.ListItemClickListener {

    @BindView(R.id.rv_recycleview_PosterImage)
    RecyclerView mRecycleView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private String key = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    private GridLayoutManager mLayoutManager;

    private MainActivity mMainActivity;

    private Gson mGson;

    private RequestQueue mRequestQueue;

    private JSONResultList ResultList = new JSONResultList();

    private List<FeedItem> PopMoviesList = new ArrayList<>();

    private List<FeedItem> TopRatedMoviesList = new ArrayList<>();

    private List<FeedItem> FavoriteMoviesList = new ArrayList<>();

    private MovieAdapter mMovieAdapter;

    private final String KEY_INSTANCE_STATE_RV_POSITION = "recycleViewKey";

    private String MOVIE_POPULAR_URL =
            "http://api.themoviedb.org/3/movie/popular?api_key=" + key + "&language=en-US&page=";

    private String MOVIE_RATE_URL =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=" + key + "&language=en-US&page=";

    private static boolean ParsingPopMoviesJson = true;

    private Toast mToast;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public PopularListFragment() {

    }

    public PopularListFragment newInstance() {
        PopularListFragment fragment = new PopularListFragment();
        //Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        //loadDataToArrayList();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        //loadDataToArrayList();

        if (internet_connection()) {
            mRequestQueue = Volley.newRequestQueue(MainActivity.getmContext());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            mGson = gsonBuilder.create();

            StringRequest requestForPopularMovies = new StringRequest(Request.Method.GET, MOVIE_POPULAR_URL + "1", onPostsLoadedPopular, onPostsError);

            mRequestQueue.add(requestForPopularMovies);

        }

        //mMovieAdapter = new MovieAdapter(MainActivity.getmContext(), PopMoviesList, this);


        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mainpage, container, false);

        ButterKnife.bind(this, view);

        SetMarginOfGridlayout setMarginOfGridlayout = new SetMarginOfGridlayout(0);

        mLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());

        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.addItemDecoration(setMarginOfGridlayout);

        mRecycleView.setHasFixedSize(true);

        EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener
                = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {


                StringRequest requestForPopularMovies = new StringRequest(Request.Method.GET, MOVIE_POPULAR_URL + String.valueOf(page), onPostsLoadedPopular, onPostsError);

                mRequestQueue.add(requestForPopularMovies);

                final int curSize = mMovieAdapter.getItemCount();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        // Notify adapter with appropriate notify methods
                        mMovieAdapter.notifyItemRangeInserted(curSize, PopMoviesList.size() - 1);
                    }
                });

            }
        };
        mRecycleView.addOnScrollListener(endlessRecyclerViewScrollListener);

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_INSTANCE_STATE_RV_POSITION);
            mRecycleView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_INSTANCE_STATE_RV_POSITION, mRecycleView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onClick(int clickedItemIndex) {

        String textTitle;

        String textReleaseDate;

        String textOverview;

        Double textVoteAverage;

        String urlThumbnail;

        int numberMovieIDInTMDB;

        Class destinationActivity = ChildActivity.class;

        Intent startChildActivityIntent = new Intent(getContext(), destinationActivity);

        textTitle = PopMoviesList.get(clickedItemIndex).getTitle();

        textReleaseDate = PopMoviesList.get(clickedItemIndex).getReleaseDate();

        textOverview = PopMoviesList.get(clickedItemIndex).getOverview();

        textVoteAverage = PopMoviesList.get(clickedItemIndex).getVoteAverage();

        urlThumbnail = PopMoviesList.get(clickedItemIndex).getPosterPath();

        numberMovieIDInTMDB = PopMoviesList.get(clickedItemIndex).getId();


        Bundle extras = new Bundle();

        extras.putString("title", textTitle);
        extras.putString("releaseDate", textReleaseDate);
        extras.putString("overview", textOverview);
        extras.putDouble("voteAverage", textVoteAverage);
        extras.putString("Thumbnail", urlThumbnail);
        extras.putInt("id", numberMovieIDInTMDB);

        startChildActivityIntent.putExtras(extras);

        startActivity(startChildActivityIntent);
    }

    private class FetchMovieTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
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

            //mLoadingIndicator.setVisibility(View.INVISIBLE);

            //parseResult(movieResponse);

            if (getArguments() != null && getArguments().getInt(ARG_SECTION_NUMBER) == 1) {

                mMovieAdapter.setMovieData(PopMoviesList);
            }

            if (getArguments() != null && getArguments().getInt(ARG_SECTION_NUMBER) == 2) {

                mMovieAdapter.setMovieData(TopRatedMoviesList);
            }

            if (getArguments() != null && getArguments().getInt(ARG_SECTION_NUMBER) == 3) {

                mMovieAdapter.setMovieData(FavoriteMoviesList);
            }
            if (getArguments() == null) {

                mMovieAdapter.setMovieData(PopMoviesList);
            }
        }
    }

    private void loadDataToArrayList() {


        makeMovieSearchQuery(MOVIE_POPULAR_URL + String.valueOf(1));

        makeMovieSearchQuery(MOVIE_RATE_URL + String.valueOf(1));

        List<String> FavoriteUrlList = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            FavoriteUrlList = getAllFavoriteMovieURL();
        }

        if (FavoriteUrlList != null && FavoriteUrlList.size() > 0) {

            for (int i = 0; i < FavoriteUrlList.size(); i++) {

                makeMovieSearchQuery(FavoriteUrlList.get(i));
            }

        } else {
            Toast.makeText(getActivity(), "This is no favorite movie in list", Toast.LENGTH_LONG).show();
        }


    }

    private final Response.Listener<String> onPostsLoadedPopular = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            ResultList = mGson.fromJson(response, JSONResultList.class);

            if (PopMoviesList == null || PopMoviesList.size() == 0) {

                PopMoviesList = ResultList.getResults();

                mMovieAdapter = new MovieAdapter(MainActivity.getmContext(), PopMoviesList, PopularListFragment.this);

                mRecycleView.setAdapter(mMovieAdapter);
            } else {

                PopMoviesList.addAll(ResultList.getResults());
            }


        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mToast = Toast.makeText(MainActivity.getmContext(), "There is error in popular movies Json", Toast.LENGTH_LONG);
            mToast.show();
        }
    };

    private static boolean internet_connection() {

        ConnectivityManager cm =
                (ConnectivityManager) MainActivity.getmContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private ArrayList<String> getAllFavoriteMovieURL() {
        ArrayList<String> movieUrlList = new ArrayList<>();
        Cursor cursor = MainActivity.getmContext().getContentResolver().query(PopularMovieContract.PopularMovieEntry.CONTENT_URI, new String[]{PopularMovieContract.PopularMovieEntry.COLUMN_MOVIE_URL}, null, null, null, null);
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


    private int numberOfColumns() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;

    }

    public void makeMovieSearchQuery(String Url) {

        if (Url.contains("top_rated")) {
            ParsingPopMoviesJson = false;
        }
        URL MovieSearchUrl = NetworkUtils.buildUrl(Url);

        new FetchMovieTask().execute(MovieSearchUrl);

    }


    /*private void parseResult(String movieJsonStr) {

        final String OWM_LIST = "results";

        final String OWM_TITLE = "original_title";

        final String OWM_OVERVIEW = "overview";

        final String OWM_RELEASEDATE = "release_date";

        final String OWM_VOTEAVERAGE = "vote_average";

        final String OWM_POSTERADDRESS = "poster_path";

        final String OWM_POPULARITY = "popularity";

        final String OWM_MovieIDINTMDB = "id";
        try {

            JSONObject response = new JSONObject(movieJsonStr);

            JSONArray posts;

            posts = response.optJSONArray(OWM_LIST);

            if (posts != null) {

                for (int i = 0; i < posts.length(); i++) {

                    JSONObject post = posts.optJSONObject(i);

                    FeedItem item = new FeedItem(post.optString(OWM_TITLE), post.optString(OWM_POSTERADDRESS), post.optString(OWM_OVERVIEW)
                            , post.optString(OWM_RELEASEDATE).substring(0, 4), post.optString(OWM_VOTEAVERAGE),
                            post.optString(OWM_POPULARITY), post.optInt(OWM_MovieIDINTMDB));

                    if (ParsingPopMoviesJson) {
                        PopMoviesList.add(item);
                    } else {
                        TopRatedMoviesList.add(item);
                    }
                }

            } else {

                FeedItem item = new FeedItem(response.optString(OWM_TITLE), response.optString(OWM_POSTERADDRESS),
                        response.optString(OWM_OVERVIEW), response.optString(OWM_RELEASEDATE).substring(0, 4)
                        , response.optString(OWM_VOTEAVERAGE), response.optString(OWM_POPULARITY), response.optInt(OWM_MovieIDINTMDB));

                FavoriteMoviesList.add(item);

            }
        } catch (Exception e) {
            mToast = Toast.makeText(getActivity(), "This is error in parseJson", Toast.LENGTH_LONG);
            Log.v("Error:", "" + e.getMessage());
            mToast.show();
        }
    }*/

}
