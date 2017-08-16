package com.setyawan.moviedb.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pad on 8/6/2017.
 */

public class Trailer {
    @SerializedName("name")
    private String name;
    @SerializedName("key")
    private String key;

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
