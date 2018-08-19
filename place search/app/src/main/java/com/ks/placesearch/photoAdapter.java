package com.ks.placesearch;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class photoAdapter extends RecyclerView.Adapter<photoAdapter.ViewHolder> {

    private List<String> photos;
    private Context context;

    public photoAdapter(List<String> photos, Context context) {
        this.photos = photos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String photo = photos.get(position);

        Picasso.get().load(photo).into(holder.photo);

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);

            photo = (ImageView)itemView.findViewById(R.id.detailPhoto);
        }
    }
}
