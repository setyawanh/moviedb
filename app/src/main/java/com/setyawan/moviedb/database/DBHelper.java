package com.setyawan.moviedb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.setyawan.moviedb.model.Movie;
import com.setyawan.moviedb.model.Shows;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pad on 8/8/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "movie.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + DBContract.FavoriteEntry.TABLE_NAME + " (" +
                DBContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBContract.FavoriteEntry.COLUMN_ID_MOVIE + " INTEGER NOT NULL, " +
                DBContract.FavoriteEntry.COLUMN_TYPE_MOVIE + " TEXT NOT NULL, " +
                DBContract.FavoriteEntry.COLUMN_MOVIE + " TEXT NOT NULL, " +
                DBContract.FavoriteEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + DBContract.GenreEntry.TABLE_NAME + " (" +
                DBContract.GenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBContract.GenreEntry.COLUMN_ID_GENRE + " INTEGER NOT NULL, " +
                DBContract.GenreEntry.COLUMN_GENRE + " TEXT NOT NULL " +
                "); ";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
        db.execSQL(SQL_CREATE_GENRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.FavoriteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.GenreEntry.TABLE_NAME);
        onCreate(db);
    }


    // FAVORITE
    public List<Movie> getAllFavorite(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DBContract.FavoriteEntry.COLUMN_TYPE_MOVIE + " = ? ";
        String[] argument = {"1"};
        String orderBy = DBContract.FavoriteEntry.COLUMN_TIMESTAMP + " DESC";
        Cursor cursor = db.query(DBContract.FavoriteEntry.TABLE_NAME,
                null,
                selection,
                argument,
                null,
                null,
                orderBy);

        List<Movie> movies = new ArrayList<>();

        while (cursor.moveToNext()){
            String movieJSON = cursor.getString(cursor.getColumnIndex(DBContract.FavoriteEntry.COLUMN_MOVIE));
            Movie r = new GsonBuilder().create().fromJson(movieJSON, Movie.class);
            movies.add(r);
        }

        cursor.close();
        return movies;
    }

    public List<Shows> getAllFavoriteShow(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DBContract.FavoriteEntry.COLUMN_TYPE_MOVIE + " = ? ";
        String[] argument = {"2"};
        String orderBy = DBContract.FavoriteEntry.COLUMN_TIMESTAMP + " DESC";
        Cursor cursor = db.query(DBContract.FavoriteEntry.TABLE_NAME,
                null,
                selection,
                argument,
                null,
                null,
                orderBy);

        List<Shows> movies = new ArrayList<>();

        while (cursor.moveToNext()){
            String movieJSON = cursor.getString(cursor.getColumnIndex(DBContract.FavoriteEntry.COLUMN_MOVIE));
            Shows r = new GsonBuilder().create().fromJson(movieJSON, Shows.class);
            movies.add(r);
        }

        cursor.close();
        return movies;
    }

    public void addFavorite(int id, String movie){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.FavoriteEntry.COLUMN_ID_MOVIE,id);
        cv.put(DBContract.FavoriteEntry.COLUMN_TYPE_MOVIE,"1");
        cv.put(DBContract.FavoriteEntry.COLUMN_MOVIE,movie);
        db.insert(DBContract.FavoriteEntry.TABLE_NAME,null,cv);
    }

    public void addFavoriteShow(int id, String movie){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.FavoriteEntry.COLUMN_ID_MOVIE,id);
        cv.put(DBContract.FavoriteEntry.COLUMN_TYPE_MOVIE,"2");
        cv.put(DBContract.FavoriteEntry.COLUMN_MOVIE,movie);
        db.insert(DBContract.FavoriteEntry.TABLE_NAME,null,cv);
    }

    public void deleteFavorite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DBContract.FavoriteEntry.COLUMN_ID_MOVIE + " = ?";
        String[] argument = {String.valueOf(id)};
        db.delete(DBContract.FavoriteEntry.TABLE_NAME, selection, argument);
    }

    public boolean isFavorite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DBContract.FavoriteEntry.COLUMN_ID_MOVIE + " = ? ";
        String[] argument = {String.valueOf(id)};
        Cursor cursor = db.query(DBContract.FavoriteEntry.TABLE_NAME,
                null,
                selection,
                argument,
                null,
                null,
                null);

        return cursor.getCount() != 0;
    }


    //GENRE
    public String getGenre(List<Integer> genres){
        List<String> param = new ArrayList<>();
        List<String> arg = new ArrayList<>();
        for (Integer i: genres) {
            param.add("?");
            arg.add(String.valueOf(i));
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DBContract.GenreEntry.COLUMN_ID_GENRE + " in ("+TextUtils.join(",",param)+")";
        String[] argument = arg.toArray(new String[0]);
        String orderBy = DBContract.GenreEntry.COLUMN_GENRE + " ASC";
        Cursor cursor = db.query(DBContract.GenreEntry.TABLE_NAME,
                null,
                selection,
                argument,
                null,
                null,
                orderBy);

        List<String> result = new ArrayList<>();

        while (cursor.moveToNext()){
            String genre = cursor.getString(cursor.getColumnIndex(DBContract.GenreEntry.COLUMN_GENRE));
            result.add(genre);
        }

        cursor.close();
        return TextUtils.join(", ",result);
    }

    public void addGenre(int id, String genre){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.GenreEntry.COLUMN_ID_GENRE,id);
        cv.put(DBContract.GenreEntry.COLUMN_GENRE,genre);
        db.insert(DBContract.GenreEntry.TABLE_NAME,null,cv);
    }

    public void deleteGenre(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DBContract.GenreEntry.COLUMN_ID_GENRE + " = ?";
        String[] argument = {String.valueOf(id)};
        db.delete(DBContract.GenreEntry.TABLE_NAME, selection, argument);
    }

    public boolean isGenre(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DBContract.GenreEntry.COLUMN_ID_GENRE + " = ? ";
        String[] argument = {String.valueOf(id)};
        Cursor cursor = db.query(DBContract.GenreEntry.TABLE_NAME,
                null,
                selection,
                argument,
                null,
                null,
                null);

        return cursor.getCount() != 0;
    }
}
