package com.nickstamp.dev.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;

import com.nickstamp.dev.popularmovies.R;
import com.nickstamp.dev.popularmovies.data.MoviesContract;
import com.nickstamp.dev.popularmovies.data.PopularMoviesPreferences;
import com.nickstamp.dev.popularmovies.model.MovieTmdb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Dev on 16/1/2017.
 */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    /**
     * This method checks the screen width of the running device in pixels and calculates the ideal number of columns for the grid.
     *
     * @param context the context of the caller
     * @return the number of grid columns for the current screen
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    /**
     * This method checks wether there is an active internet connection on the devive.
     *
     * @param context the calling context
     * @return True if there is an active internet connection, False otherwise
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * This method converts an API-formatted date String into a UI friendly format
     *
     * @param date the date to be formatted
     * @return the UI friend formatted date
     */
    public static String formatDateForUI(String date) {

        final String API_DATE_FORMAT = "yyyy-MM-dd";
        final String UI_DATE_FORMAT = "dd MMM yyyy";

        try {
            return new SimpleDateFormat(UI_DATE_FORMAT).format(new SimpleDateFormat(API_DATE_FORMAT).parse(date)).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    /**
     * Display a dialog informing the user about the absence of a connection
     * with the option to launch the settings to enable it
     *
     * @param context the context
     */
    public static void showInternetErrorDialog(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(context.getString(R.string.dialog_title_network_error));

        // Setting Dialog Message
        alertDialog.setMessage(R.string.dialog_message_network_error);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_alert);

        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.dialog_button_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public static String saveToInternalStorage(Context context, Bitmap bitmapImage, String filename) {
        ContextWrapper cw = new ContextWrapper(context);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("img", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory, filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    public static ContentValues[] getContentValuesFromResponse(ArrayList<MovieTmdb> response) {
        ContentValues[] contentValues = new ContentValues[response.size()];

        for (int i = 0; i < contentValues.length; i++) {
            ContentValues cv = new ContentValues();

            MovieTmdb movie = response.get(i);

            cv.put(MoviesContract._ID, movie.getId());
            cv.put(MoviesContract.COLUMN_TITLE, movie.getTitle());
            cv.put(MoviesContract.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            cv.put(MoviesContract.COLUMN_POSTER, movie.getPosterPath());
            cv.put(MoviesContract.COLUMN_BACKDROP, movie.getBackdropPath());
            cv.put(MoviesContract.COLUMN_PLOT, movie.getPlotSynopsis());
            cv.put(MoviesContract.COLUMN_RELEASE_DATE, Utils.formatDateForUI(movie.getReleaseDate()));
            cv.put(MoviesContract.COLUMN_RATING, movie.getRating());

            contentValues[i] = cv;
        }

        return contentValues;
    }

    public static int getStartingTab(Context context) {
        String sort = PopularMoviesPreferences.getPreferredMovieSorting(context);
        if (sort.equals(AppConstants.SORT_UPCOMING)) {
            return 0;
        } else if (sort.equals(AppConstants.SORT_POPULAR)) {
            return 1;
        } else if (sort.equals(AppConstants.SORT_TOP_RATED)) {
            return 2;
        }
        return 0;
    }

    /**
     * This method constructs a youtube link from an existing key
     * @param key the key of the requested youtube video
     * @return the complete Uri of the video as a string
     */
    public static String buildYoutubeUri(String key) {
        return "http://www.youtube.com/watch?v=" + key;
    }
}
