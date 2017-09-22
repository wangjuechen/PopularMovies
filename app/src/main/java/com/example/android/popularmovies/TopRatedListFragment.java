package com.example.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TopRatedListFragment extends Fragment implements MovieAdapter.ListItemClickListener {

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


    private List<FeedItem> TopRatedMoviesList = new ArrayList<>();

    private MovieAdapter mMovieAdapter;

    private final String KEY_INSTANCE_STATE_RV_POSITION = "recycleViewKey";

    private String MOVIE_RATE_URL =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=" + key + "&language=en-US&page=";


    private Toast mToast;


    public TopRatedListFragment() {

    }

    public TopRatedListFragment newInstance() {
        TopRatedListFragment fragment = new TopRatedListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        if (internet_connection()) {
            mRequestQueue = Volley.newRequestQueue(MainActivity.getmContext());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            mGson = gsonBuilder.create();

            StringRequest requestForTopRatedMovies = new StringRequest(Request.Method.GET, MOVIE_RATE_URL + "1", onPostsLoadedTopRated, onPostsError);

            mRequestQueue.add(requestForTopRatedMovies);

        }

        //mMovieAdapter = new MovieAdapter(MainActivity.getmContext(), TopRatedMoviesList, this);
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


                StringRequest requestForTopRatedMovies = new StringRequest(Request.Method.GET, MOVIE_RATE_URL + String.valueOf(page), onPostsLoadedTopRated, onPostsError);

                mRequestQueue.add(requestForTopRatedMovies);


                final int curSize = mMovieAdapter.getItemCount();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        // Notify adapter with appropriate notify methods
                        mMovieAdapter.notifyItemRangeInserted(curSize, TopRatedMoviesList.size() - 1);
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

        textTitle = TopRatedMoviesList.get(clickedItemIndex).getTitle();

        textReleaseDate = TopRatedMoviesList.get(clickedItemIndex).getReleaseDate();

        textOverview = TopRatedMoviesList.get(clickedItemIndex).getOverview();

        textVoteAverage = TopRatedMoviesList.get(clickedItemIndex).getVoteAverage();

        urlThumbnail = TopRatedMoviesList.get(clickedItemIndex).getPosterPath();

        numberMovieIDInTMDB = TopRatedMoviesList.get(clickedItemIndex).getId();


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


    private final Response.Listener<String> onPostsLoadedTopRated = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            ResultList = mGson.fromJson(response, JSONResultList.class);

            if (TopRatedMoviesList == null || TopRatedMoviesList.size() == 0) {

                TopRatedMoviesList = ResultList.getResults();

                mMovieAdapter = new MovieAdapter(MainActivity.getmContext(), TopRatedMoviesList, TopRatedListFragment.this);

                mRecycleView.setAdapter(mMovieAdapter);

            } else {

                TopRatedMoviesList.addAll(ResultList.getResults());
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

}
