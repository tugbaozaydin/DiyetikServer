package com.project.diyetikserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project.diyetikserver.Interface.ItemClickListener;
import com.project.diyetikserver.R;

import info.hoang8f.widget.FButton;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView shipper_name,shipper_phone;
    public FButton btn_edit,btn_remove;
    private ItemClickListener ıtemClickListener;

    public ShipperViewHolder(@NonNull View itemView) {
        super(itemView);
        shipper_name = itemView.findViewById(R.id.shipper_name);
        shipper_phone = itemView.findViewById(R.id.shipper_phone);
        btn_edit = itemView.findViewById(R.id.btnEdit);
        btn_remove = itemView.findViewById(R.id.btnRemove);
    }

    public void setItemClickListener(ItemClickListener ıtemClickListener) {
        this.ıtemClickListener = ıtemClickListener;
    }

    @Override
    public void onClick(View v) {
        ıtemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
