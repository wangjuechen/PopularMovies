package com.jcMobile.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by JC on 25/03/17.
 */

public class PopularMovieContract  {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.app";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class PopularMovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "FavoriteMovie";

        public static final String _ID = "_id";

        public static final String COLUMN_MOVIE_ID = "MovieId";

        public static final String COLUMN_MOVIE_URL = "MovieUrl";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildPopularMovieUri(long id){ return ContentUris.withAppendedId(CONTENT_URI, id);}

    }

}
