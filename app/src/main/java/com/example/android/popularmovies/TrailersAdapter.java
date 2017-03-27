package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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


        String numberOfTrailer = Integer.toString(position + 1);

        String Trailer = mContext.getResources().getString(R.string.trailer_number);

        holder.mTextView.setText(Trailer + "   " + numberOfTrailer);

        holder.mTextView.setText(mTrailersName.get(holder.getAdapterPosition()) +
        "\n" + mTrailerSize.get(holder.getAdapterPosition()) + mContext.getString(R.string.movie_size));


        holder.mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailersYoutubeUrl.get(holder.getAdapterPosition()))));

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != feedMovieVideoList ? feedMovieVideoList.size() : 0);
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView mTextView;
        final ImageButton mImageButton;

        public TrailersAdapterViewHolder(View view) {
            super(view);

            this.mTextView = (TextView) view.findViewById(R.id.tv_number_trailers);
            this.mImageButton = (ImageButton) view.findViewById(R.id.trailer_play_button);
        }

    }

    public void setWeatherData(List<FeedMovieVideo> feedItems) {
        this.feedMovieVideoList = feedItems;
        notifyDataSetChanged();
    }

}
