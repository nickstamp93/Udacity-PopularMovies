package com.nickstamp.dev.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nickstamp.dev.popularmovies.MainActivity;
import com.nickstamp.dev.popularmovies.R;
import com.nickstamp.dev.popularmovies.utils.AppConstants;

/**
 * Created by Dev on 26/1/2017.
 */

public class MoviesAdapter extends CursorRecyclerViewAdapter<MoviesAdapter.MovieHolder> {


    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private final Context mContext;
    private MovieGridClickListener mGridClickListener;
    private boolean mUseLocalStorageForImg = false;

    public interface MovieGridClickListener {
        void onGridItemClick(int movieId, ImageView img);
    }

    public MoviesAdapter(Context context, Cursor cursor, MovieGridClickListener gridItemClickListener) {
        super(context, cursor);
        this.mContext = context;
        this.mGridClickListener = gridItemClickListener;
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MovieHolder viewHolder, Cursor cursor) {
        viewHolder.bind(cursor);
    }

    @Override
    public MoviesAdapter.MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.grid_item_movie_poster, parent, false);

        return new MovieHolder(itemView);
    }

    public Cursor swapCursor(Cursor newCursor, boolean useLocalStorage) {
        this.mUseLocalStorageForImg = useLocalStorage;
        return super.swapCursor(newCursor);
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView posterImage;

        MovieHolder(View itemView) {
            super(itemView);

            posterImage = (ImageView) itemView.findViewById(R.id.ivMoviePosterGrid);

            itemView.setOnClickListener(this);

        }

        public void bind(Cursor cursor) {

            String imgUri;

            if (mUseLocalStorageForImg) {
                imgUri = cursor.getString(MainActivity.INDEX_MOVIE_POSTER);
            } else {
                imgUri = AppConstants.IMAGE_POSTER_BASE_URL + cursor.getString(MainActivity.INDEX_MOVIE_POSTER);
            }

            ViewCompat.setTransitionName(posterImage, "img_grid_" + getAdapterPosition());

            posterImage.setContentDescription(mContext.getString(R.string.text_movie_poster) + " " + getAdapterPosition());

            Glide.with(mContext)
                    .load(imgUri)
                    .centerCrop()
                    .crossFade()
                    .error(R.drawable.ic_error_poster)
                    .into(posterImage);

        }

        @Override
        public void onClick(View view) {

            getCursor().moveToPosition(getAdapterPosition());
            int id = getCursor().getInt(MainActivity.INDEX_MOVIE_ID);

            mGridClickListener.onGridItemClick(id, posterImage);
        }
    }
}
