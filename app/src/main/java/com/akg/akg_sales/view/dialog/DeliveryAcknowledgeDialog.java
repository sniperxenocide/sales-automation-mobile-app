package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.databinding.DialogDeliveryAcknowledgeBinding;
import com.akg.akg_sales.databinding.DialogDeliveryFilterBinding;
import com.akg.akg_sales.dto.delivery.DeliveryAcknowledgeLineDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;
import com.akg.akg_sales.view.adapter.delivery.DeliveryAckLineAdapter;

import java.util.ArrayList;

public class DeliveryAcknowledgeDialog {
    DialogDeliveryAcknowledgeBinding binding;
    Dialog dialog;
    public DeliveryDetailActivity activity;
    public String customer;

    public DeliveryAcknowledgeDialog(DeliveryDetailActivity activity,String customer){
        this.activity = activity;
        this.customer = customer;
        dialog=new Dialog(activity);
        binding = DialogDeliveryAcknowledgeBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(this.activity,this.dialog);
        loadData();
        dialog.show();
    }

    private void loadData(){
        ArrayList<DeliveryAcknowledgeLineDto> lines = new ArrayList<>();
        for (String doNumber:activity.doItemsMap.keySet()) {
            DeliveryAcknowledgeLineDto header = new DeliveryAcknowledgeLineDto(doNumber);
            lines.add(header);
            try {
                for(MoveOrderConfirmedLineDto l:activity.doItemsMap.get(doNumber)){
                    DeliveryAcknowledgeLineDto line = new DeliveryAcknowledgeLineDto(
                            l.getItemDescription(),l.getLineQuantity(),l.getUomCode());
                    lines.add(line);
                }
            }catch (Exception e){e.printStackTrace();}
        }

        populateItemList(lines);
    }

    private void populateItemList(ArrayList<DeliveryAcknowledgeLineDto> list){
        RecyclerView recyclerView = binding.deliveryAckLinesList;
        recyclerView.setItemViewCacheSize(list.size());
        recyclerView.setHasFixedSize(true);
        DeliveryAckLineAdapter adapter = new DeliveryAckLineAdapter(activity,list);
        recyclerView.setAdapter(adapter);
    }
}
