package com.nickstamp.dev.popularmovies.model;

import android.app.Activity;
import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nickstamp.dev.popularmovies.R;
import com.nickstamp.dev.popularmovies.data.MoviesContract;
import com.nickstamp.dev.popularmovies.utils.AppConstants;
/**
 * Created by Dev on 16/1/2017.
 */

/**
 * Class representing a single MovieTmdb, containing all the possible details of a movie.
 */
public class MovieTmdb implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("original_title")
    @Expose
    private String originalTitle;

    @SerializedName("overview")
    @Expose
    private String plotSynopsis;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName("vote_average")
    @Expose
    private double rating;

    protected MovieTmdb(Parcel in) {
        id = in.readInt();
        title = in.readString();
        originalTitle = in.readString();
        plotSynopsis = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        rating = in.readDouble();
    }

    public MovieTmdb(Cursor c) {
        this.id = c.getInt(c.getColumnIndex(MoviesContract._ID));
        this.title = c.getString(c.getColumnIndex(MoviesContract.COLUMN_TITLE));
        this.originalTitle = c.getString(c.getColumnIndex(MoviesContract.COLUMN_ORIGINAL_TITLE));
        this.posterPath = c.getString(c.getColumnIndex(MoviesContract.COLUMN_POSTER));
        this.backdropPath = c.getString(c.getColumnIndex(MoviesContract.COLUMN_BACKDROP));
        this.plotSynopsis = c.getString(c.getColumnIndex(MoviesContract.COLUMN_PLOT));
        this.releaseDate = c.getString(c.getColumnIndex(MoviesContract.COLUMN_RELEASE_DATE));
        this.rating = c.getDouble(c.getColumnIndex(MoviesContract.COLUMN_RATING));
    }

    public static final Creator<MovieTmdb> CREATOR = new Creator<MovieTmdb>() {

        @Override
        public MovieTmdb createFromParcel(Parcel in) {
            return new MovieTmdb(in);
        }

        @Override
        public MovieTmdb[] newArray(int size) {
            return new MovieTmdb[size];
        }

    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(originalTitle);
        parcel.writeString(plotSynopsis);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeDouble(rating);

    }

    @BindingAdapter({"bind:backdropUrl"})
    public static void loadBackdrop(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .error(R.drawable.ic_error_poster)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);

    }

    @BindingAdapter({"bind:posterUrl"})
    public static void loadPoster(final Activity activity, ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .crossFade()
                .error(R.drawable.ic_error_poster)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new ImageViewTarget<GlideDrawable>(view) {
                    @Override
                    protected void setResource(GlideDrawable resource) {
                        ActivityCompat.startPostponedEnterTransition(activity);
                        view.setImageDrawable(resource);
                    }
                });

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterUrl() {
        return AppConstants.IMAGE_POSTER_BASE_URL + posterPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getRating() {
        return rating + " / 10";
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackdropUrl() {
        return AppConstants.IMAGE_BACKDROP_BASE_URL + backdropPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}
