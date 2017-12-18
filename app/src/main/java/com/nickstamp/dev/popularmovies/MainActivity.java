package com.nickstamp.dev.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nickstamp.dev.popularmovies.adapter.MoviesAdapter;
import com.nickstamp.dev.popularmovies.data.MoviesContract;
import com.nickstamp.dev.popularmovies.data.PopularMoviesPreferences;
import com.nickstamp.dev.popularmovies.databinding.ActivityMainBinding;
import com.nickstamp.dev.popularmovies.sync.SyncUtils;
import com.nickstamp.dev.popularmovies.utils.AppConstants;
import com.nickstamp.dev.popularmovies.utils.Utils;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.MovieGridClickListener, OnTabSelectListener, OnTabReselectListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_GRID_STATE = "grid_state";

    private GridLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private Parcelable mGridState;

    private Uri mUri = MoviesContract.CacheMovieEntry.CONTENT_URI;

    private int mPosition = RecyclerView.NO_POSITION;

    private int mCurrentView = AppConstants.VIEW_FROM_CACHE;

    public static final int LOADER_MOVIES = 0;

    public static final String[] MOVIES_PROJECTION = {
            MoviesContract._ID,
            MoviesContract.COLUMN_POSTER,
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_POSTER = 1;

    ActivityMainBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //set the layout manager
        mLayoutManager = new GridLayoutManager(MainActivity.this, Utils.calculateNoOfColumns(MainActivity.this));
        mViewBinding.rvMovieGrid.setLayoutManager(mLayoutManager);
        mViewBinding.rvMovieGrid.setHasFixedSize(true);

        //init the movie grid adapter
        mAdapter = new MoviesAdapter(this, null, this);

        //and set it to our recycler view
        mViewBinding.rvMovieGrid.setAdapter(mAdapter);

        // set up the bottom bar
        mViewBinding.bottomBar.setDefaultTabPosition(Utils.getStartingTab(this));
        mViewBinding.bottomBar.setOnTabSelectListener(this, false);
        mViewBinding.bottomBar.setOnTabReselectListener(this);

        if (savedInstanceState != null) {
            mCurrentView = savedInstanceState.getInt(AppConstants.TAG_CURRENT_VIEW);
        }

        // init the movies loader
        getSupportLoaderManager().initLoader(LOADER_MOVIES, null, this);

        // show the loading indicator
        showLoading();

        // sync the movie grid information in the background
        SyncUtils.initialize(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(AppConstants.TAG_CURRENT_VIEW, mCurrentView);
        mGridState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(TAG_GRID_STATE, mGridState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if (state != null)
            mGridState = state.getParcelable(TAG_GRID_STATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGridState != null) {
            mLayoutManager.onRestoreInstanceState(mGridState);
        }
    }

    /**
     * Hide the loading view
     */
    public void hideLoading() {

        mViewBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

    }

    /**
     * Show the loading view
     */
    private void showLoading() {

        mViewBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);

    }

    /**
     * Show an error snackbar informing the user that something went wrong
     */
    public void showErrorSnackbar() {

        mViewBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

        Snackbar.make(mViewBinding.rvMovieGrid, getString(R.string.error_message), Snackbar.LENGTH_LONG).show();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,
                mUri,
                MOVIES_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // if the current tab of the bottom bar is "favorites" then
        // images should be loaded from device, not from the network
        boolean useLocalImages = (mCurrentView == AppConstants.VIEW_FROM_FAVORITES);
        mAdapter.swapCursor(data, useLocalImages);

//        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
//        mViewBinding.rvMovieGrid.smoothScrollToPosition(mPosition);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null, true);
    }

    @Override
    public void onGridItemClick(int movieId, ImageView img) {

        String transitionName = ViewCompat.getTransitionName(img);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, img, transitionName);
        //create a new intent and pass the movie id through it in the movie details activity
        Intent detailsIntent = new Intent(MainActivity.this, MovieDetailsActivity.class);

        Uri uri;

        if (mCurrentView == AppConstants.VIEW_FROM_CACHE) {

            Cursor c = getContentResolver().query(MoviesContract.FavoriteEntry.buildFavoritesUri(movieId), new String[]{MoviesContract._ID}, null, null, null);
            if (c != null && c.getCount() > 0) {
                //Movie called from the cache list, but is a favorite, so load it from the local storage for better performance
                uri = MoviesContract.FavoriteEntry.buildFavoritesUri(movieId);
            } else {
                //Else, called from cache list and it's not a favorite
                //so of no internet connection, do nothing
                if (!Utils.isOnline(this)) {
                    Utils.showInternetErrorDialog(this);
                    return;
                }
                uri = MoviesContract.CacheMovieEntry.buildMovieCacheUri(movieId);
            }
            detailsIntent.putExtra(AppConstants.TAG_FAVORITE, false);

        } else {
            uri = MoviesContract.FavoriteEntry.buildFavoritesUri(movieId);
            detailsIntent.putExtra(AppConstants.TAG_FAVORITE, true);
        }
        detailsIntent.setData(uri);

        detailsIntent.putExtra(AppConstants.TRANSITION_VIEW_NAME, transitionName);
        ActivityCompat.startActivity(this, detailsIntent, options.toBundle());
    }


    @Override
    public void onTabSelected(@IdRes int tabId) {

        if (tabId == R.id.tab_popular) {

            showLoading();
            PopularMoviesPreferences.setPreferredMovieSort(MainActivity.this, AppConstants.SORT_POPULAR);
            mUri = MoviesContract.CacheMovieEntry.CONTENT_URI;
            SyncUtils.restart(MainActivity.this);
            if (mCurrentView == AppConstants.VIEW_FROM_FAVORITES) {
                mCurrentView = AppConstants.VIEW_FROM_CACHE;
                getSupportLoaderManager().restartLoader(LOADER_MOVIES, null, this);
            }
        } else if (tabId == R.id.tab_top_rated) {

            showLoading();
            PopularMoviesPreferences.setPreferredMovieSort(MainActivity.this, AppConstants.SORT_TOP_RATED);
            mUri = MoviesContract.CacheMovieEntry.CONTENT_URI;
            SyncUtils.restart(MainActivity.this);
            if (mCurrentView == AppConstants.VIEW_FROM_FAVORITES) {
                mCurrentView = AppConstants.VIEW_FROM_CACHE;
                getSupportLoaderManager().restartLoader(LOADER_MOVIES, null, this);
            }
        } else if (tabId == R.id.tab_upcoming) {
            showLoading();
            PopularMoviesPreferences.setPreferredMovieSort(MainActivity.this, AppConstants.SORT_UPCOMING);
            mUri = MoviesContract.CacheMovieEntry.CONTENT_URI;
            SyncUtils.restart(MainActivity.this);
            if (mCurrentView == AppConstants.VIEW_FROM_FAVORITES) {
                mCurrentView = AppConstants.VIEW_FROM_CACHE;
                getSupportLoaderManager().restartLoader(LOADER_MOVIES, null, this);
            }
        } else if (tabId == R.id.tab_favorite) {
            mUri = MoviesContract.FavoriteEntry.CONTENT_URI;

            if (mCurrentView == AppConstants.VIEW_FROM_CACHE) {
                mCurrentView = AppConstants.VIEW_FROM_FAVORITES;
                getSupportLoaderManager().restartLoader(LOADER_MOVIES, null, this);
            }
        }
    }

    @Override
    public void onTabReSelected(@IdRes int tabId) {
        mViewBinding.rvMovieGrid.smoothScrollToPosition(0);
    }
}
