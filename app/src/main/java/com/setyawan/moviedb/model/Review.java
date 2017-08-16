package com.setyawan.moviedb.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pad on 8/6/2017.
 */

public class Review {
    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
