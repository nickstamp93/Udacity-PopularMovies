package com.nickstamp.dev.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Dev on 25/1/2017.
 */

public class MovieResults{

    @SerializedName("results")
    @Expose
    private ArrayList<MovieTmdb> movies = new ArrayList<>();

    /**
     * @return The movies
     */
    public ArrayList<MovieTmdb> getMovies() {
        return movies;
    }

    /**
     * @param movies The movies
     */
    public void setMovies(ArrayList<MovieTmdb> movies) {
        this.movies = movies;
    }

}
