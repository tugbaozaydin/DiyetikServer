package com.project.diyetikserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.diyetikserver.Common.Common;
import com.project.diyetikserver.Interface.ItemClickListener;
import com.project.diyetikserver.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener
{
    public TextView bannerName;
    public ImageView bannerImage;


    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
        bannerName = itemView.findViewById(R.id.banner_name);
        bannerImage = itemView.findViewById(R.id.banner_image);

        itemView.setOnCreateContextMenuListener(this);


    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");

        menu.add(0,0,getAdapterPosition(), Common.UPDATE);

        menu.add(0,1,getAdapterPosition(), Common.DELETE);


    }
}


