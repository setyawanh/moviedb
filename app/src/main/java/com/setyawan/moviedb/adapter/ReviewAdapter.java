package com.setyawan.moviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.setyawan.moviedb.R;
import com.setyawan.moviedb.model.Review;

import java.util.List;

/**
 * Created by Pad on 8/6/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_review,parent,false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Review r = reviewList.get(position);
        String content = r.getContent();
        if(content.length()>100){
            holder.isLess = true;
            content = content.substring(0,99)+"...<b>more</b>";
        }
        holder.author.setText(r.getAuthor());
        holder.content.setText(Html.fromHtml(content));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class ReviewHolder extends RecyclerView.ViewHolder{
        TextView author, content;
        boolean isLess;
        public ReviewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.author);
            content = (TextView) itemView.findViewById(R.id.content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    String contents = reviewList.get(pos).getContent();
                    if(isLess) {
                        content.setText(contents);
                        isLess = false;
                    } else {
                        if(contents.length()>100) content.setText(Html.fromHtml(contents.substring(0,99)+"...<b>more</b>"));
                        isLess = true;
                    }
                }
            });
        }
    }
}
