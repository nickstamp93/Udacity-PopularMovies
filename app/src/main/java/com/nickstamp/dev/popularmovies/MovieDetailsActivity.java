package com.nickstamp.dev.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nickstamp.dev.popularmovies.adapter.ReviewsAdapter;
import com.nickstamp.dev.popularmovies.adapter.VideosAdapter;
import com.nickstamp.dev.popularmovies.data.MoviesContract;
import com.nickstamp.dev.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.nickstamp.dev.popularmovies.model.MovieTmdb;
import com.nickstamp.dev.popularmovies.model.ReviewResults;
import com.nickstamp.dev.popularmovies.model.TrailerResults;
import com.nickstamp.dev.popularmovies.sync.ApiService;
import com.nickstamp.dev.popularmovies.sync.RetroClient;
import com.nickstamp.dev.popularmovies.utils.AppConstants;
import com.nickstamp.dev.popularmovies.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nickstamp.dev.popularmovies.utils.AppConstants.POPULAR_MOVIES_HASHTAG;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MovieDetailsActivity.class.getSimpleName();
    public static final String TAG_VIDEOS = "videos";
    public static final String TAG_REVIEWS = "mReviews";

    private static final int LOADER_MOVIE = 0;

    public static final String[] MOVIE_PROJECTION = {
            MoviesContract._ID,
            MoviesContract.COLUMN_TITLE,
            MoviesContract.COLUMN_ORIGINAL_TITLE,
            MoviesContract.COLUMN_POSTER,
            MoviesContract.COLUMN_BACKDROP,
            MoviesContract.COLUMN_PLOT,
            MoviesContract.COLUMN_RELEASE_DATE,
            MoviesContract.COLUMN_RATING,
    };


    MovieTmdb mMovie;

    Uri mUri = null;

    ActivityMovieDetailsBinding mBinding;

    VideosAdapter mVideosAdapter;
    ReviewsAdapter mReviewAdapter;
    Toast mToast;

    private boolean mInFavoriteDb;
    private boolean mCalledFromCache;

    Bitmap mBitmapPoster = null;
    Bitmap mBitmapBackdrop = null;

    ArrayList<TrailerResults.Trailer> mVideos;
    ArrayList<ReviewResults.Review> mReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        ViewCompat.setTransitionName(mBinding.ivMoviePoster,
                getIntent().getStringExtra(AppConstants.TRANSITION_VIEW_NAME));

        mVideosAdapter = new VideosAdapter(this, null);
        mBinding.rvVideos.setLayoutManager(new GridLayoutManager(MovieDetailsActivity.this, 2));
        mBinding.rvVideos.setAdapter(mVideosAdapter);

        mReviewAdapter = new ReviewsAdapter(this, null);
        mBinding.rvReviews.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mBinding.rvReviews.setAdapter(mReviewAdapter);

        // load movie data
        loadMovieData();

        if (savedInstanceState != null) {
            mVideos = savedInstanceState.getParcelableArrayList(TAG_VIDEOS);
            if (mVideos != null && mVideos.size() > 0) {
                mVideosAdapter.setData(mVideos);
            } else {
                showVideoError();
            }
            mReviews = savedInstanceState.getParcelableArrayList(TAG_REVIEWS);
            if (mReviews != null && mReviews.size() > 0) {
                mReviewAdapter.setData(mReviews);
            } else {
                showReviewError();
            }
        } else {
            showVideosLoading();
            showReviewsLoading();

            loadTrailers();
            loadReviews();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TAG_VIDEOS, mVideos);
        outState.putParcelableArrayList(TAG_REVIEWS, mReviews);

        super.onSaveInstanceState(outState);
    }

    private void loadTrailers() {
        ApiService client = RetroClient.getApiService();

        int id = Integer.valueOf(mUri.getPathSegments().get(1));

        Call<TrailerResults> call = client.getMovieVideos(id, BuildConfig.MOVIE_DB_API_KEY);

        call.enqueue(new Callback<TrailerResults>() {
            @Override
            public void onResponse(Call<TrailerResults> call, final Response<TrailerResults> response) {

                mVideos = response.body().getTrailers();

                if (mVideos != null && mVideos.size() > 0) {
                    mVideosAdapter.setData(mVideos);
                } else {
                    showVideoError();
                }

                hideVideosLoading();

            }

            @Override
            public void onFailure(Call<TrailerResults> call, Throwable t) {

                showVideoError();

                hideVideosLoading();

            }
        });
    }

    private void loadReviews() {

        ApiService client = RetroClient.getApiService();

        int id = Integer.valueOf(mUri.getPathSegments().get(1));

        Call<ReviewResults> call = client.getMovieReviews(id, BuildConfig.MOVIE_DB_API_KEY);

        call.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Call<ReviewResults> call, final Response<ReviewResults> response) {

                mReviews = response.body().getReviews();

                if (mReviews != null && mReviews.size() > 0) {
                    mReviewAdapter.setData(mReviews);
                } else {
                    showReviewError();
                }

                hideReviewsLoading();
            }

            @Override
            public void onFailure(Call<ReviewResults> call, Throwable t) {

                showReviewError();

                hideReviewsLoading();

            }
        });
    }

    /**
     * Loads the data of the requested movie, based on the Uri that was passed through the intent
     * into this activity. This method always tries to load data from local database instead of network
     * for better efficiency and network data saving.
     */
    private void loadMovieData() {

        // Postpone the transition until the detail image thumbnail is loaded
        ActivityCompat.postponeEnterTransition(this);

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        boolean calledFromFavoriteList = false;
        if (getIntent().hasExtra(AppConstants.TAG_FAVORITE)) {
            calledFromFavoriteList = getIntent().getBooleanExtra(AppConstants.TAG_FAVORITE, false);
        }

        mCalledFromCache = !calledFromFavoriteList;

        //If the movie details, was started by the cached list, must check if this movie is in favorite list
        //so that if it's in the favorites, we should load ir from there and not the network
        if (!calledFromFavoriteList) {
            int id = Integer.valueOf(mUri.getPathSegments().get(1));
            Uri correspondingFavoritesUri = MoviesContract.FavoriteEntry.buildFavoritesUri(id);

            Cursor c = getContentResolver().query(correspondingFavoritesUri, null, null, null, null);
            if (c != null && c.moveToFirst() && c.getCount() > 0) {
                mInFavoriteDb = true;
                mUri = correspondingFavoritesUri;
            }
            c.close();
        } else {
            mInFavoriteDb = true;
        }

        toggleFavorite();

        //if called from cache list and is not in favorites, load it from cache
        if (mCalledFromCache && !mInFavoriteDb) {
            mUri = MoviesContract.CacheMovieEntry.buildMovieCacheUri(Integer.valueOf(mUri.getPathSegments().get(1)));
        }

        getSupportLoaderManager().restartLoader(LOADER_MOVIE, null, this);

    }

    /**
     * Toggle favorite floating action button view
     */
    private void toggleFavorite() {
        if (mInFavoriteDb) {
            mBinding.floatingActionButton.setImageResource(R.drawable.ic_heart_filled);
        } else {
            mBinding.floatingActionButton.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    /**
     * Show a message when no videos found for the current movie
     */
    private void showVideoError() {
        if (Utils.isOnline(this)) {
            mBinding.tvVideoError.setText(getString(R.string.text_error_videos));
        } else {
            mBinding.tvVideoError.setText(getString(R.string.text_error_videos_offline));
        }
        mBinding.rvVideos.setVisibility(View.GONE);
        mBinding.tvVideoError.setVisibility(View.VISIBLE);
        mBinding.pbVideos.setVisibility(View.GONE);
    }

    /**
     * Show videos loading indicator
     */
    private void showVideosLoading() {
        mBinding.pbVideos.setVisibility(View.VISIBLE);
    }

    /**
     * Hide videos loading indicator
     */
    private void hideVideosLoading() {
        mBinding.pbVideos.setVisibility(View.GONE);
    }

    /**
     * Show mReviews loading indicator
     */
    private void showReviewsLoading() {
        mBinding.pbReviews.setVisibility(View.VISIBLE);
    }

    /**
     * Hide mReviews loading indicator
     */
    private void hideReviewsLoading() {
        mBinding.pbReviews.setVisibility(View.GONE);
    }

    /**
     * Show a message when no mReviews found for the current movie
     */
    private void showReviewError() {
        if (Utils.isOnline(this)) {
            mBinding.tvReviewError.setText(getString(R.string.text_error_reviews));
        } else {
            mBinding.tvReviewError.setText(getString(R.string.text_error_reviews_offline));
        }
        mBinding.rvReviews.setVisibility(View.GONE);
        mBinding.tvReviewError.setVisibility(View.VISIBLE);
    }

    private Intent createShareMovieIntent() {
        String shareText = mMovie.getTitle() + " " + POPULAR_MOVIES_HASHTAG;
        if (mVideos.size() > 0)
            shareText = POPULAR_MOVIES_HASHTAG + "\n" + Utils.buildYoutubeUri(mVideos.get(0).getKey());

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(shareText)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_menu_share:
                Intent sendIntent = createShareMovieIntent();
                startActivity(sendIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Listener for add to favorites button
     *
     * @param view the calling UI element
     */
    public void onFavorite(View view) {

        new OnFavoriteTask().execute(mInFavoriteDb);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case LOADER_MOVIE:

                return new CursorLoader(this,
                        mUri,
                        MOVIE_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() == 0) {
            return;
        }

        data.moveToFirst();

        mMovie = new MovieTmdb(data);

        mBinding.setCurrentMovie(mMovie);

        //load image explicitly because of postponeEnterTransition

        if (mInFavoriteDb) {
            MovieTmdb.loadPoster(this, mBinding.ivMoviePoster, mMovie.getPosterPath());
            MovieTmdb.loadBackdrop(mBinding.ivMovieBackdrop, mMovie.getBackdropPath());
        } else {
            MovieTmdb.loadPoster(this, mBinding.ivMoviePoster, mMovie.getPosterUrl());
            MovieTmdb.loadBackdrop(mBinding.ivMovieBackdrop, mMovie.getBackdropUrl());
        }

        if (!mMovie.getTitle().equals(mMovie.getOriginalTitle())) {
            String text = "( " + mMovie.getOriginalTitle() + " )";
            mBinding.tvMovieOriginalTitle.setText(text);
        } else {
            mBinding.tvMovieOriginalTitle.setVisibility(View.GONE);
        }

        downloadImagesForLocalSave();

    }

    /**
     * This method loads the current movie's poster and backdrop images into bitmap files, so that
     * if the user adds this movie into the favorites list, this images will be saved into
     * the local file system
     */
    private void downloadImagesForLocalSave() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mBitmapPoster = Glide.with(MovieDetailsActivity.this).load(mMovie.getPosterUrl()).asBitmap().into(-1, -1).get();
                    mBitmapBackdrop = Glide.with(MovieDetailsActivity.this).load(mMovie.getBackdropUrl()).asBitmap().into(-1, -1).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    class OnFavoriteTask extends AsyncTask<Boolean, Void, Void> {

        String toastMessage;

        @Override
        protected Void doInBackground(Boolean... bool) {

            boolean isInFavoriteDb = bool[0];

            if (!isInFavoriteDb) {

                if (mBitmapPoster == null || mBitmapBackdrop == null) {
                    mInFavoriteDb = false;
                    toastMessage = getApplicationContext().getString(R.string.toast_fail_add_favorite);

                    return null;
                }

                String localPathPoster = Utils.saveToInternalStorage(MovieDetailsActivity.this, mBitmapPoster, mMovie.getPosterPath());
                String localPathBackdrop = Utils.saveToInternalStorage(MovieDetailsActivity.this, mBitmapBackdrop, mMovie.getBackdropPath());

                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesContract._ID, mMovie.getId());
                contentValues.put(MoviesContract.COLUMN_TITLE, mMovie.getTitle());
                contentValues.put(MoviesContract.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
                contentValues.put(MoviesContract.COLUMN_POSTER, localPathPoster);
                contentValues.put(MoviesContract.COLUMN_BACKDROP, localPathBackdrop);
                contentValues.put(MoviesContract.COLUMN_PLOT, mMovie.getPlotSynopsis());
                contentValues.put(MoviesContract.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
                contentValues.put(MoviesContract.COLUMN_RATING, mMovie.getRating());

                getContentResolver().insert(MoviesContract.FavoriteEntry.CONTENT_URI, contentValues);

                toastMessage = mMovie.getTitle() + " " + getApplicationContext().getString(R.string.toast_added_to_favorites);

                mInFavoriteDb = true;

                mMovie.setPosterPath(localPathPoster);
                mMovie.setBackdropPath(localPathBackdrop);

            } else {
                getContentResolver().delete(MoviesContract.FavoriteEntry.buildFavoritesUri(mMovie.getId()), null, null);

                File file = new File(mMovie.getPosterPath());
                boolean deleted = file.delete();

                if (deleted) {
                    toastMessage = mMovie.getTitle() + " " + getApplicationContext().getString(R.string.toast_deleted_to_favorites);
                } else {
                    toastMessage = getApplicationContext().getString(R.string.toast_fail_delete_image);
                }

                mInFavoriteDb = false;

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            toggleFavorite();

            //If the movies was just deleted from favorites, and we are in the favorites list , finish activity
            if (!mInFavoriteDb && !mCalledFromCache) {
                finish();
                //else if, the movie is deleted from favorites and we are in a cached list, so no need to finish activity
                //because the exit shared animation will still work
            } else if (!mInFavoriteDb && mCalledFromCache) {
                mInFavoriteDb = false;
                mUri = MoviesContract.CacheMovieEntry.buildMovieCacheUri(Integer.valueOf(getIntent().getData().getPathSegments().get(1)));

                Cursor c = getContentResolver().query(mUri, null, null, null, null);
                if (c != null && c.moveToFirst()) {
                    mMovie = new MovieTmdb(c);
                    downloadImagesForLocalSave();
                }
                c.close();
            }

            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(MovieDetailsActivity.this, toastMessage, Toast.LENGTH_LONG);
            mToast.show();

        }
    }


}
