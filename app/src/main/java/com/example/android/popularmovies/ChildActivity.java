package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by JC on 12/03/17.
 * This is the Child Activity class
 */

public class ChildActivity extends AppCompatActivity {

    private ImageButton mTrailerButton1;
    private ImageButton mTrailerButton2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_page);

        mTrailerButton1 = (ImageButton) findViewById(R.id.trailer1_play_button);
        mTrailerButton2 = (ImageButton) findViewById(R.id.trailer2_play_button);

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

        mVoteAverageText.setText(getString(R.string.voteAverage, intentThatStartedThisActivity.getStringExtra("voteAverage")));

        Picasso.with(mContext).
                load("http://image.tmdb.org/t/p/w185/" + intentThatStartedThisActivity.getStringExtra("Thumbnail"))
                .into(mThumbnailImage);

        mTrailerButton1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            //TODO going to add trailer1 play action
                Log.v("Test 1", "trialer1");
            }
        });

        mTrailerButton2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //TODO going to add trailer2 play action
                Log.v("Test 2", "trialer2");
            }
        });
    }


}
