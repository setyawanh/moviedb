package com.setyawan.moviedb.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.setyawan.moviedb.R;
import com.setyawan.moviedb.model.Episode;
import com.setyawan.moviedb.model.Season;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Pad on 8/17/2017.
 */

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.SeasonHolder> {
    private Context context;
    private List<Season> seasonList;
    private int color;

    public SeasonAdapter(Context context, List<Season> seasonList) {
        this.context = context;
        this.seasonList = seasonList;
        color = 0;
    }

    @Override
    public SeasonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_season,parent,false);
        return new SeasonHolder(view);
    }

    @Override
    public void onBindViewHolder(final SeasonHolder holder, int position) {
        Season s = seasonList.get(position);
        holder.title.setText(s.getName());
        holder.seasonView.setVisibility(View.GONE);
        holder.seasonView.setLayoutManager(new LinearLayoutManager(context));
        holder.seasonView.setAdapter(new EpisodeAdapter(s.getEpisodes()));

        holder.bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.seasonView.getVisibility()==View.GONE){
                    holder.seasonView.setVisibility(View.VISIBLE);
                    holder.indicator.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    holder.seasonView.setVisibility(View.GONE);
                    holder.indicator.setImageResource(R.drawable.ic_arrow_down);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return seasonList.size();
    }

    public void addSeason(Season season) {
        seasonList.add(season);
        notifyDataSetChanged();
    }

    public void setColor(int color) {
        this.color=color;
        notifyDataSetChanged();
    }

    class SeasonHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView indicator;
        RecyclerView seasonView;
        LinearLayout bar;
        public SeasonHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            indicator = (ImageView) itemView.findViewById(R.id.season_indicator);
            seasonView = (RecyclerView) itemView.findViewById(R.id.season_view);
            bar = (LinearLayout) itemView.findViewById(R.id.bar);
        }
    }


    class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeHolder>{
        private List<Episode> episodeList;

        public EpisodeAdapter(List<Episode> episodeList) {
            this.episodeList = episodeList;
        }

        @Override
        public EpisodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_episode,parent,false);
            return new EpisodeHolder(view);
        }

        @Override
        public void onBindViewHolder(EpisodeHolder holder, int position) {
            Episode e = episodeList.get(position);
            holder.number.setText(String.valueOf(e.getEpisodeNumber()));
            if(color!=0) holder.number.setBackgroundColor(color);

            holder.episode.setText(e.getName());
            holder.airing.setText(e.getAirDate());
            holder.summary.setText(e.getOverview());
            Picasso.with(context)
                    .load(ApiInterface.BASE_IMG_MED+e.getStillPath())
                    .placeholder(R.drawable.poster_placeholder)
                    .centerCrop()
                    .fit()
                    .into(holder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return episodeList.size();
        }

        class EpisodeHolder extends RecyclerView.ViewHolder{
            TextView episode, summary, airing, number;
            ImageView thumbnail;
            public EpisodeHolder(View itemView) {
                super(itemView);
                number = (TextView) itemView.findViewById(R.id.number);
                episode = (TextView) itemView.findViewById(R.id.episode);
                airing = (TextView) itemView.findViewById(R.id.airing);
                summary = (TextView) itemView.findViewById(R.id.summary);
                thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            }
        }
    }
}
