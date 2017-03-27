package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.data.PopularMovieContract.*;
/**
 * Created by JC on 25/03/17.
 */

public class PopularMovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorite.db";

    public static final int DATABASE_VERSION = 1;

    public PopularMovieDbHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION );
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + PopularMovieEntry.TABLE_NAME
                + "(" + PopularMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PopularMovieEntry.COLUMN_MovieID + " INTEGER NOT NULL" + ");";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PopularMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
