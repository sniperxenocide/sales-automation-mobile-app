package com.akg.akg_sales.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemDeliveryConfirmedLineBinding;
import com.akg.akg_sales.dto.notification.DeliveryConfirmedLineDto;

import java.util.ArrayList;

public class DeliveryConfirmedLineAdapter extends RecyclerView.Adapter<DeliveryConfirmedLineAdapter.ViewHolder> {
    private ArrayList<DeliveryConfirmedLineDto> lines;
    private final Context context;

    public DeliveryConfirmedLineAdapter(Context context, ArrayList<DeliveryConfirmedLineDto> objects){
        this.lines = objects;
        this.context = context;
    }


    @NonNull
    @Override
    public DeliveryConfirmedLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemDeliveryConfirmedLineBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_delivery_confirmed_line,
                parent,false);
        return new DeliveryConfirmedLineAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryConfirmedLineAdapter.ViewHolder holder, int position) {
        DeliveryConfirmedLineDto line = lines.get(position);
        holder.bind(line,Integer.toString(position+1));
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemDeliveryConfirmedLineBinding itemBinding;
        public ViewHolder(ListitemDeliveryConfirmedLineBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj,String idx) {
            itemBinding.setVariable(BR.vm, obj);
            itemBinding.setVariable(BR.index,idx);
            itemBinding.executePendingBindings();
        }
    }
}
