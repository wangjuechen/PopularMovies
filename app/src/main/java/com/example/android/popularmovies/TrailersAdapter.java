package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JC on 25/03/17.
 */

class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private List<FeedMovieVideo> feedMovieVideoList = new ArrayList<>();
    private final Context mContext;
    private List<String> mTrailersYoutubeUrl = new ArrayList<>();
    private List<String> mTrailersName = new ArrayList<>();
    private List<String> mTrailerSize = new ArrayList<>();
    private List<String> mTrailerType = new ArrayList<>();


    TrailersAdapter(Context context, List<FeedMovieVideo> feedMovieVideoList) {
        this.mContext = context;
        this.feedMovieVideoList = feedMovieVideoList;

    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutIdForListItem = R.layout.recycleview_trail;

        boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        TrailersAdapterViewHolder viewHolder = new TrailersAdapterViewHolder(view);


        for (int i = 0; i < feedMovieVideoList.size(); i++) {

            mTrailersYoutubeUrl.add("https://www.youtube.com/watch?v="
                    + feedMovieVideoList.get(i).getmKeyForTrailerOnYoutube());

            mTrailersName.add(feedMovieVideoList.get(i).getmTrailerName());

            mTrailerSize.add(feedMovieVideoList.get(i).getmTrailerSize());

            mTrailerType.add(feedMovieVideoList.get(i).getmTrailerType());
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TrailersAdapterViewHolder holder, int position) {

        String trailerthumnail = "http://img.youtube.com/vi/" + feedMovieVideoList.get(position).getmKeyForTrailerOnYoutube() + "/0.jpg";

        Picasso.with(mContext)
                .load(trailerthumnail)
                .placeholder(R.drawable.placeholder)
                .into(holder.mtrailerThumnail);


        holder.mPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(mTrailersYoutubeUrl.get(holder.getAdapterPosition()));

                Intent trailersIntent = new Intent(Intent.ACTION_VIEW, uri);

                PackageManager packageManager = mContext.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(trailersIntent, 0);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {
                    mContext.startActivity(trailersIntent);}
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != feedMovieVideoList ? feedMovieVideoList.size() : 0);
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder {


        final ImageView mtrailerThumnail;
        final ImageView mPlayButton;

        public TrailersAdapterViewHolder(View view) {
            super(view);

            this.mtrailerThumnail = (ImageView) view.findViewById(R.id.trailer_thumnail);
            this.mPlayButton = (ImageView) view.findViewById(R.id.play_button);
        }

    }

    public void setMovieData(List<FeedMovieVideo> feedItems) {
        this.feedMovieVideoList = feedItems;
        notifyDataSetChanged();
    }

}
