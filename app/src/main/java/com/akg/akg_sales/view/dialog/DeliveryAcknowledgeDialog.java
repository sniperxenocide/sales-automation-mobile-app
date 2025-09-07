package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.databinding.DialogDeliveryAcknowledgeBinding;
import com.akg.akg_sales.dto.delivery.DeliveryAckRequestHeader;
import com.akg.akg_sales.dto.delivery.DeliveryAckRequestLine;
import com.akg.akg_sales.dto.delivery.DeliveryAcknowledgeLineDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.service.DeliveryService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;
import com.akg.akg_sales.view.adapter.delivery.DeliveryAckLineAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;

public class DeliveryAcknowledgeDialog {
    public DialogDeliveryAcknowledgeBinding binding;
    Dialog dialog;
    public DeliveryDetailActivity activity;
    public DeliveryAckRequestHeader deliveryAckRequestHeader;
    public String customer;
    private String receivingDateTimeStr;
    ArrayList<DeliveryAcknowledgeLineDto> acknowledgeLines = new ArrayList<>();
    public boolean editable;

    public DeliveryAcknowledgeDialog(DeliveryDetailActivity activity,String customer,DeliveryAckRequestHeader ackRequestHeader){
        this.activity = activity;
        this.customer = customer;
        this.deliveryAckRequestHeader = ackRequestHeader;
        this.editable = ackRequestHeader==null;
        System.out.println("DeliveryAckRequestHeader: "+deliveryAckRequestHeader);
        System.out.println("Editable "+editable);
        if(deliveryAckRequestHeader==null)
            deliveryAckRequestHeader = new DeliveryAckRequestHeader()
                    .setMovOrdHdrId(activity.moveConfirmedHeaderDto.getMoveOrderHeaderId())
                    .setMovOrderNo(activity.moveConfirmedHeaderDto.getMovOrderNo())
                    .setDeviceInfo(CommonUtil.getDeviceInfoJson().toString());

        dialog=new Dialog(activity);
        binding = DialogDeliveryAcknowledgeBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        binding.setEditable(editable);
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
        loadAttachment(deliveryAckRequestHeader.getId());
        acknowledgeLines = new ArrayList<>();
        for (String doNumber:activity.doItemsMap.keySet()) {
            DeliveryAcknowledgeLineDto header = new DeliveryAcknowledgeLineDto(doNumber);
            if(customer.contains(activity.doItemsMap.get(doNumber).get(0).getCustomerNumber()))
                acknowledgeLines.add(header);
            try {
                int serial = 1;
                for(MoveOrderConfirmedLineDto l:activity.doItemsMap.get(doNumber)){
                    if(customer.equals(l.getCustomerName()+" (" + l.getCustomerNumber()+")")){
                        DeliveryAcknowledgeLineDto line =
                                new DeliveryAcknowledgeLineDto(l.getId(),l.getDoNumber(),
                                l.getItemDescription(),l.getLineQuantity(),
                                        l.getReceivedQuantity(),l.getUomCode(),serial);
                        acknowledgeLines.add(line);
                        serial++;

                        deliveryAckRequestHeader.setCustomerNumber(l.getCustomerNumber());
                        deliveryAckRequestHeader.setCustomerName(l.getCustomerName());
                    }
                }
            }catch (Exception e){Log.e("ERROR", "loadData: ", e);}
        }
        populateItemList(acknowledgeLines);
    }

    private void populateItemList(ArrayList<DeliveryAcknowledgeLineDto> list){
        RecyclerView recyclerView = binding.deliveryAckLinesList;
        recyclerView.setItemViewCacheSize(list.size());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        DeliveryAckLineAdapter adapter = new DeliveryAckLineAdapter(activity,list,this,editable);
        adapter.updateMismatchIcon();
        adapter.updateMismatchSummary();
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
                String hr = timePicker.getHour()>=10?String.valueOf(timePicker.getHour()):"0"+timePicker.getHour();
                String min = timePicker.getMinute()>=10?String.valueOf(timePicker.getMinute()):"0"+timePicker.getMinute();
                receivingDateTimeStr = receivingDateTimeStr+" "+hr +":"+min;

                deliveryAckRequestHeader.setReceivingTime(receivingDateTimeStr);

                sdf.applyPattern("yyyy-MM-dd HH:mm");
                Date receivingDate = sdf.parse(receivingDateTimeStr);
                sdf.applyPattern("dd-MMMM-yyyy hh:mm a");
                binding.receivingTime.setText(sdf.format(receivingDate));
            }catch (Exception e){Log.e("T", "onClickReceivingTime: ",e );}
        });

        datePicker.show(activity.getSupportFragmentManager(),"DATE_PICKER");

    }

    public void onClickAttachment(){
        ImagePicker.with(activity).crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(500)			//Final image size will be less than 500 KB(Optional)
                .maxResultSize(800, 600)	//Final image resolution will be less than 800 x 600(Optional)
                .start();
    }

    public void onAttachmentSelected(String path){
        try {
            String[] paths = path.split("/");
            binding.attachment.setText(paths[paths.length-1]);
            try {deliveryAckRequestHeader.setAttachment(new File(path));
            }catch (Exception e){Log.d("Error", "submitAcknowledgement: ",e);}
        } catch (Exception e) {
            Log.d("Error", "onAttachmentSelected: ",e);
        }
    }

    private void loadAttachment(Long id){
        String url = API.baseUrl + "/notification-service/api/move-order/acknowledge-attachment?id="+id;
        System.out.println("Loading Attachment ********");
        ImageView attachmentView = binding.attachmentView;
        GlideUrl glideUrl = new GlideUrl(url,
                new LazyHeaders.Builder()
                        .addHeader("Authorization","Bearer "+CommonUtil.loggedInUser.getToken())
                        .build());

        Glide.with(activity)
                .load(glideUrl)
                .into(attachmentView);

    }

    public void submitAcknowledgement(){
        try {
//            if(deliveryAckRequestHeader.getReceivingTime()==null || deliveryAckRequestHeader.getReceivingTime().isBlank())
//            {
//                throw new Exception("Receiving Time Can't be empty.");
//            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            deliveryAckRequestHeader.setReceivingTime(sdf.format(new Date()));

            if(binding.commentField.getText()!=null)
                deliveryAckRequestHeader.setComment(binding.commentField.getText().toString());

            List<DeliveryAckRequestLine> reqLines = new ArrayList<>();
            for(DeliveryAcknowledgeLineDto l:acknowledgeLines){
                if(l.getIsHeader()) continue;
                if(l.getLineQuantity().doubleValue()!=l.getReceivedQuantity())
                    reqLines.add(new DeliveryAckRequestLine(l.getId(),l.getReceivedQuantity()));
            }
            deliveryAckRequestHeader.setLines(reqLines);
            System.out.println(deliveryAckRequestHeader);
            MultipartBody body  = DeliveryService.generateMultipartBody(deliveryAckRequestHeader);
            System.out.println("Delivery Ack Body: "+body);
            DeliveryService.submitAcknowledgement(activity,body,(res)->{
                System.out.println(res);
                CommonUtil.showToast(activity,"Acknowledgement Submitted Successfully",true);
                activity.updateUIWithAcknowledgementStatus(res);
                dialog.dismiss();
            });
        }catch (Exception e){
            Log.d("Error", "submitAcknowledgement: "+e);
            CommonUtil.showToast(activity,e.getMessage(),false);
        }
    }
}
