package com.setyawan.moviedb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pad on 8/6/2017.
 */

public class ReviewList {
    @SerializedName("id")
    private int id;

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<Review> results;

    public int getId() {
        return id;
    }

    public List<Review> getResults() {
        return results;
    }
}
