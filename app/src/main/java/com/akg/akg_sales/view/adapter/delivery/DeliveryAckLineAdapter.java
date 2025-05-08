package com.akg.akg_sales.view.adapter.delivery;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemDeliveryAckLineBinding;
import com.akg.akg_sales.dto.delivery.DeliveryAcknowledgeLineDto;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;

import java.util.ArrayList;

public class DeliveryAckLineAdapter extends RecyclerView.Adapter<DeliveryAckLineAdapter.ViewHolder>{
    private ArrayList<DeliveryAcknowledgeLineDto> lines;
    private final DeliveryDetailActivity context;

    public DeliveryAckLineAdapter(DeliveryDetailActivity context, ArrayList<DeliveryAcknowledgeLineDto> objects){
        this.lines = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public DeliveryAckLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemDeliveryAckLineBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_delivery_ack_line,
                parent,false);
        return new DeliveryAckLineAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryAckLineAdapter.ViewHolder holder, int position) {
        DeliveryAcknowledgeLineDto line = lines.get(position);
        holder.bind(line,Integer.toString(position+1));
    }


    @Override
    public int getItemCount() {
        return lines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemDeliveryAckLineBinding itemBinding;
        public ViewHolder(ListitemDeliveryAckLineBinding itemBinding) {
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
