package com.project.diyetikserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.diyetikserver.Interface.ItemClickListener;
import com.project.diyetikserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder  {

    public TextView txtOrderId, txtOrderStatus, txtOrderDate,txtOrderPhone, txtOrderAddress;
    public Button btnEdit, btnRemove, btnDetail, btnDirection;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView) itemView.findViewById(R.id.order_name);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderDate = (TextView) itemView.findViewById(R.id.order_date);

        //Görselde buton yok horizontal(yanyana) dört buton eklenecek
        btnDetail = itemView.findViewById(R.id.btnDetail);
        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        btnDirection = itemView.findViewById(R.id.btnDirection);

    }


}

