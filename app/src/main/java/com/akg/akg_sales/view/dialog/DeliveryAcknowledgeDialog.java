package com.akg.akg_sales.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.databinding.DialogDeliveryAcknowledgeBinding;
import com.akg.akg_sales.databinding.DialogDeliveryFilterBinding;
import com.akg.akg_sales.dto.delivery.DeliveryAcknowledgeLineDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;
import com.akg.akg_sales.view.adapter.delivery.DeliveryAckLineAdapter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DeliveryAcknowledgeDialog {
    public DialogDeliveryAcknowledgeBinding binding;
    Dialog dialog;
    public DeliveryDetailActivity activity;
    public String customer;
    private String customerName;
    private String customerNumber;
    private String receivingDateTimeStr;

    public DeliveryAcknowledgeDialog(DeliveryDetailActivity activity,String customer){
        this.activity = activity;
        this.customer = customer;
        dialog=new Dialog(activity);
        binding = DialogDeliveryAcknowledgeBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        dialog.setContentView(binding.getRoot());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(0.99*displayMetrics.widthPixels);
        int height = (int)(0.99*displayMetrics.heightPixels);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        dialog.show();
        loadData();
    }

    private void loadData(){
        ArrayList<DeliveryAcknowledgeLineDto> lines = new ArrayList<>();
        for (String doNumber:activity.doItemsMap.keySet()) {
            DeliveryAcknowledgeLineDto header = new DeliveryAcknowledgeLineDto(doNumber);
            lines.add(header);
            try {
                int serial = 1;
                for(MoveOrderConfirmedLineDto l:activity.doItemsMap.get(doNumber)){
                    if(customer.equals(l.getCustomerName()+" (" + l.getCustomerNumber()+")")){
                        DeliveryAcknowledgeLineDto line = new DeliveryAcknowledgeLineDto(
                                l.getItemDescription(),l.getLineQuantity(),l.getUomCode(),serial);
                        lines.add(line);
                        serial++;

                        this.customerNumber = l.getCustomerNumber();
                        this.customerName = l.getCustomerName();
                    }
                }
            }catch (Exception e){Log.e("ERROR", "loadData: ", e);}
        }
        populateItemList(lines);
    }

    private void populateItemList(ArrayList<DeliveryAcknowledgeLineDto> list){
        RecyclerView recyclerView = binding.deliveryAckLinesList;
        recyclerView.setItemViewCacheSize(list.size());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        DeliveryAckLineAdapter adapter = new DeliveryAckLineAdapter(activity,list,this);
        recyclerView.setAdapter(adapter);
    }

    public void onClickReceivingTime(){
        SimpleDateFormat sdf = new SimpleDateFormat();
        Calendar calendar = Calendar.getInstance();
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Receiving Date").build();
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("ReceivingTime").build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            sdf.applyPattern("yyyy-MM-dd");
            receivingDateTimeStr = sdf.format(calendar.getTime());
            timePicker.show(activity.getSupportFragmentManager(),"TIME_PICKER");
        });

        timePicker.addOnPositiveButtonClickListener(l->{
            try {
                receivingDateTimeStr = receivingDateTimeStr+" "+timePicker.getHour()
                        +":"+timePicker.getMinute();

                sdf.applyPattern("yyyy-MM-dd HH:mm");
                Date receivingDate = sdf.parse(receivingDateTimeStr);
                sdf.applyPattern("dd-MMMM-yyyy hh:mm a");
                binding.receivingTime.setText(sdf.format(receivingDate));
            }catch (Exception e){Log.e("T", "onClickReceivingTime: ",e );}
        });

        datePicker.show(activity.getSupportFragmentManager(),"DATE_PICKER");

    }
}
