package com.akg.akg_sales.service;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.core.util.Consumer;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.PaymentApi;
import com.akg.akg_sales.dto.payment.PaymentAccountDto;
import com.akg.akg_sales.util.CommonUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentService {

    public static void getPaymentAccounts(Context context, String operatingUnitId,
                                          Consumer<List<PaymentAccountDto>> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(PaymentApi.class)
                .getPaymentAccounts(operatingUnitId).enqueue(new Callback<List<PaymentAccountDto>>() {
            @Override
            public void onResponse(Call<List<PaymentAccountDto>> call, Response<List<PaymentAccountDto>> response) {
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
            public void onFailure(Call<List<PaymentAccountDto>> call, Throwable t) {
                progressDialog.dismiss();
                call.cancel();
            }
        });
    }
}
