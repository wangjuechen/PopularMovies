package com.example.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.data.PopularMovieContract;
import com.example.android.popularmovies.utilities.FavoriteMovieUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TabFragment extends Fragment implements MovieAdapter.ListItemClickListener {

    @BindView(R.id.rv_recycleview_PosterImage)
    RecyclerView mRecycleView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private String key = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    private GridLayoutManager mLayoutManager;

    private ArrayList<FeedItem> PopMoviesList = new ArrayList<>();

    private ArrayList<FeedItem> TopRatedMoviesList = new ArrayList<>();

    private ArrayList<FeedItem> FavoriteMoviesList = new ArrayList<>();

    private MovieAdapter mMovieAdapter;

    private final String KEY_INSTANCE_STATE_RV_POSITION = "recycleViewKey";

    private int mSectionNumber;

    private String MOVIE_POPULAR_URL =
            "http://api.themoviedb.org/3/movie/popular?api_key=" + key + "&language=en-US&page=";

    private String MOVIE_RATE_URL =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=" + key + "&language=en-US&page=";

    private static int mParsingJSONFlag;

    private Toast mToast;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public TabFragment() {

    }

    public static TabFragment newInstance(int sectionNumber) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
        //TODO: cant load popular movies list, as section number plus twice at first launch and movies List is messy!!!!!!!even worse after clicking, need to be fixed
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mainpage, container, false);

        ButterKnife.bind(this, view);

        loadDataToArrayList();

        mSectionNumber = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;

        SetMarginOfGridlayout setMarginOfGridlayout = new SetMarginOfGridlayout(0);

        mLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());

        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.addItemDecoration(setMarginOfGridlayout);

        mRecycleView.setHasFixedSize(true);

       switch (mSectionNumber) {
            case 1: {
                makeMovieSearchQuery(MOVIE_POPULAR_URL + String.valueOf(1));
                mMovieAdapter = new MovieAdapter(getActivity(), PopMoviesList, mSectionNumber, this);

                break;
            }
            case 2: {
                makeMovieSearchQuery(MOVIE_RATE_URL + String.valueOf(1));
                mMovieAdapter = new MovieAdapter(getActivity(), TopRatedMoviesList, mSectionNumber, this);

                break;
            }
            case 3: {
                //mMovieAdapter.setMovieData(FavoriteMoviesList);
                mMovieAdapter = new MovieAdapter(getActivity(), FavoriteMoviesList, mSectionNumber, this);

                break;
            }
        }
        mRecycleView.setAdapter(mMovieAdapter);


        EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener
                = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                makeMovieSearchQuery(mSectionNumber == 1 ? MOVIE_POPULAR_URL + String.valueOf(page) : MOVIE_RATE_URL + String.valueOf(page));

                final int curSize = mMovieAdapter.getItemCount();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        // Notify adapter with appropriate notify methods
                        mMovieAdapter.notifyItemRangeInserted(curSize, mSectionNumber == 1 ? PopMoviesList.size() - 1 : TopRatedMoviesList.size() - 1);
                    }
                });

            }
        };
        mRecycleView.addOnScrollListener(endlessRecyclerViewScrollListener);

        return view;
    }

    @Override
    public void onClick(int clickedItemIndex) {

        String textTitle;

        String textReleaseDate;

        String textOverview;

        String textVoteAverage;

        String urlThumbnail;

        int numberMovieIDInTMDB;

        Class destinationActivity = ChildActivity.class;

        Intent startChildActivityIntent = new Intent(getContext(), destinationActivity);

        if (mSectionNumber == 1) {

            textTitle = PopMoviesList.get(clickedItemIndex).getTitle();

            textReleaseDate = PopMoviesList.get(clickedItemIndex).getRelease_date();

            textOverview = PopMoviesList.get(clickedItemIndex).getOverview();

            textVoteAverage = PopMoviesList.get(clickedItemIndex).getVote_count();

            urlThumbnail = PopMoviesList.get(clickedItemIndex).getPoster_path();

            numberMovieIDInTMDB = PopMoviesList.get(clickedItemIndex).getId();

        } else if (mSectionNumber == 2) {

            textTitle = TopRatedMoviesList.get(clickedItemIndex).getTitle();

            textReleaseDate = TopRatedMoviesList.get(clickedItemIndex).getRelease_date();

            textOverview = TopRatedMoviesList.get(clickedItemIndex).getOverview();

            textVoteAverage = TopRatedMoviesList.get(clickedItemIndex).getVote_count();

            urlThumbnail = TopRatedMoviesList.get(clickedItemIndex).getPoster_path();

            numberMovieIDInTMDB = TopRatedMoviesList.get(clickedItemIndex).getId();
        } else {

            textTitle = FavoriteMoviesList.get(clickedItemIndex).getTitle();

            textReleaseDate = FavoriteMoviesList.get(clickedItemIndex).getRelease_date();

            textOverview = FavoriteMoviesList.get(clickedItemIndex).getOverview();

            textVoteAverage = FavoriteMoviesList.get(clickedItemIndex).getVote_count();

            urlThumbnail = FavoriteMoviesList.get(clickedItemIndex).getPoster_path();

            numberMovieIDInTMDB = FavoriteMoviesList.get(clickedItemIndex).getId();

        }

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
            switch (mSectionNumber) {
                case 1:
                    parsePopularResult(movieResponse);
                    mMovieAdapter.setMovieData(PopMoviesList);
                    break;
                case 2:
                    parseTopRatedResult(movieResponse);
                    mMovieAdapter.setMovieData(TopRatedMoviesList);
                    break;
                case 3:
                    parseFavoriteResult(movieResponse);
                    mMovieAdapter.setMovieData(FavoriteMoviesList);
                    break;
            }


        }
    }

    private void loadDataToArrayList() {

        if (internet_connection()) {

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
        } else {
            mToast = Toast.makeText(getActivity(), "This is no Internet", Toast.LENGTH_LONG);
            mToast.show();
        }


    }

    private void makeMovieSearchQuery(String Url) {

        if (Url.contains("popular")) {
            mParsingJSONFlag = 1;
        } else if (Url.contains("top_rated")) {
            mParsingJSONFlag = 2;
        } else {
            mParsingJSONFlag = 3;
        }

        URL MovieSearchUrl = NetworkUtils.buildUrl(Url);

        new FetchMovieTask().execute(MovieSearchUrl);

    }


    private void parsePopularResult(String movieJsonStr) {

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

            JSONArray posts = null;

            posts = response.optJSONArray(OWM_LIST);

            PopMoviesList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {

                JSONObject post = posts.optJSONObject(i);

                FeedItem item = new FeedItem(post.optString(OWM_TITLE), post.optString(OWM_POSTERADDRESS), post.optString(OWM_OVERVIEW)
                        , post.optString(OWM_RELEASEDATE).substring(0, 4), post.optString(OWM_VOTEAVERAGE),
                        post.optString(OWM_POPULARITY), post.optInt(OWM_MovieIDINTMDB));


                PopMoviesList.add(item);

            }

        } catch (Exception e) {
            mToast = Toast.makeText(getActivity(), "This is error in parse popularMovies Json", Toast.LENGTH_LONG);
            Log.v("Error:", "" + e.getMessage());
            mToast.show();
        }
    }

    private void parseTopRatedResult(String movieJsonStr) {

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

            JSONArray posts = null;

            posts = response.optJSONArray(OWM_LIST);

            TopRatedMoviesList = new ArrayList<>();


                for (int i = 0; i < posts.length(); i++) {

                    JSONObject post = posts.optJSONObject(i);

                    FeedItem item = new FeedItem(post.optString(OWM_TITLE), post.optString(OWM_POSTERADDRESS), post.optString(OWM_OVERVIEW)
                            , post.optString(OWM_RELEASEDATE).substring(0, 4), post.optString(OWM_VOTEAVERAGE),
                            post.optString(OWM_POPULARITY), post.optInt(OWM_MovieIDINTMDB));

                    TopRatedMoviesList.add(item);

                }

        } catch (Exception e) {
            mToast = Toast.makeText(getActivity(), "This is error in parse topRated movies Json", Toast.LENGTH_LONG);
            Log.v("Error:", "" + e.getMessage());
            mToast.show();
        }
    }

    private void parseFavoriteResult(String movieJsonStr) {

        final String OWM_TITLE = "original_title";

        final String OWM_OVERVIEW = "overview";

        final String OWM_RELEASEDATE = "release_date";

        final String OWM_VOTEAVERAGE = "vote_average";

        final String OWM_POSTERADDRESS = "poster_path";

        final String OWM_POPULARITY = "popularity";

        final String OWM_MovieIDINTMDB = "id";

        try {

            JSONObject response = new JSONObject(movieJsonStr);

            FavoriteMoviesList = new ArrayList<>();

            FeedItem item = new FeedItem(response.optString(OWM_TITLE), response.optString(OWM_POSTERADDRESS),
                    response.optString(OWM_OVERVIEW), response.optString(OWM_RELEASEDATE).substring(0, 4)
                    , response.optString(OWM_VOTEAVERAGE), response.optString(OWM_POPULARITY), response.optInt(OWM_MovieIDINTMDB));

            FavoriteMoviesList.add(item);

        } catch (Exception e) {
            mToast = Toast.makeText(getActivity(), "This is error in parse favoriteMovies Json", Toast.LENGTH_LONG);
            Log.v("Error:", "" + e.toString());
            mToast.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private ArrayList<String> getAllFavoriteMovieURL() {
        ArrayList<String> movieUrlList = new ArrayList<>();
        Cursor cursor = getActivity().getContentResolver().query(PopularMovieContract.PopularMovieEntry.CONTENT_URI, new String[]{PopularMovieContract.PopularMovieEntry.COLUMN_MOVIE_URL}, null, null, null, null);
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


    private boolean internet_connection() {

        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
