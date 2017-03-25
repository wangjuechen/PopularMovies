package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by JC on 25/03/17.
 */

public class PopularMovieContract  {



    public static final class PopularMovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "FavorateMovie";

        public static final String _ID = "_id";

        public static final String COLUMN_MovieID = "MovieId";




    }

}
