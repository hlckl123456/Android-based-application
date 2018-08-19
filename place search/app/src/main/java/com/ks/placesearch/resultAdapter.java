package com.ks.placesearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class resultAdapter extends RecyclerView.Adapter<resultAdapter.ViewHolder> {

    private List<result> results;
    private Context context;
    private SharedPreferenceManager sharedPreferenceManager;
    private FragmentFavorites fragmentFavorites;

    public resultAdapter(List<result> results, Context context) {
        this.results = results;
        this.context = context;
    }

    public resultAdapter(List<result> results, Context context, FragmentFavorites fragmentFavorites) {
        this.results = results;
        this.context = context;
        this.fragmentFavorites = fragmentFavorites;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        sharedPreferenceManager = new SharedPreferenceManager(context);
        final result result= results.get(position);
        holder.resultName.setText(result.getName());
        holder.resultAddress.setText(result.getAddress());
        Picasso.get().load(result.getIcon()).into(holder.resultIcon);
        if (sharedPreferenceManager.isFavourite(result.getPlaceId())) {
            holder.favBtn.setImageResource(R.drawable.hear_fill_red);
        }

        holder.detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,  detailActivtiy.class);
                intent.putExtra("placeId", result.getPlaceId());
                intent.putExtra("address", result.getAddress());
                intent.putExtra("position", result.getPosition());
                intent.putExtra("name", result.getName());
                context.startActivity(intent);
            }
        });


        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView imageView = (ImageView) v;
                String placeId = result.getPlaceId();
                if (sharedPreferenceManager.isFavourite(placeId)) {
                    sharedPreferenceManager.removeFavourite(result.getPlaceId());
                    imageView.setImageResource(R.drawable.heart_outline_black);
                    fragmentFavorites.generateFavoriteList();
                    toast(result.getName() + " was removed to favorites");
                } else {
                    Gson gson = new Gson();
                    String json = gson.toJson(result);
                    sharedPreferenceManager.setFavourite(result.getPlaceId(), json);
                    imageView.setImageResource(R.drawable.hear_fill_red);
                    toast(result.getName() + " was added to favorites");
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView resultName;
        public TextView resultAddress;
        public ImageView resultIcon;
        public LinearLayout detailBtn;
        public ImageView favBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            resultName = (TextView) itemView.findViewById(R.id.resultName);
            resultAddress = (TextView) itemView.findViewById(R.id.resultAddress);
            resultIcon = (ImageView) itemView.findViewById(R.id.resultIcon);
            detailBtn = (LinearLayout) itemView.findViewById(R.id.detailBtn);
            favBtn = (ImageView) itemView.findViewById(R.id.favBtn);
        }
    }

    private void toast(String s){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }
}
