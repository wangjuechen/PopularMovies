package com.example.android.popularmovies;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultActivity extends AppCompatActivity implements SearchResultFragment.OnFragmentInteractionListener{

    @BindView(R.id.tv_searchResultTitle)
    TextView mSearchTitle;

    private String query;

    private SearchResultFragment ResultFragment = new SearchResultFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        handleIntent(getIntent());

        Bundle bundle = new Bundle();

        bundle.putString(SearchResultFragment.ARG_QUERY_PARAM, query);

        ResultFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.searchResult_fragment_container, ResultFragment).commit();
    }

    private void handleIntent(Intent intent){

        if(Intent.ACTION_SEARCH.equals(intent.getAction())){

            query = intent.getStringExtra(SearchManager.QUERY);

            mSearchTitle.setText(getString(R.string.searchResult_title, query));

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
            mSearchTitle.setText(getString(R.string.searchNoResult, query));
    }
}
