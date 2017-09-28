package com.example.android.popularmovies.Adapters;

/**
 * Created by JC on 2017/3/9.
 * This is the adapter for RecyclerView
 */

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.DataList.FeedItem;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.utilities.FavoriteMovieUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private List<FeedItem> feedItemList = new ArrayList<>();
    private final Context mContext;
    private final ListItemClickListener mOnClickListener;
    private final FavoriteMovieUtils mFavoriteMovieUtils = new FavoriteMovieUtils();
    private Cursor mCursor;


    public MovieAdapter(Context context, List<FeedItem> feedItemList, ListItemClickListener listener) {
        this.mContext = context;
        this.feedItemList = feedItemList;
        mOnClickListener = listener;

    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutIdForListItem = R.layout.recycleview_item;

        boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();

        if (parent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && !parent.getResources().getBoolean(R.bool.isTablet)) {
            lp.height = parent.getMeasuredHeight() / 2;
        }

        if (parent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !parent.getResources().getBoolean(R.bool.isTablet)) {
            lp.height = parent.getMeasuredHeight();
        }


        view.setLayoutParams(lp);

        MovieAdapterViewHolder viewHolder = new MovieAdapterViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder movieAdapterViewHolder, final int position) {

        final FeedItem feedItem = feedItemList.get(position);

        final String imageUrl = "http://image.tmdb.org/t/p/w500/" +
                feedItem.getPosterPath();

        movieAdapterViewHolder.mMovieNameTextView.setText(feedItem.getTitle());


        if (mFavoriteMovieUtils.hasObject(this.mContext, String.valueOf(feedItem.getId()))) {

            movieAdapterViewHolder.mFavoriteCheckButton.setChecked(true);

        }


        movieAdapterViewHolder.mFavoriteCheckButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = movieAdapterViewHolder.mFavoriteCheckButton.isChecked();

                mFavoriteMovieUtils.favoriteCheck(mContext, feedItemList.get(position).getId(), checked);
            }
        });

        if (!TextUtils.isEmpty(feedItem.getPosterPath())) {

            Picasso.with(mContext)
                    .load(imageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(movieAdapterViewHolder.mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.v("Picasso", "fetch image success in first time.");
                        }

                        @Override
                        public void onError() {
                            //Try again online if cache failed
                            Log.v("Picasso", "Could not fetch image in first time...");
                            Picasso.with(mContext).load(imageUrl).networkPolicy(NetworkPolicy.NO_CACHE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).error(R.drawable.error)
                                    .into(movieAdapterViewHolder.mImageView, new Callback() {

                                        @Override
                                        public void onSuccess() {
                                            Log.v("Picasso", "fetch image success in try again.");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.v("Picasso", "Could not fetch image again...");
                                        }

                                    });
                        }
                    });

        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        final ImageView mImageView;
        final TextView mMovieNameTextView;
        final CheckBox mFavoriteCheckButton;

        public MovieAdapterViewHolder(View view) {
            super(view);

            this.mImageView = (ImageView) view.findViewById(R.id.iv_Poster);
            this.mMovieNameTextView = (TextView) view.findViewById(R.id.tv_movieNameInMainPage);
            this.mFavoriteCheckButton = (CheckBox) view.findViewById(R.id.favorite_checkBoxInMainPage);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    mOnClickListener.onClick(clickedPosition);
                }
            });
        }
    }

    public interface ListItemClickListener {
        void onClick(int clickedItemIndex);
    }

    /*public void setMovieData(List<FeedItem> feedItems) {
        this.feedItemList = feedItems;
        notifyDataSetChanged();
    }*/
}


