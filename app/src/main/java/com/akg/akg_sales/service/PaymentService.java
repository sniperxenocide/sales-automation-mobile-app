package com.akg.akg_sales.service;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.core.util.Consumer;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.PaymentApi;
import com.akg.akg_sales.dto.payment.PaymentAccountDto;
import com.akg.akg_sales.dto.payment.PaymentMasterDto;
import com.akg.akg_sales.dto.payment.PaymentRequestDto;
import com.akg.akg_sales.util.CommonUtil;

import java.lang.reflect.Field;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentService {

    public static void getPaymentMaster(Context context, String operatingUnitId,
                                        Consumer<PaymentMasterDto> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(PaymentApi.class)
                .getPaymentMaster(operatingUnitId).enqueue(new Callback<PaymentMasterDto>() {
            @Override
            public void onResponse(Call<PaymentMasterDto> call, Response<PaymentMasterDto> response) {
                progressDialog.dismiss();
                try {
                    if(response.code()==200){
                        callback.accept(response.body());
                    }
                    else throw new Exception(response.code()+"."+response.message());
                }catch (Exception e){
                    CommonUtil.showToast(context,e.getMessage(),false);
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PaymentMasterDto> call, Throwable t) {
                progressDialog.dismiss();
                call.cancel();
                System.out.println(t.getMessage());
            }
        });
    }

    public static void createPayment(Context context, PaymentRequestDto requestDto,
                                     Consumer<String> callback){
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            for(Field f:PaymentRequestDto.class.getDeclaredFields()){
                try {
                    f.setAccessible(true);
                    if(f.getName().equals("attachment")){
                        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), requestDto.getAttachment());
                        builder.addFormDataPart("attachment",requestDto.getAttachment().getName(), fileRequestBody);
                    }
                    else builder.addFormDataPart(f.getName(), String.valueOf(f.get(requestDto)));
                }catch (Exception e){
                    System.out.println("Field Value Error "+f.getName());
                }
            }
            MultipartBody body = builder.build();

            ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
            API.getClient().create(PaymentApi.class).createPayment(body)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            progressDialog.dismiss();
                            try {
                                if(response.code()==200){
                                    callback.accept(response.body());
                                }
                                else throw new Exception(response.code()+"."+response.message());
                            }catch (Exception e){
                                CommonUtil.showToast(context,e.getMessage(),false);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            progressDialog.dismiss();
                            call.cancel();
                            System.out.println(t.getMessage());
                        }
                    });
        }catch (Exception e){e.printStackTrace();}

    }
}
