package com.jcMobile.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jcMobile.android.popularmovies.Adapters.MovieAdapter;
import com.jcMobile.android.popularmovies.DataList.FeedItem;
import com.jcMobile.android.popularmovies.data.PopularMovieContract;
import com.jcMobile.android.popularmovies.utilities.SetMarginOfGridlayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteListFragment extends Fragment implements MovieAdapter.ListItemClickListener {

    @BindView(com.jcMobile.android.popularmovies.R.id.mainPage_CoordinatorLayout)
    CoordinatorLayout mLayout;

    @BindView(com.jcMobile.android.popularmovies.R.id.rv_recycleview_PosterImage)
    RecyclerView mRecycleView;

    @BindView(com.jcMobile.android.popularmovies.R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private GridLayoutManager mLayoutManager;

    private Gson mGson;

    private MainActivity mMainactivity;

    private boolean snakeBarAppear = false;

    private RequestQueue mRequestQueue;

    private FeedItem feeditem;

    private final List<FeedItem> FavoriteMoviesList = new ArrayList<>();

    private MovieAdapter mMovieAdapter;

    private final String KEY_INSTANCE_STATE_RV_POSITION = "recycleViewKey";

    public FavoriteListFragment() {

    }

    public FavoriteListFragment newInstance() {
        FavoriteListFragment fragment = new FavoriteListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        if (internet_connection()) {
            mRequestQueue = Volley.newRequestQueue(mMainactivity.getmContext());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            mGson = gsonBuilder.create();

            List<String> FavoriteUrlList = null;


            FavoriteUrlList = getAllFavoriteMovieURL();


            if (FavoriteUrlList != null && FavoriteUrlList.size() > 0) {

                for (int i = 0; i < FavoriteUrlList.size(); i++) {

                    StringRequest requestForPopularMovies = new StringRequest(Request.Method.GET, FavoriteUrlList.get(i), onPostsLoadedPopular, onPostsError);

                    mRequestQueue.add(requestForPopularMovies);
                }

            } else {
                Toast.makeText(getActivity(), "This is no favorite movie in list", Toast.LENGTH_LONG).show();
            }

            snakeBarAppear = false;

        } else {
            snakeBarAppear = true;
        }

        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(com.jcMobile.android.popularmovies.R.layout.fragment_mainpage, container, false);

        ButterKnife.bind(this, view);

        SetMarginOfGridlayout setMarginOfGridlayout = new SetMarginOfGridlayout(0);

        mLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());

        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.addItemDecoration(setMarginOfGridlayout);

        mRecycleView.setHasFixedSize(true);

        if(snakeBarAppear) showSnakeBar();

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
    public void onClick(int clickedItemIndex, ImageView sharedImageView) {

        String textTitle;

        String textReleaseDate;

        String textOverview;

        Double textVoteAverage;

        String urlThumbnail;

        int numberMovieIDInTMDB;

        Class destinationActivity = ChildActivity.class;

        Intent startChildActivityIntent = new Intent(getContext(), destinationActivity);

        textTitle = FavoriteMoviesList.get(clickedItemIndex).getTitle();

        textReleaseDate = FavoriteMoviesList.get(clickedItemIndex).getReleaseDate();

        textOverview = FavoriteMoviesList.get(clickedItemIndex).getOverview();

        textVoteAverage = FavoriteMoviesList.get(clickedItemIndex).getVoteAverage();

        urlThumbnail = FavoriteMoviesList.get(clickedItemIndex).getPosterPath();

        numberMovieIDInTMDB = FavoriteMoviesList.get(clickedItemIndex).getId();

        Bundle extras = new Bundle();

        extras.putString("title", textTitle);
        extras.putString("releaseDate", textReleaseDate);
        extras.putString("overview", textOverview);
        extras.putDouble("voteAverage", textVoteAverage);
        extras.putString("Thumbnail", urlThumbnail);
        extras.putInt("id", numberMovieIDInTMDB);
        extras.putString("transitionName", ViewCompat.getTransitionName(sharedImageView));

        String transitionName = getString(R.string.transition_name);

        startChildActivityIntent.putExtras(extras);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                sharedImageView,   // Starting view
                transitionName    // The String
        );
        //Start the Intent
        ActivityCompat.startActivity(getActivity(), startChildActivityIntent, options.toBundle());
    }


    private final Response.Listener<String> onPostsLoadedPopular = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            feeditem = mGson.fromJson(response, FeedItem.class);

            FavoriteMoviesList.add(feeditem);

            mMovieAdapter = new MovieAdapter(MainActivity.getmContext(), FavoriteMoviesList, FavoriteListFragment.this);

            mRecycleView.setAdapter(mMovieAdapter);

        }
    };


    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            showSnakeBar();
        }
    };

    private boolean internet_connection() {

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
        cursor.close();
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

    private void showSnakeBar() {
        String message = getString(com.jcMobile.android.popularmovies.R.string.no_internet_connection);
        Snackbar snackbar = Snackbar
                .make(mLayout, message, Snackbar.LENGTH_LONG);
        View SnakeView = snackbar.getView();
        SnakeView.setBackgroundColor(Color.WHITE);
        snackbar.show();
    }

}
