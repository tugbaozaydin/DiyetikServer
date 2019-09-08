package com.project.diyetikserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.diyetikserver.Model.Order;
import com.project.diyetikserver.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView name, quantitiy, price, discount;

    public MyViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantitiy = itemView.findViewById(R.id.product_quantity);
        price = itemView.findViewById(R.id.product_price);
        discount = itemView.findViewById(R.id.product_discount);
    }

}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {
List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_detail_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Order order = myOrders.get(i);
        myViewHolder.name.setText(String.format("Name : %s",order.getProductName()));
        myViewHolder.quantitiy.setText(String.format("Quantity : %s",order.getQuantity()));
        myViewHolder.price.setText(String.format("Price: %s",order.getPrice()));
        myViewHolder.discount.setText(String.format("Discount : %s",order.getDiscount()));
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}