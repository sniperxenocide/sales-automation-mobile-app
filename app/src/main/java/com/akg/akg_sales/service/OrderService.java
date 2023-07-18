package com.akg.akg_sales.service;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.core.util.Consumer;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.ItemApi;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.dto.item.ItemTypeDto;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderService {
    public OrderService(){}

    public static void fetchItemFromServer(Context context,Long subTypeId, Consumer<List<ItemDto>> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(ItemApi.class)
                .getOrderItems(CommonUtil.selectedCustomer.getId(),subTypeId)
                .enqueue(new Callback<List<ItemDto>>() {
                    @Override
                    public void onResponse(Call<List<ItemDto>> call, Response<List<ItemDto>> response) {
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
                    public void onFailure(Call<List<ItemDto>> call, Throwable t) {
                        progressDialog.dismiss();
                        call.cancel();
                    }
                });
    }


    public static void fetchItemTypeSubTypeFromServer(Context context,Long customerId, Consumer<List<ItemTypeDto>> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(ItemApi.class)
                .getItemTypes(customerId)
                .enqueue(new Callback<List<ItemTypeDto>>() {
                    @Override
                    public void onResponse(Call<List<ItemTypeDto>> call, Response<List<ItemTypeDto>> response) {
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
                    public void onFailure(Call<List<ItemTypeDto>> call, Throwable t) {
                        progressDialog.dismiss();
                        call.cancel();
                    }
                });
    }


    public static void fetchOrderDetailFromServer(String orderId,Context context,Consumer<OrderDto> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(OrderApi.class).getOrderDetail(orderId)
                .enqueue(new Callback<OrderDto>() {
                    @Override
                    public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
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
                    public void onFailure(Call<OrderDto> call, Throwable t) {
                        progressDialog.dismiss();
                        call.cancel();
                    }
                });
    }

    public static void approveOrder(OrderRequest body,Context context,Consumer<OrderDto> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(OrderApi.class).approveOrder(body)
                .enqueue(new Callback<OrderDto>() {
                    @Override
                    public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
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
                    public void onFailure(Call<OrderDto> call, Throwable t) {
                        progressDialog.dismiss();
                        call.cancel();
                        CommonUtil.showToast(context,t.getMessage(),false);
                    }
                });
    }


    public static void cancelOrder(String orderId,Context context,Consumer<OrderDto> callback) {
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(OrderApi.class).cancelOrder(orderId)
                .enqueue(new Callback<OrderDto>() {
                    @Override
                    public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
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
                    public void onFailure(Call<OrderDto> call, Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
                        call.cancel();
                        CommonUtil.showToast(context,t.getLocalizedMessage(),false);
                    }
                });
    }

    public static void fetchOrderStatusFromServer(Context context,Consumer<List<OrderStatusDto>> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(OrderApi.class).getOrderStatus()
                .enqueue(new Callback<List<OrderStatusDto>>() {
                    @Override
                    public void onResponse(Call<List<OrderStatusDto>> call, Response<List<OrderStatusDto>> response) {
                        progressDialog.dismiss();
                        try {
                            if(response.code()==200){
                                callback.accept(response.body());
                            }
                        }catch (Exception e){e.printStackTrace();}
                    }
                    @Override
                    public void onFailure(Call<List<OrderStatusDto>> call, Throwable t) {
                        progressDialog.dismiss();
                        call.cancel();
                        t.printStackTrace();
                    }
                });
    }

}
