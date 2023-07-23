package com.akg.akg_sales.view.adapter.payment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemPaymentBinding;
import com.akg.akg_sales.dto.payment.PaymentDto;
import com.akg.akg_sales.view.activity.payment.PaymentListActivity;
import com.akg.akg_sales.view.dialog.PaymentDetailDialog;

import java.util.ArrayList;

public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.ViewHolder>{
    private ArrayList<PaymentDto> payments;
    private final PaymentListActivity activity;

    public PaymentListAdapter(PaymentListActivity activity,ArrayList<PaymentDto> objects){
        this.payments = objects;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PaymentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemPaymentBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_payment,
                parent,false);
        return new PaymentListAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentListAdapter.ViewHolder holder, int position) {
        PaymentDto p = payments.get(position);
        holder.bind(p,Integer.toString(position+1));
        holder.itemBinding.cardItem.setOnClickListener(view ->
                new PaymentDetailDialog(activity,p));
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemPaymentBinding itemBinding;
        public ViewHolder(ListitemPaymentBinding itemBinding) {
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
