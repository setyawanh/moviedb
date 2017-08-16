package com.setyawan.moviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.setyawan.moviedb.R;
import com.setyawan.moviedb.model.Trailer;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Pad on 8/6/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {
    Context context;
    List<Trailer> trailerList;

    public TrailerAdapter(Context context, List<Trailer> trailerList) {
        this.context = context;
        this.trailerList = trailerList;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_trailer,parent,false);
        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        Trailer t = trailerList.get(position);
        holder.txtTrailer.setText(t.getName());
        Picasso.with(context)
                .load("https://img.youtube.com/vi/"+t.getKey()+"/0.jpg")
                .placeholder(R.drawable.poster_placeholder)
                .into(holder.imgThumb);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    class TrailerHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView txtTrailer;
        public TrailerHolder(View itemView) {
            super(itemView);
            txtTrailer = (TextView) itemView.findViewById(R.id.trailer);
            imgThumb = (ImageView) itemView.findViewById(R.id.thumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    String videoId = trailerList.get(pos).getKey();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+videoId));
                    if(i.resolveActivity(context.getPackageManager())!=null){
                        context.startActivity(i);
                    }
                }
            });
        }
    }
}
