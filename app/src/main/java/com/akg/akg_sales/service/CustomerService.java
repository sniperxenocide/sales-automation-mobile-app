package com.akg.akg_sales.service;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.core.util.Consumer;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.CustomerApi;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.util.CommonUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerService {

    public static void fetchCustomerListForUser(Context context, Consumer<List<CustomerDto>> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(CustomerApi.class).getCustomersForUser()
                .enqueue(new Callback<List<CustomerDto>>() {
                    @Override
                    public void onResponse(Call<List<CustomerDto>> call, Response<List<CustomerDto>> response) {
                        progressDialog.dismiss();
                        try {
                            if(response.code()==200){
                                callback.accept(response.body());
                            }
                        }catch (Exception e){e.printStackTrace();}
                    }

                    @Override
                    public void onFailure(Call<List<CustomerDto>> call, Throwable t) {
                        progressDialog.dismiss();
                        call.cancel();
                        t.printStackTrace();
                    }
                });
    }
}
