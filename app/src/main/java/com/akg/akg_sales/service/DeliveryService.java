package com.akg.akg_sales.service;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.core.util.Consumer;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.DeliveryApi;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedHeaderDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.util.CommonUtil;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryService {

    public static void fetchDeliveryListFromServer(
            Context context, HashMap<String,String> filter,
            Consumer<PageResponse<MoveOrderConfirmedHeaderDto>> callback){
        try {
            ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
            API.getClient().create(DeliveryApi.class).getMoveOrderConfirmedHeaders(filter)
                    .enqueue(new Callback<PageResponse<MoveOrderConfirmedHeaderDto>>() {
                        @Override
                        public void onResponse(Call<PageResponse<MoveOrderConfirmedHeaderDto>> call, Response<PageResponse<MoveOrderConfirmedHeaderDto>> response) {
                            progressDialog.dismiss();
                            try {
                                if(response.code()==200){
                                    callback.accept(response.body());
                                }
                                else throw new Exception(response.code()+"."+response.message());
                            }catch (Exception e){
                                e.printStackTrace();
                                CommonUtil.showToast(context,e.getMessage(),false);
                            }
                        }

                        @Override
                        public void onFailure(Call<PageResponse<MoveOrderConfirmedHeaderDto>> call, Throwable t) {
                            progressDialog.dismiss();
                            call.cancel();
                            t.printStackTrace();
                        }
                    });
        }catch (Exception e){e.printStackTrace();}
    }


    public static void fetchDeliveryDetailLinesFromServer(
            Context context, HashMap<String,String> filter,
            Consumer<List<MoveOrderConfirmedLineDto>> callback){
        try {
            ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
            API.getClient().create(DeliveryApi.class).getMoveOrderConfirmedLines(filter)
                    .enqueue(new Callback<List<MoveOrderConfirmedLineDto>>() {
                        @Override
                        public void onResponse(Call<List<MoveOrderConfirmedLineDto>> call, Response<List<MoveOrderConfirmedLineDto>> response) {
                            progressDialog.dismiss();
                            try {
                                if(response.code()==200){
                                    callback.accept(response.body());
                                }
                                else throw new Exception(response.code()+"."+response.message());
                            }catch (Exception e){
                                e.printStackTrace();
                                CommonUtil.showToast(context,e.getMessage(),false);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<MoveOrderConfirmedLineDto>> call, Throwable t) {
                            progressDialog.dismiss();
                            call.cancel();
                            t.printStackTrace();
                        }
                    });
        }
        catch (Exception e){e.printStackTrace();}
    }
}
