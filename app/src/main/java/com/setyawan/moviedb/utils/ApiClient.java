package com.setyawan.moviedb.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pad on 8/5/2017.
 */

public class ApiClient {
    public static final String BASE_URL_MOVIE ="https://api.themoviedb.org/3/";

    public static Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_MOVIE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
