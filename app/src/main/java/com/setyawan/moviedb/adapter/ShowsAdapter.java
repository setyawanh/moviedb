package com.setyawan.moviedb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.setyawan.moviedb.DetailShowsActivity;
import com.setyawan.moviedb.R;
import com.setyawan.moviedb.model.Shows;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Pad on 8/5/2017.
 */

public class ShowsAdapter extends RecyclerView.Adapter<ShowsAdapter.MovieHolder> {
    private List<Shows> showsList;
    private Context context;

    public ShowsAdapter(List<Shows> showsList, Context context) {
        this.context = context;
        this.showsList = showsList;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_movie,parent,false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieHolder holder, final int position) {
        final Shows shows = showsList.get(position);
        Picasso.with(holder.itemView.getContext())
                .load(ApiInterface.BASE_IMG_MED + shows.getPosterPath())
                .placeholder(R.drawable.poster_placeholder)
                .fit()
                .centerCrop()
                .into(holder.moviePoster);
        holder.title.setText(shows.getName());
        holder.rate.setText(String.valueOf(shows.getVoteAverage()));

        ViewCompat.setTransitionName(holder.moviePoster, shows.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailShowsActivity.class);
                i.putExtra("shows", new GsonBuilder().create().toJson(shows));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context
                        ,holder.moviePoster,"imageTrans");
                ActivityCompat.startActivity(context,i,options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return showsList.size();
    }

    public void addMovieList(List<Shows> showsList) {
        this.showsList.addAll(showsList);
        notifyDataSetChanged();
    }

    public void setMovieList(List<Shows> showsList) {
        this.showsList = showsList;
        notifyDataSetChanged();
    }
    class MovieHolder extends RecyclerView.ViewHolder{
        ImageView moviePoster;
        TextView title, rate;
        public MovieHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            title = (TextView) itemView.findViewById(R.id.title);
            rate = (TextView) itemView.findViewById(R.id.rate);
        }
    }
}
