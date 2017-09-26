package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.android.popularmovies.Adapters.MovieAdapter;
import com.example.android.popularmovies.DataList.FeedItem;
import com.example.android.popularmovies.DataList.JSONResultList;
import com.example.android.popularmovies.utilities.SetMarginOfGridlayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    private String key = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    public static final String ARG_QUERY_PARAM = "search_movie_title";

    final private String QUERY_URL = "https://api.themoviedb.org/3/search/movie?api_key=" + key + "&language=en-US&query=";

    private String mFullQueryUrl;

    private GridLayoutManager mLayoutManager;

    private boolean fragmentLoaded;

    private Gson mGson;

    private RequestQueue mRequestQueue;

    private FeedItem feedItem;

    private Toast mToast;

    private JSONResultList ResultList;

    private List<FeedItem> ResultMoviesList = new ArrayList<>();

    private MovieAdapter mMovieAdapter;

    @BindView(R.id.rv_searchResult)
    RecyclerView mRecycleView;

    @BindView(R.id.tv_searchResultTitle)
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

            mFullQueryUrl = QUERY_URL + mQueryMovieTitle;

        }

        if (internet_connection()) {
            mRequestQueue = Volley.newRequestQueue(MainActivity.getmContext());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            mGson = gsonBuilder.create();

            StringRequest requestForPopularMovies = new StringRequest(Request.Method.GET, mFullQueryUrl, onPostsLoadedPopular, onPostsError);

            mRequestQueue.add(requestForPopularMovies);

        }

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        ButterKnife.bind(this, view);

        SetMarginOfGridlayout setMarginOfGridlayout = new SetMarginOfGridlayout(0);

        mLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());

        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.addItemDecoration(setMarginOfGridlayout);

        mRecycleView.setHasFixedSize(true);
        // Inflate the layout for this fragment

       /* if (ResultMoviesList != null && ResultMoviesList.size() > 0) {
            mSearchTitle.setText(getString(R.string.searchResult_title, mQueryMovieTitle));
        }*/
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

                mSearchTitle.setText(getString(R.string.searchNoResult, mQueryMovieTitle));
            } else {

                mSearchTitle.setText(getString(R.string.searchResult_title, mQueryMovieTitle));

                mMovieAdapter = new MovieAdapter(MainActivity.getmContext(), ResultMoviesList, SearchResultFragment.this);

                mRecycleView.setNestedScrollingEnabled(false);

                mRecycleView.setAdapter(mMovieAdapter);
            }

        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mSearchTitle.setText(getString(R.string.searchNoResult, mQueryMovieTitle));
        }
    };

    @Override
    public void onClick(int clickedItemIndex) {

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

    private static boolean internet_connection() {

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
}
