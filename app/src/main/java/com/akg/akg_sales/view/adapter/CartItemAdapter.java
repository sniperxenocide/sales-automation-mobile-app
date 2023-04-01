package com.akg.akg_sales.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemCartItemBinding;
import com.akg.akg_sales.dto.CartItemDto;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder>{
    private ArrayList<CartItemDto> headers;
    private final Context context;

    public CartItemAdapter(Context context,ArrayList<CartItemDto> objects){
        this.headers = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemCartItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_cart_item,
                parent,false);
        return new CartItemAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ViewHolder holder, int position) {
        CartItemDto header = headers.get(position);
        holder.bind(header,Integer.toString(position+1));
    }


    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemCartItemBinding itemBinding;
        public ViewHolder(ListitemCartItemBinding itemBinding) {
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
