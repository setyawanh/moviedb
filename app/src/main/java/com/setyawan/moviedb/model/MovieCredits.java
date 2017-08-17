package com.setyawan.moviedb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pad on 8/17/2017.
 */

public class MovieCredits {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("cast")
    @Expose
    private List<Movie> movies = null;

    public Integer getId() {
        return id;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
