package com.setyawan.moviedb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pad on 8/9/2017.
 */

public class GenreList {
    @SerializedName("genres")
    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
