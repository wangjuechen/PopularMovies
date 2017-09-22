package com.example.android.popularmovies.utilities;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.data.PopularMovieContract;
import com.example.android.popularmovies.data.PopularMovieDbHelper;

public class FavoriteMovieUtils {

    private SQLiteDatabase db;

    private static final String mURLDomain = "https://api.themoviedb.org/3/movie/";

    private static final String mKey = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    public void favoriteCheck(Context context, int MovieIdInTMDB, boolean favoriteButtonChecked) {

        PopularMovieDbHelper mPopularMovieHelper = new PopularMovieDbHelper(context);

        db = mPopularMovieHelper.getWritableDatabase();

        if (favoriteButtonChecked) {

            addFavoriteToSQLite(context, MovieIdInTMDB);

        } else {

            deleteFavoriteFromSQLite(context, MovieIdInTMDB);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean hasObject(Context context, String searchItem) {

        String[] columns = {PopularMovieContract.PopularMovieEntry.COLUMN_MOVIE_ID};
        String selection = PopularMovieContract.PopularMovieEntry.COLUMN_MOVIE_ID + " =?";
        String[] selectionArgs = {searchItem};

        Cursor cursor = context.getContentResolver().query(PopularMovieContract.PopularMovieEntry.CONTENT_URI, columns, selection, selectionArgs, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private void addFavoriteToSQLite(Context context, int MovieIdInTMDB) {
        ContentValues FavoriteMovie = new ContentValues();

        FavoriteMovie.put(PopularMovieContract.PopularMovieEntry.COLUMN_MOVIE_ID, String.valueOf(MovieIdInTMDB));

        FavoriteMovie.put(PopularMovieContract.PopularMovieEntry.COLUMN_MOVIE_URL, mURLDomain + String.valueOf(MovieIdInTMDB)
                + "?api_key=" + mKey + "&language=en-US");

        context.getContentResolver().insert(PopularMovieContract.PopularMovieEntry.CONTENT_URI, FavoriteMovie);
    }

    private void deleteFavoriteFromSQLite(Context context, int MovieIdInTMDB) {
        String selection = PopularMovieContract.PopularMovieEntry.COLUMN_MOVIE_ID + " =?";
        String[] selectionArgs = {String.valueOf(MovieIdInTMDB)};
        context.getContentResolver().delete(PopularMovieContract.PopularMovieEntry.CONTENT_URI, selection, selectionArgs);
    }
}
