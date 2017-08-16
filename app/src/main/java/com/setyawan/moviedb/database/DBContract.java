package com.setyawan.moviedb.database;

import android.provider.BaseColumns;

/**
 * Created by Pad on 8/8/2017.
 */

public final class DBContract {
    private DBContract(){}

    public static class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_ID_MOVIE = "id_movie";
        public static final String COLUMN_TYPE_MOVIE = "type_movie";
        public static final String COLUMN_MOVIE = "movie";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static class GenreEntry implements BaseColumns {
        public static final String TABLE_NAME = "genre";
        public static final String COLUMN_ID_GENRE = "id_genre";
        public static final String COLUMN_GENRE = "genre";
    }
}
