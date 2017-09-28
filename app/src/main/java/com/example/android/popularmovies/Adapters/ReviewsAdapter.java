package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.DataList.FeedUserReviews;
import com.example.android.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JC on 26/03/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private List<FeedUserReviews> feedUserReviewsesList = new ArrayList<>();
    private final Context mContext;


    public ReviewsAdapter(Context context, List<FeedUserReviews> feedUserReviewsesList) {
        this.mContext = context;
        this.feedUserReviewsesList = feedUserReviewsesList;

    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutIdForListItem = R.layout.recycleview_userreviews;

        boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        ReviewsAdapterViewHolder viewHolder = new ReviewsAdapterViewHolder(view);

        return viewHolder;

    }
    @Override
    public void onBindViewHolder(final ReviewsAdapterViewHolder holder, int position) {

        FeedUserReviews feedItem = feedUserReviewsesList.get(position);

        holder.mTextView.setText(mContext.getString(R.string.Author) + mContext.getString(R.string.space) + feedItem.getUserNames() +
                mContext.getString(R.string.newLine) + feedItem.getReviewsContent());
    }

    @Override
    public int getItemCount() {
        return (null != feedUserReviewsesList ? feedUserReviewsesList.size() : 0);
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView mTextView;

        public ReviewsAdapterViewHolder(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.tv_userReviews);
        }

    }

    public void setMovieData(List<FeedUserReviews> feedItems) {
        this.feedUserReviewsesList = feedItems;
        notifyDataSetChanged();
    }

}

