package com.setyawan.moviedb.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pad on 8/8/2017.
 */

public class Genre {
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
