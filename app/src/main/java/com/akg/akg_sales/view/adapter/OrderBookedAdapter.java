package com.akg.akg_sales.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemOrderBookedBinding;
import com.akg.akg_sales.dto.notification.OrderBookedHeaderDto;
import com.akg.akg_sales.view.activity.notification.OrderBookedActivity;

import java.util.ArrayList;

public class OrderBookedAdapter extends RecyclerView.Adapter<OrderBookedAdapter.ViewHolder>{
    private  ArrayList<OrderBookedHeaderDto> headers;
    private final Context context;

    public OrderBookedAdapter(Context context, ArrayList<OrderBookedHeaderDto> objects){
        this.headers = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemOrderBookedBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_order_booked,
                parent,false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderBookedHeaderDto header = headers.get(position);
        holder.bind(header);
        holder.itemBinding.cardItem
                .setOnClickListener((v)->openOrderDetailPage(header.getId()));
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemOrderBookedBinding itemBinding;
        public ViewHolder(ListitemOrderBookedBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj) {
            itemBinding.setVariable(BR.vm, obj);
            itemBinding.executePendingBindings();
        }
    }

    private void openOrderDetailPage(Long orderId){
        Intent intent = new Intent(context, OrderBookedActivity.class);
        intent.putExtra("orderId",orderId.toString());
        context.startActivity(intent);
    }
}
