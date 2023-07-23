package com.akg.akg_sales.view.adapter.notification;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemDeliveryConfirmedBinding;
import com.akg.akg_sales.dto.notification.DeliveryConfirmedHeaderDto;
import com.akg.akg_sales.view.activity.notification.DeliveryConfirmedActivity;

import java.util.ArrayList;

public class DeliveryConfirmedAdapter extends RecyclerView.Adapter<DeliveryConfirmedAdapter.ViewHolder>{

    private ArrayList<DeliveryConfirmedHeaderDto> headers;
    private final Context context;

    public DeliveryConfirmedAdapter(Context context, ArrayList<DeliveryConfirmedHeaderDto> objects){
        this.headers = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public DeliveryConfirmedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemDeliveryConfirmedBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_delivery_confirmed,
                parent,false);
        return new DeliveryConfirmedAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryConfirmedAdapter.ViewHolder holder, int position) {
        DeliveryConfirmedHeaderDto header = headers.get(position);
        holder.bind(header);
        holder.itemBinding.cardItem
                .setOnClickListener((v)->openDeliveryDetailPage(header.getId().toString()));
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemDeliveryConfirmedBinding itemBinding;
        public ViewHolder(ListitemDeliveryConfirmedBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj) {
            itemBinding.setVariable(BR.vm, obj);
            itemBinding.executePendingBindings();
        }
    }

    private void openDeliveryDetailPage(String headerId){
        Intent intent = new Intent(context, DeliveryConfirmedActivity.class);
        intent.putExtra("headerId",headerId);
        context.startActivity(intent);
    }
}
