package com.akg.akg_sales.view.adapter.delivery;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.DialogDeliveryAcknowledgeBinding;
import com.akg.akg_sales.databinding.ListitemDeliveryAckLineBinding;
import com.akg.akg_sales.dto.delivery.DeliveryAcknowledgeLineDto;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;
import com.akg.akg_sales.view.dialog.DeliveryAcknowledgeDialog;

import java.util.ArrayList;

public class DeliveryAckLineAdapter extends RecyclerView.Adapter<DeliveryAckLineAdapter.ViewHolder>{
    private ArrayList<DeliveryAcknowledgeLineDto> lines;
    private final DeliveryDetailActivity context;
    DeliveryAcknowledgeDialog dialogObj;

    public DeliveryAckLineAdapter(DeliveryDetailActivity context, ArrayList<DeliveryAcknowledgeLineDto> objects,DeliveryAcknowledgeDialog dialog){
        this.lines = objects;
        this.context = context;
        this.dialogObj = dialog;
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
        holder.bind(line);
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
        public void bind(Object obj) {
            itemBinding.setVariable(BR.vm, obj);
            handleQuantityChange((DeliveryAcknowledgeLineDto) obj,itemBinding);
            itemBinding.executePendingBindings();
        }
    }

    private void handleQuantityChange(DeliveryAcknowledgeLineDto dto,ListitemDeliveryAckLineBinding itemBinding){
        EditText rcvQty = itemBinding.rcvQty;
        rcvQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    double newQty = Double.parseDouble(rcvQty.getText().toString());
                    if(newQty != dto.getLineQuantity())
                        rcvQty.setBackgroundResource(R.drawable.yellow_background);
                    else rcvQty.setBackgroundResource(R.drawable.deep_green_background);
                    dto.setReceivedQuantity(newQty);
                }catch (Exception e){
                    rcvQty.setBackgroundResource(R.drawable.yellow_background);
                    rcvQty.setText("0");
                    dto.setReceivedQuantity(0d);
                    Log.e("LINE", "afterTextChanged: ",e);
                }
                updateMismatchIcon();
            }
        });
    }

    private void updateMismatchIcon(){
        try {
            for(DeliveryAcknowledgeLineDto l:lines){
                if(l.getIsHeader()) continue;
                if(l.getLineQuantity().doubleValue() != l.getReceivedQuantity()){
                    dialogObj.binding.mismatchIcon.setBackgroundResource(R.drawable.not_equal);
                    return;
                }
            }
            dialogObj.binding.mismatchIcon.setBackgroundResource(R.drawable.equal);
        }catch (Exception e){}
    }
}
