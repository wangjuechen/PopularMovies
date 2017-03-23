package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by JC on 12/03/17.
 * This is the Child Activity class
 */

public class ChildActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_page);


         TextView mTitleText;
         TextView mReleaseDateText;
         TextView mOverviewText;
         TextView mVoteAverageText;
         ImageView mThumbnailImage;
         Context mContext;

        mTitleText = (TextView) findViewById(R.id.tv_title_child_activity);
        mReleaseDateText = (TextView) findViewById(R.id.tv_releaseDate_child_activity);
        mOverviewText = (TextView) findViewById(R.id.tv_overview);
        mVoteAverageText = (TextView) findViewById(R.id.tv_voteReverage_child_activity);
        mThumbnailImage = (ImageView) findViewById(R.id.iv_poster_child_astivity);
        mContext = ChildActivity.this;


        Intent intentThatStartedThisActivity = getIntent();

        mTitleText.setText(intentThatStartedThisActivity.getStringExtra("title"));

        mReleaseDateText.setText(intentThatStartedThisActivity.getStringExtra("releaseDate"));

        mOverviewText.setText(intentThatStartedThisActivity.getStringExtra("overview"));

        mVoteAverageText.setText(getString(R.string.voteAverage,intentThatStartedThisActivity.getStringExtra("voteAverage")));

        Picasso.with(mContext).
                load("http://image.tmdb.org/t/p/w185/"+intentThatStartedThisActivity.getStringExtra("Thumbnail"))
                .into(mThumbnailImage);


    }
}
