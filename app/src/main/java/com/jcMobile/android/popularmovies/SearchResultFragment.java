package com.jcMobile.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jcMobile.android.popularmovies.Adapters.MovieAdapter;
import com.jcMobile.android.popularmovies.DataList.FeedItem;
import com.jcMobile.android.popularmovies.DataList.JSONResultList;
import com.jcMobile.android.popularmovies.utilities.SetMarginOfGridlayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment implements MovieAdapter.ListItemClickListener {

    @BindView(com.jcMobile.android.popularmovies.R.id.researchresult_linearlayout)
    CoordinatorLayout mLayout;

    private final String KEY_INSTANCE_STATE_RV_POSITION = "recycleViewKey";

    private final String key = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    private static final String ARG_QUERY_PARAM = "search_movie_title";

    final private String QUERY_URL = "https://api.themoviedb.org/3/search/movie?api_key=" + key + "&language=en-US&query=";

    private String mFullQueryUrl;

    private GridLayoutManager mLayoutManager;

    private boolean snakeBarAppear = false;

    private MainActivity mMainActivity;

    private Gson mGson;

    private RequestQueue mRequestQueue;

    private JSONResultList ResultList;

    private List<FeedItem> ResultMoviesList = new ArrayList<>();

    private MovieAdapter mMovieAdapter;

    @BindView(com.jcMobile.android.popularmovies.R.id.rv_searchResult)
     RecyclerView mRecycleView;

    @BindView(com.jcMobile.android.popularmovies.R.id.tv_searchResultTitle)
     TextView mSearchTitle;

    // TODO: Rename and change types of parameters
    private String mQueryMovieTitle;


    private OnFragmentInteractionListener mListener;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SearchResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultFragment newInstance(String param1) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY_PARAM, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {

            mQueryMovieTitle = getArguments().getString(ARG_QUERY_PARAM);

            mFullQueryUrl = QUERY_URL + Uri.encode(mQueryMovieTitle);

        }

        if (internet_connection()) {
            mRequestQueue = Volley.newRequestQueue(mMainActivity.getmContext());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            mGson = gsonBuilder.create();

            StringRequest requestForPopularMovies = new StringRequest(Request.Method.GET, mFullQueryUrl, onPostsLoadedPopular, onPostsError);

            mRequestQueue.add(requestForPopularMovies);

            snakeBarAppear = false;

        }else{
            snakeBarAppear = true;
        }

        super.onCreate(savedInstanceState);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(com.jcMobile.android.popularmovies.R.layout.fragment_search_result, container, false);

        ButterKnife.bind(this, view);

        SetMarginOfGridlayout setMarginOfGridlayout = new SetMarginOfGridlayout(0);

        mLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());

        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.addItemDecoration(setMarginOfGridlayout);

        if(snakeBarAppear) showSnakeBar();

        mRecycleView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private final Response.Listener<String> onPostsLoadedPopular = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            ResultMoviesList = new ArrayList<>();

            ResultList = mGson.fromJson(response, JSONResultList.class);

            ResultMoviesList = ResultList.getResults();

            if (ResultMoviesList == null || ResultMoviesList.size() == 0) {

                mSearchTitle.setText(getString(com.jcMobile.android.popularmovies.R.string.searchNoResult, mQueryMovieTitle));
            } else {

                mSearchTitle.setText(getString(com.jcMobile.android.popularmovies.R.string.searchResult_title, mQueryMovieTitle));

                mMovieAdapter = new MovieAdapter(MainActivity.getmContext(), ResultMoviesList, SearchResultFragment.this);

                mRecycleView.setNestedScrollingEnabled(false);

                mRecycleView.setAdapter(mMovieAdapter);
            }

        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mSearchTitle.setText(getString(com.jcMobile.android.popularmovies.R.string.searchNoResult, mQueryMovieTitle));
        }
    };

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

        textTitle = ResultMoviesList.get(clickedItemIndex).getTitle();

        textReleaseDate = ResultMoviesList.get(clickedItemIndex).getReleaseDate();

        textOverview = ResultMoviesList.get(clickedItemIndex).getOverview();

        textVoteAverage = ResultMoviesList.get(clickedItemIndex).getVoteAverage();

        urlThumbnail = ResultMoviesList.get(clickedItemIndex).getPosterPath();

        numberMovieIDInTMDB = ResultMoviesList.get(clickedItemIndex).getId();

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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private  boolean internet_connection() {

        ConnectivityManager cm =
                (ConnectivityManager) MainActivity.getmContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
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
