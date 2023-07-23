package com.akg.akg_sales.view.adapter.delivery;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemDeliveryLineBinding;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;

import java.util.ArrayList;

public class DeliveryLineAdapter extends RecyclerView.Adapter<DeliveryLineAdapter.ViewHolder>{

    private ArrayList<MoveOrderConfirmedLineDto> lines;
    private final DeliveryDetailActivity context;

    public DeliveryLineAdapter(DeliveryDetailActivity context, ArrayList<MoveOrderConfirmedLineDto> objects){
        this.lines = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public DeliveryLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemDeliveryLineBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_delivery_line,
                parent,false);
        return new DeliveryLineAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryLineAdapter.ViewHolder holder, int position) {
        MoveOrderConfirmedLineDto line = lines.get(position);
        holder.bind(line,Integer.toString(position+1));
    }


    @Override
    public int getItemCount() {
        return lines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemDeliveryLineBinding itemBinding;
        public ViewHolder(ListitemDeliveryLineBinding itemBinding) {
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
