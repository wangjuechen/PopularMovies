package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by JC on 26/03/17.
 */

public class PopularMovieProvider extends ContentProvider {
    private SQLiteDatabase db;
    private PopularMovieDbHelper mPopularMovieDbHelper;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 200;

    @Override
    public boolean onCreate() {
        mPopularMovieDbHelper = new PopularMovieDbHelper(getContext());
        db = mPopularMovieDbHelper.getWritableDatabase();

        if (db == null) {
            return false;
        }

        if (db.isReadOnly()) {
            db.close();
            db = null;
            return false;
        }

        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            // All Flavors selected
            case MOVIE: {
                retCursor = mPopularMovieDbHelper.getReadableDatabase().query(
                        PopularMovieContract.PopularMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual flavor based on Id selected
            case MOVIE_WITH_ID: {
                retCursor = mPopularMovieDbHelper.getReadableDatabase().query(
                        PopularMovieContract.PopularMovieEntry.TABLE_NAME,
                        projection,
                        PopularMovieContract.PopularMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIE: {
                return PopularMovieContract.PopularMovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_WITH_ID: {
                return PopularMovieContract.PopularMovieEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(PopularMovieContract.PopularMovieEntry.TABLE_NAME, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = PopularMovieContract.PopularMovieEntry.buildPopularMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case MOVIE:
                numDeleted = db.delete(
                        PopularMovieContract.PopularMovieEntry.TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        PopularMovieContract.PopularMovieEntry.TABLE_NAME + "'");
                break;
            case MOVIE_WITH_ID:
                numDeleted = db.delete(PopularMovieContract.PopularMovieEntry.TABLE_NAME,
                        PopularMovieContract.PopularMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        PopularMovieContract.PopularMovieEntry.TABLE_NAME + "'");

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numUpdated = 0;

        if (values == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(uriMatcher.match(uri)){
            case MOVIE:{
                numUpdated = db.update(PopularMovieContract.PopularMovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                numUpdated = db.update(PopularMovieContract.PopularMovieEntry.TABLE_NAME,
                        values,
                        PopularMovieContract.PopularMovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

    private static UriMatcher buildUriMatcher() {
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopularMovieContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, PopularMovieContract.PopularMovieEntry.TABLE_NAME, MOVIE);
        matcher.addURI(authority, PopularMovieContract.PopularMovieEntry.TABLE_NAME + "/#", MOVIE_WITH_ID);

        return matcher;
    }
}
