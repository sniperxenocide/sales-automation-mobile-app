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
import com.akg.akg_sales.databinding.ListitemPaymentClearedBinding;
import com.akg.akg_sales.dto.notification.PaymentDto;
import com.akg.akg_sales.view.activity.notification.PaymentClearedActivity;

import java.util.ArrayList;

public class PaymentClearedAdapter extends RecyclerView.Adapter<PaymentClearedAdapter.ViewHolder>{

    private ArrayList<PaymentDto> list;
    private final Context context;

    public PaymentClearedAdapter(Context context,ArrayList<PaymentDto> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemPaymentClearedBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_payment_cleared,
                parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentDto dto = list.get(position);
        holder.bind(dto);
        holder.binding.cardItem.setOnClickListener((v)->openPaymentDetailPage(dto));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemPaymentClearedBinding binding;
        public ViewHolder(ListitemPaymentClearedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Object obj) {
            binding.setVariable(BR.vm, obj);
            binding.executePendingBindings();
        }
    }

    private void openPaymentDetailPage(PaymentDto dto){
        Intent intent = new Intent(context, PaymentClearedActivity.class);
        intent.putExtra("payment",dto);
        context.startActivity(intent);
    }
}
