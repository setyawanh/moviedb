package com.setyawan.moviedb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
 * Created by Pad on 8/10/2017.
 */

public class SearchShowsAdapter extends RecyclerView.Adapter<SearchShowsAdapter.SearchHolder> {
    private List<Shows> showsList;
    private Context context;

    public SearchShowsAdapter(List<Shows> showsList, Context context) {
        this.showsList = showsList;
        this.context = context;
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_search,parent,false);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchHolder holder, int position) {
        final Shows shows = showsList.get(position);
        String title = (shows.getName().equals(shows.getOriginalName()))? "<b>"+shows.getName()+"</b>" : "<b>"+shows.getName()+"</b><br><i>("+shows.getOriginalName()+")</i>";
        holder.title.setText(Html.fromHtml(title));
        holder.year.setText(shows.getFirstAirDate().split("-")[0]);
        Picasso.with(context)
                .load(ApiInterface.BASE_IMG_URL+shows.getPosterPath())
                .placeholder(R.drawable.poster_placeholder)
                .fit()
                .centerCrop()
                .into(holder.poster);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailShowsActivity.class);
                i.putExtra("shows", new GsonBuilder().create().toJson(shows));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context
                        ,holder.poster,"imageTrans");
                ActivityCompat.startActivity(context,i,options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return showsList.size();
    }

    class SearchHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, year;
        public SearchHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.poster);
            title = (TextView) itemView.findViewById(R.id.title);
            year = (TextView) itemView.findViewById(R.id.year);
        }
    }
}
