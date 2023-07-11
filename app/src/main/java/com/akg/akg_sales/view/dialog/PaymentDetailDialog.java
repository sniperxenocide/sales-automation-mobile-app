package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.databinding.DialogPaymentDetailBinding;
import com.akg.akg_sales.dto.StatusFlow;
import com.akg.akg_sales.dto.payment.PaymentDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.OrderDetailActivity;
import com.akg.akg_sales.view.activity.payment.PaymentListActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.ArrayList;

public class PaymentDetailDialog {
    DialogPaymentDetailBinding binding;
    PaymentListActivity activity;
    public PaymentDto paymentDto;
    public Dialog dialog;

    public PaymentDetailDialog(PaymentListActivity activity,PaymentDto dto){
        this.paymentDto = dto;
        this.activity = activity;
        dialog=new Dialog(activity);
        binding = DialogPaymentDetailBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        dialog.setContentView(binding.getRoot());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(0.95*displayMetrics.widthPixels);
        int height = (int)(0.9*displayMetrics.heightPixels);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        loadAttachment();
        loadStatusFlowDialog();
    }

    public Spanned getPaymentInfo(){
        return Html.fromHtml(
                "<b>Amount: </b>"+paymentDto.getPaymentAmount()+"<br>"
                        +"<b>Method: </b>"+paymentDto.getPaymentType()+"<br>"
                        +"<b>Date: </b>"+paymentDto.getPaymentDate()
        );
    }

    public Spanned getBeneficiaryInfo(){
        return Html.fromHtml(
                "<b>Bank: </b>"+paymentDto.getBankName()+"<br>"
                        +"<b>Branch: </b>"+paymentDto.getBranchName()+"<br>"
                        +"<b>AC: </b>"+paymentDto.getBankAccountNumber()+"<br>"
                        +"<b>Name: </b>"+paymentDto.getAccountHolderName()
        );
    }

    public Spanned getCustomerInfo(){
        return Html.fromHtml(
                "<b>Customer: </b>"+paymentDto.getCustomerName()+"<br>"
                        +"<b>Bank: </b>"+paymentDto.getCustomerBankName()+"<br>"
                        +"<b>Branch: </b>"+paymentDto.getCustomerBankBranch()
        );
    }

    private void loadAttachment(){
        String url = API.baseUrl + "/payment-service/api/payment/attachment?id="+paymentDto.getId();
        ImageView attachmentView = binding.attachment;
        GlideUrl glideUrl = new GlideUrl(url,
                new LazyHeaders.Builder()
                        .addHeader("Authorization","Bearer "+CommonUtil.loggedInUser.getToken())
                        .build());

        Glide.with(activity)
                .load(glideUrl)
                .into(attachmentView);

    }

    private void loadStatusFlowDialog(){
        try {
            ArrayList<StatusFlow> statusFlows = new ArrayList<>();
            statusFlows.add(new StatusFlow(1,false,"Submitted"));
            statusFlows.add(new StatusFlow(2,false,"Confirmed"));
            statusFlows.add(new StatusFlow(3,false,"Remitted"));
            statusFlows.add(new StatusFlow(4,false,"Cleared"));
            for (StatusFlow s:statusFlows){
                s.setPassed(true);
                if(s.getStatus().equals(paymentDto.getCurrentStatus())) break;
            }
            binding.statusLayout.setOnClickListener(v->{
                new StatusFlowDialog(statusFlows, activity);
            });
        }catch (Exception e){e.printStackTrace();}
    }
}
