package com.akg.akg_sales.view.adapter.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemOrderLineBinding;
import com.akg.akg_sales.dto.order.OrderLineDto;
import com.akg.akg_sales.dto.order.OrderLineRequest;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.OrderDetailActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.view.dialog.ItemQuantityDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderLineAdapter extends RecyclerView.Adapter<OrderLineAdapter.ViewHolder>{
    private ArrayList<OrderLineDto> lines;
    private final OrderDetailActivity activity;

    public OrderLineAdapter(OrderDetailActivity activity,ArrayList<OrderLineDto> objects){
        this.lines = objects;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OrderLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemOrderLineBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_order_line,
                parent,false);
        return new OrderLineAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderLineAdapter.ViewHolder holder, int position) {
        OrderLineDto line = lines.get(position);
        holder.bind(line,Integer.toString(position+1));

        if(CommonUtil.orderPermission.getCanEditOrder() && activity.canApproveOrder){
            holder.itemBinding.deleteLineItem.setVisibility(View.VISIBLE);
            holder.itemBinding.quantity.setOnClickListener(view -> onQuantityUpdate(line));
            holder.itemBinding.deleteLineItem.setOnClickListener(view -> onClickDeleteItem(line));
        }
        else holder.itemBinding.deleteLineItem.setVisibility(View.GONE);
    }



    @Override
    public int getItemCount() {
        return lines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemOrderLineBinding itemBinding;
        public ViewHolder(ListitemOrderLineBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj,String idx) {
            itemBinding.setVariable(BR.vm, obj);
            itemBinding.setVariable(BR.index, idx);
            itemBinding.executePendingBindings();
        }
    }

    private void onClickDeleteItem(OrderLineDto line){
        if(lines.size()>1){
            new ConfirmationDialog(activity,"Delete Item ?", i->{
                activity.orderDto.getOrderLines().remove(line);
                activity.loadOrderLines();
                recalculateGrossValue();
            });
        }
        else CommonUtil.showToast(activity,"Minimum one item must be present",false);
    }

    private void onQuantityUpdate(OrderLineDto line){
        new ItemQuantityDialog(activity, line.getItemDescription(), line.getUom(),  line.getQuantity(),"Update Quantity?",
            qty->{
                line.setQuantity(qty);
                Objects.requireNonNull(activity.recyclerView.getAdapter())
                        .notifyItemChanged(lines.indexOf(line));
                recalculateGrossValue();
        });
    }

    private void recalculateGrossValue(){
        try {
            double grossValue = 0;
            for (OrderLineDto l:activity.orderDto.getOrderLines()){
                if(l.getUnitPrice()==null) continue;
                grossValue = grossValue+l.getQuantity()*l.getUnitPrice();
            }
            activity.orderDto.setValue(grossValue);
            activity.binding.grossValue.setText(activity.orderDto.getValue());
        }catch (Exception e){
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

    }
}
