package com.akg.akg_sales.service;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.core.util.Consumer;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.ReportApi;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.report.ReportDto;
import com.akg.akg_sales.util.CommonUtil;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportService {

    public static void fetchReportList(Context context, Map<String,String> filter, Consumer<PageResponse<ReportDto>> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(ReportApi.class).getReportList(filter).enqueue(new Callback<PageResponse<ReportDto>>() {
            @Override
            public void onResponse(Call<PageResponse<ReportDto>> call, Response<PageResponse<ReportDto>> response) {
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
            public void onFailure(Call<PageResponse<ReportDto>> call, Throwable t) {
                progressDialog.dismiss();
                call.cancel();
            }
        });
    }
}
