package com.akg.akg_sales.view.adapter.delivery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemDeliveryBinding;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedHeaderDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;
import com.akg.akg_sales.view.activity.delivery.DeliveryListActivity;
import com.akg.akg_sales.view.dialog.GeneralDialog;

import java.util.ArrayList;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.ViewHolder>{
    private ArrayList<MoveOrderConfirmedHeaderDto> headers;
    private final DeliveryListActivity context;

    public DeliveryAdapter(DeliveryListActivity context, ArrayList<MoveOrderConfirmedHeaderDto> objects){
        this.headers = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public DeliveryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemDeliveryBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_delivery,
                parent,false);
        return new DeliveryAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryAdapter.ViewHolder holder, int position) {
        MoveOrderConfirmedHeaderDto header = headers.get(position);
        holder.bind(header,position);
        holder.itemBinding.deliveryHeader
                .setOnClickListener((v)->openDeliveryDetailPage(header));
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemDeliveryBinding itemBinding;
        public ViewHolder(ListitemDeliveryBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj,int idx) {
            itemBinding.setVariable(BR.vm, obj);
            itemBinding.setIndex(String.valueOf(idx+1));
            itemBinding.setCanView(CommonUtil.deliveryPermission.getCanViewDeliveryReceiving());
            itemBinding.executePendingBindings();

            itemBinding.frTxt.setOnClickListener(view -> new GeneralDialog(context,"Fully Received Delivery Count"));
            itemBinding.prTxt.setOnClickListener(view -> new GeneralDialog(context,"Partially Received Delivery Count"));
            itemBinding.nrTxt.setOnClickListener(view -> new GeneralDialog(context,"Not Received Delivery Count"));
        }
    }

    private void openDeliveryDetailPage(MoveOrderConfirmedHeaderDto header){
        Intent intent = new Intent(context, DeliveryDetailActivity.class);
        intent.putExtra("moveHeader",header);
        intent.putExtra("filter",context.filter);
        context.startActivity(intent);
    }
}
