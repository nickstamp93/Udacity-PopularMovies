package com.nickstamp.dev.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nickstamp.dev.popularmovies.utils.AppConstants;


/**
 * Created by Dev on 16/1/2017.
 */

public class PopularMoviesPreferences {

    public static final String TAG = PopularMoviesPreferences.class.getSimpleName();

    private static final String DEFAULT_MOVIES_SORT = AppConstants.SORT_POPULAR;


    /**
     * This method returns the preferred way to sort the grid of movie posters.
     *
     * @param context the calling context
     * @return \"most_popular\" or \"top_rated\" as a sorting method
     */
    public static String getPreferredMovieSorting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(AppConstants.MOVIE_SORT_ORDER, DEFAULT_MOVIES_SORT);

    }

    /**
     * This method sets the preferred way to sort the grid of movie posters.
     *
     * @param context            the calling context
     * @param preferredMovieSort the preferred sorting
     * @return \"most_popular\" or \"top_rated\"
     */
    public static void setPreferredMovieSort(Context context, String preferredMovieSort) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        prefs.edit().putString(AppConstants.MOVIE_SORT_ORDER, preferredMovieSort).apply();

    }


}
