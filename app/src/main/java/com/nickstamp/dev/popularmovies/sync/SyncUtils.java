package com.nickstamp.dev.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.nickstamp.dev.popularmovies.BuildConfig;
import com.nickstamp.dev.popularmovies.MainActivity;
import com.nickstamp.dev.popularmovies.data.MoviesContract;
import com.nickstamp.dev.popularmovies.data.PopularMoviesPreferences;
import com.nickstamp.dev.popularmovies.model.MovieResults;
import com.nickstamp.dev.popularmovies.utils.AppConstants;
import com.nickstamp.dev.popularmovies.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dev on 26/1/2017.
 */

public class SyncUtils {

    public static final String TAG = SyncUtils.class.getSimpleName();

    private static boolean sInitialized = false;

    public static synchronized void initialize(final Context context) {

        if (sInitialized){

            ((MainActivity) context).hideLoading();
            return;
        }

        sInitialized = true;

        //if there is an internet connection, make an API request
        if (Utils.isOnline(context)) {

            ApiService client = RetroClient.getApiService();

            Call<MovieResults> call = client.getMovieList(PopularMoviesPreferences.getPreferredMovieSorting(context), BuildConfig.MOVIE_DB_API_KEY);

            call.enqueue(new Callback<MovieResults>() {
                @Override
                public void onResponse(Call<MovieResults> call, final Response<MovieResults> response) {


                    context.getContentResolver().delete(
                            MoviesContract.CacheMovieEntry.CONTENT_URI,
                            null,
                            null);

                    ContentValues[] contentValues;
                    contentValues = Utils.getContentValuesFromResponse(response.body().getMovies());

                    context.getContentResolver().bulkInsert(MoviesContract.CacheMovieEntry.CONTENT_URI, contentValues);


                    ((MainActivity) context).hideLoading();

                }

                @Override
                public void onFailure(Call<MovieResults> call, Throwable t) {

                    ((MainActivity) context).showErrorSnackbar();
                    ((MainActivity) context).hideLoading();

                }
            });

        } else {
            //else show an internet error dialog
            Utils.showInternetErrorDialog(context);
            ((MainActivity) context).hideLoading();
        }

    }

    public static void restart(Context context) {
        sInitialized = false;
        initialize(context);
    }
}
