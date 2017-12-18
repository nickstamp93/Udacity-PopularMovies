package com.nickstamp.dev.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nickstamp.dev.popularmovies.data.MoviesContract.FavoriteEntry;
import com.nickstamp.dev.popularmovies.data.MoviesContract.CacheMovieEntry;
import com.nickstamp.dev.popularmovies.model.MovieTmdb;

/**
 * Created by Dev on 26/1/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "popularMovies.db";

    public static final int DATABASE_VERSION = 1;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_CACHE = "CREATE TABLE " + CacheMovieEntry.TABLE_NAME + " (" +
                CacheMovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_BACKDROP + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_PLOT + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_RATING + " REAL NOT NULL);";

        final String CREATE_FAVORITES = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_BACKDROP + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_PLOT + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_RATING + " REAL NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_CACHE);
        sqLiteDatabase.execSQL(CREATE_FAVORITES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //nothing for now
    }
}
