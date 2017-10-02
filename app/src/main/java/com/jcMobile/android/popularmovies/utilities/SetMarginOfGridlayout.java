package com.jcMobile.android.popularmovies.utilities;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by JC on 11/03/17.
 * This class is to set margin of gridlayout of RecycleView
 */

 public class SetMarginOfGridlayout extends RecyclerView.ItemDecoration{

    private final int space;

    public SetMarginOfGridlayout(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
    }
}