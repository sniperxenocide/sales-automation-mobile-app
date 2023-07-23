package com.akg.akg_sales.view.adapter.order;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemPendingOrderBinding;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.view.activity.order.OrderDetailActivity;

import java.util.ArrayList;

public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.ViewHolder>{
    private ArrayList<OrderDto> headers;
    private final Context context;

    public PendingOrderAdapter(Context context,ArrayList<OrderDto> objects){
        this.headers = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public PendingOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemPendingOrderBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_pending_order,
                parent,false);
        return new PendingOrderAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingOrderAdapter.ViewHolder holder, int position) {
        OrderDto header = headers.get(position);
        holder.bind(header,Integer.toString(position+1));
        holder.itemBinding.orderItem.setOnClickListener(view -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId",header.getId().toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemPendingOrderBinding itemBinding;
        public ViewHolder(ListitemPendingOrderBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj,String idx) {
            itemBinding.setVariable(BR.vm, obj);
            itemBinding.setVariable(BR.index, idx);
            itemBinding.executePendingBindings();
        }
    }


}
