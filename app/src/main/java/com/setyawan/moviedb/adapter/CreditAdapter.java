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
import com.setyawan.moviedb.DetailShowsActivity;
import com.setyawan.moviedb.R;
import com.setyawan.moviedb.model.Movie;
import com.setyawan.moviedb.model.Shows;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Pad on 8/17/2017.
 */

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.CreditHolder> {
    private List<Movie> movieList;
    private List<Shows> showsList;
    private Context context;
    private String type;

    public CreditAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
        movieList = new ArrayList<>();
        showsList = new ArrayList<>();
    }

    @Override
    public CreditHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_image,parent,false);
        return new CreditHolder(view);
    }

    @Override
    public void onBindViewHolder(final CreditHolder holder, int position) {
        if(type.equals("movie")) {
            final Movie m = movieList.get(position);
            Picasso.with(context).load(ApiInterface.BASE_IMG_MED + m.getPosterPath())
                    .placeholder(R.drawable.poster_placeholder).into(holder.image);
            holder.title.setText(m.getTitle());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToActivity(holder.image,new GsonBuilder().create().toJson(m), DetailMovieActivity.class);
                }
            });

        } else if(type.equals("shows")) {
            final Shows s = showsList.get(position);
            Picasso.with(context).load(ApiInterface.BASE_IMG_MED + s.getPosterPath())
                    .placeholder(R.drawable.poster_placeholder).into(holder.image);
            holder.title.setText(s.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToActivity(holder.image,new GsonBuilder().create().toJson(s), DetailShowsActivity.class);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (type.equals("movie"))? movieList.size() : showsList.size();
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public void setShowsList(List<Shows> showsList) {
        this.showsList = showsList;
        notifyDataSetChanged();
    }

    public void goToActivity(View v, String value, Class activity) {
        Intent i = new Intent(context, activity);
        i.putExtra(type, value);
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation((Activity)context,v,"imageTrans");
        ActivityCompat.startActivity(context,i,options.toBundle());
    }

    class CreditHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        public CreditHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
