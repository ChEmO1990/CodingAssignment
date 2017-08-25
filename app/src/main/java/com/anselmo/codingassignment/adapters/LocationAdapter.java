package com.anselmo.codingassignment.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anselmo.codingassignment.R;
import com.anselmo.codingassignment.models.BBVALocation;
import com.anselmo.codingassignment.ui.activities.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chemo on 8/16/17.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.RouteHolder> {
    private Context mContext;
    private List<BBVALocation> itemsList;

    public LocationAdapter(Context mContext, List<BBVALocation> itemsList) {
        this.mContext = mContext;
        this.itemsList = itemsList;
    }

    @Override
    public RouteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new RouteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RouteHolder holder, int position) {
        BBVALocation item = itemsList.get(position);
        holder.title.setText(item.getName());
        Picasso.with(mContext).load(item.getIcon()).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class RouteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public ImageView icon;

        public RouteHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.lbl_item_placeholder);
            icon = (ImageView) view.findViewById(R.id.icon_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position = getLayoutPosition();

            BBVALocation currentRoute = itemsList.get(position);

            Intent intentDetail = new Intent(mContext, DetailActivity.class);
            intentDetail.putExtra("current_location", currentRoute);
            mContext.startActivity(intentDetail);
        }
    }
}