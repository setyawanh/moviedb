package com.setyawan.moviedb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.setyawan.moviedb.DetailMovieActivity;
import com.setyawan.moviedb.R;
import com.setyawan.moviedb.database.DBHelper;
import com.setyawan.moviedb.model.Movie;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Pad on 8/8/2017.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {
    private List<Movie> movieList;
    private Context context;

    public FavoriteAdapter(Context context) {
        this.context = context;
        DBHelper mDbHelper = new DBHelper(context);
        movieList = mDbHelper.getAllFavorite();
    }

    @Override
    public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_movie,parent,false);
        return new FavoriteHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavoriteHolder holder, int position) {
        final Movie movie = movieList.get(position);

        Picasso.with(holder.itemView.getContext())
                .load(ApiInterface.BASE_IMG_MED + movie.getPosterPath())
                .fit()
                .centerCrop()
                .into(holder.moviePoster);
        holder.title.setText(movie.getTitle());
        holder.rate.setText(String.valueOf(movie.getVoteAverage()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailMovieActivity.class);
                i.putExtra("movie", new GsonBuilder().create().toJson(movie));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context
                        ,holder.moviePoster,"imageTrans");
                ActivityCompat.startActivity(context,i,options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class FavoriteHolder extends RecyclerView.ViewHolder {
        ImageView moviePoster;
        TextView title, rate;
        public FavoriteHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            title = (TextView) itemView.findViewById(R.id.title);
            rate = (TextView) itemView.findViewById(R.id.rate);
        }
    }
}
