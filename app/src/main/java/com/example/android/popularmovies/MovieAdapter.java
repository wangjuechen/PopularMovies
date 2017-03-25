package com.example.android.popularmovies;

/**
 * Created by JC on 2017/3/9.
 * This is the adapter for RecyclerView
 */

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private List<FeedItem> feedItemList;
    private final Context mContext;
    private final ListItemClickListener mOnClickListener;


    MovieAdapter(Context context, List<FeedItem> feedItemList, ListItemClickListener listener) {
        this.mContext = context;
        this.feedItemList = feedItemList;
        mOnClickListener = listener;

    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutIdForListItem = R.layout.recycleview_item;

        boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        MovieAdapterViewHolder viewHolder = new MovieAdapterViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, final int position) {

        FeedItem feedItem = feedItemList.get(position);
        int imageHeight = 800;
        int imageWidth = 550;

        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" +
                    feedItem.getThumbnail()).placeholder(R.drawable.placeholder).error(R.drawable.error).
                    into(movieAdapterViewHolder.mImageView);

        }
    }


    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        final ImageView mImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);

            this.mImageView = (ImageView) view.findViewById(R.id.iv_Poster);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onClick(clickedPosition);
        }

    }

    public interface ListItemClickListener {
        void onClick(int clickedItemIndex);
    }

    public void setWeatherData(List<FeedItem> feedItems) {
        this.feedItemList = feedItems;
        notifyDataSetChanged();
    }
}


