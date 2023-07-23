package com.akg.akg_sales.view.adapter.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemOrderBookedLineBinding;
import com.akg.akg_sales.dto.notification.OrderBookedLineDto;

import java.util.ArrayList;

public class OrderBookedLineAdapter extends RecyclerView.Adapter<OrderBookedLineAdapter.ViewHolder> {
    private ArrayList<OrderBookedLineDto> lines;
    private final Context context;

    public OrderBookedLineAdapter(Context context, ArrayList<OrderBookedLineDto> objects){
        this.lines = objects;
        this.context = context;
    }


    @NonNull
    @Override
    public OrderBookedLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemOrderBookedLineBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_order_booked_line,
                parent,false);
        return new OrderBookedLineAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderBookedLineAdapter.ViewHolder holder, int position) {
        OrderBookedLineDto line = lines.get(position);
        holder.bind(line,Integer.toString(position+1));
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemOrderBookedLineBinding itemBinding;
        public ViewHolder(ListitemOrderBookedLineBinding itemBinding) {
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
