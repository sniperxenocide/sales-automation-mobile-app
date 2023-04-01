package com.akg.akg_sales.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemOrderSkuBinding;
import com.akg.akg_sales.dto.ItemDto;
import com.akg.akg_sales.view.dialog.OrderItemQuantityDialog;

import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{
    private ArrayList<ItemDto> headers;
    private final Context context;

    public OrderItemAdapter(Context context,ArrayList<ItemDto> objects){
        this.headers = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemOrderSkuBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_order_sku,
                parent,false);
        return new OrderItemAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.ViewHolder holder, int position) {
        ItemDto header = headers.get(position);
        holder.bind(header,Integer.toString(position+1));
        holder.itemBinding.cardItem
                .setOnClickListener((v)->onItemSelect(position));
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemOrderSkuBinding itemBinding;
        public ViewHolder(ListitemOrderSkuBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj,String idx) {
            itemBinding.setVariable(BR.vm, obj);
            itemBinding.setVariable(BR.index, idx);
            itemBinding.executePendingBindings();
        }
    }

    private void onItemSelect(int position){
        OrderItemQuantityDialog dialog = new OrderItemQuantityDialog(context, headers.get(position));
    }
}
