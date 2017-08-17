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
import com.setyawan.moviedb.DetailCastActivity;
import com.setyawan.moviedb.R;
import com.setyawan.moviedb.model.Cast;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Pad on 8/12/2017.
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastHolder> {
    Context context;
    List<Cast> castList;

    public CastAdapter(Context context, List<Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    @Override
    public CastHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cast,parent,false);
        return new CastHolder(view);
    }

    @Override
    public void onBindViewHolder(final CastHolder holder, int position) {
        final Cast cast = castList.get(position);
        Picasso.with(context)
                .load(ApiInterface.BASE_IMG_URL+cast.getProfilePath())
                .placeholder(R.drawable.cast)
                .into(holder.image);
        holder.cast.setText(cast.getName());
        holder.as.setText(cast.getCharacter());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailCastActivity.class);
                i.putExtra("cast", new GsonBuilder().create().toJson(cast));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context
                        ,holder.image,"imageTrans");
                ActivityCompat.startActivity(context,i,options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (castList.size()>10)?10 : castList.size();
    }

    class CastHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView cast, as;
        public CastHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.thumbnail);
            cast = (TextView) itemView.findViewById(R.id.cast);
            as = (TextView) itemView.findViewById(R.id.as);
        }
    }
}
