package com.akg.akg_sales.service;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.core.util.Consumer;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.ItemApi;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.dto.item.ItemMaster;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderPermission;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.dto.order.OrderTypeDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.util.SPHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderService {
    public OrderService(){}

    public static void fetchItemFromServer(Context context, Map<String,String> filter, Consumer<List<ItemDto>> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(ItemApi.class)
                .getOrderItems(filter)
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


    public static void fetchItemMasterFromServer(Context context,Long customerId, Consumer<ItemMaster> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(ItemApi.class)
                .getItemMaster(customerId)
                .enqueue(new Callback<ItemMaster>() {
                    @Override
                    public void onResponse(Call<ItemMaster> call, Response<ItemMaster> response) {
                        progressDialog.dismiss();
                        try {
                            if(response.isSuccessful())
                                callback.accept(response.body());
                            else throw new Exception(response.code()+" "+response.message());
                        }catch (Exception e){
                            CommonUtil.showToast(context,e.getMessage(),false);
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<ItemMaster> call, Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
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

    public static void fetchOrderPermission(Context context,Consumer<OrderPermission> callback){
        if(SPHelper.shouldBlockApiCall(context, SPHelper.KEY_NEXT_ORDER_PERMISSION_FETCH_TIMESTAMP)) {
            CommonUtil.orderPermission = SPHelper.getDataFromSharedPref(context, SPHelper.MASTER_DATA_PREF,
                    SPHelper.KEY_ORDER_PERMISSION, OrderPermission.class);
            if(CommonUtil.orderPermission!=null) {
                callback.accept(CommonUtil.orderPermission);
                return;
            }
        }

        API.getClient().create(OrderApi.class).getOrderPermission()
                .enqueue(API.getCallback(null,permission->{
                    CommonUtil.orderPermission = permission;
                    SPHelper.setNextApiCallTimestamp(context, SPHelper.KEY_NEXT_ORDER_PERMISSION_FETCH_TIMESTAMP);
                    SPHelper.storeDataInSharedPref(context, SPHelper.MASTER_DATA_PREF, SPHelper.KEY_ORDER_PERMISSION,CommonUtil.orderPermission);
                    callback.accept(permission);
                },e->{},null));
    }

    public static void fetchOrderTypes(Context context,Consumer<List<OrderTypeDto>> callback){
        if(SPHelper.shouldBlockApiCall(context, SPHelper.KEY_NEXT_ORDER_TYPE_FETCH_TIMESTAMP)) {
            Type type = new TypeToken<ArrayList<OrderTypeDto>>() {}.getType();
            ArrayList<OrderTypeDto> orderTypes = SPHelper.getDataFromSharedPref(context, SPHelper.MASTER_DATA_PREF,
                    SPHelper.KEY_ORDER_TYPE_LIST, type);
            if(orderTypes!=null) {
                callback.accept(orderTypes);
                return;
            }
        }

        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(OrderApi.class).getOrderTypes()
                .enqueue(API.getCallback(context,types->{
                    SPHelper.setNextApiCallTimestamp(context, SPHelper.KEY_NEXT_ORDER_TYPE_FETCH_TIMESTAMP);
                    SPHelper.storeDataInSharedPref(context, SPHelper.MASTER_DATA_PREF, SPHelper.KEY_ORDER_TYPE_LIST,types);
                    callback.accept(types);},e->{},progressDialog));
    }

    public static void sendOrderAttachment(Context context, MultipartBody body,Consumer<OrderDto> callback){
        API.getClient().create(OrderApi.class).sendOrderAttachment(body)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
                        try {
                            if (response.code() == 200) {
                                callback.accept(response.body());
                            }else throw new Exception(response.code()+" "+response.message());
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.accept(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderDto> call, Throwable t) {
                        call.cancel();
                        t.printStackTrace();
                        callback.accept(null);
                    }
                });
    }

}
