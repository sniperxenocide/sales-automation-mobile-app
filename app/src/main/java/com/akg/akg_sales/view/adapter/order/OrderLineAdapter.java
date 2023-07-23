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
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.OrderDetailActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.view.dialog.ItemQuantityDialog;

import java.util.ArrayList;
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
        //holder.itemBinding.quantity.setOnClickListener(view -> onQuantityUpdate(line));
        //holder.itemBinding.deleteLineItem.setOnClickListener(view -> onClickDeleteItem(line));

//        if(activity.orderActionPermitted){
//            holder.itemBinding.deleteLineItem.setVisibility(View.VISIBLE);
//        }
//        else holder.itemBinding.deleteLineItem.setVisibility(View.GONE);
        holder.itemBinding.deleteLineItem.setVisibility(View.GONE);
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
        if(!activity.orderActionPermitted) return;
        if(lines.size()>1){
            new ConfirmationDialog(activity,"Delete Item ?", i->{
                activity.orderDto.getOrderLines().remove(line);
                activity.loadOrderLines();
            });
        }
        else CommonUtil.showToast(activity,"Minimum one item must be present",false);
    }

    private void onQuantityUpdate(OrderLineDto line){
        if(!activity.orderActionPermitted) return;
        new ItemQuantityDialog(activity, line.getItemDescription(), line.getUom(),  line.getQuantity().intValue(),"Update Quantity?",
            qty->{
            if(qty<= line.getQuantity()){
                line.setQuantity(qty.doubleValue());
                Objects.requireNonNull(activity.recyclerView.getAdapter())
                        .notifyItemChanged(lines.indexOf(line));
            }
            else CommonUtil.showToast(activity,"Can't Increase Quantity",false);
        });
    }
}
