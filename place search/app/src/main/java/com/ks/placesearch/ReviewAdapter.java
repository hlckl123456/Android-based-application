package com.ks.placesearch;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.BubbleImageView;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> reviews;
    private Context context;

    public ReviewAdapter(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Review review= reviews.get(position);

        Picasso.get().load(review.getPhoto()).into(holder.reviewPhoto);
        holder.reviewerName.setText(review.getName());
        holder.reviewRating.setRating(Float.parseFloat(review.getRating()));
        holder.reviewTime.setText(review.getTime());
        holder.reviewComment.setText(review.getComment());
        holder.reviewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri reviewUrl = Uri.parse(review.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, reviewUrl);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView reviewPhoto;
        public TextView reviewerName;
        public RatingBar reviewRating;
        public TextView reviewTime;
        public TextView reviewComment;

        public ViewHolder(View itemView) {
            super(itemView);

            reviewPhoto = (RoundedImageView) itemView.findViewById(R.id.reviewPhoto);
            reviewerName = (TextView) itemView.findViewById(R.id.reviewerName);
            reviewRating = (RatingBar) itemView.findViewById(R.id.reviewRating);
            reviewTime = (TextView) itemView.findViewById(R.id.reviewTime);
            reviewComment = (TextView) itemView.findViewById(R.id.reviewComment);
        }
    }
}


