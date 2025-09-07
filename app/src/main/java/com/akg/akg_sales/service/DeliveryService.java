package com.akg.akg_sales.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.core.util.Consumer;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.DeliveryApi;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.delivery.DeliveryAckRequestHeader;
import com.akg.akg_sales.dto.delivery.DeliveryPermission;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedHeaderDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.dto.order.OrderPermission;
import com.akg.akg_sales.dto.payment.PaymentRequestDto;
import com.akg.akg_sales.util.CommonUtil;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public static void submitAcknowledgement(Context context, MultipartBody body,
                                                  Consumer<DeliveryAckRequestHeader> callback){
        try {
            ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
            API.getClient().create(DeliveryApi.class).acknowledgeDelivery(body)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(Call<DeliveryAckRequestHeader> call, Response<DeliveryAckRequestHeader> response) {
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
                        public void onFailure(Call<DeliveryAckRequestHeader> call, Throwable t) {
                            progressDialog.dismiss();
                            call.cancel();
                            t.printStackTrace();
                        }
                    });
        }
        catch (Exception e){e.printStackTrace();}
    }

    public static MultipartBody generateMultipartBody(DeliveryAckRequestHeader requestDto){
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for(Field f: DeliveryAckRequestHeader.class.getDeclaredFields()){
                try {
                    f.setAccessible(true);
                    if(f.get(requestDto)==null) continue;
                    if(f.getName().equals("attachment")){
                        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), requestDto.getAttachment());
                        builder.addFormDataPart("attachment",requestDto.getAttachment().getName(), fileRequestBody);
                    }
                    if(f.getName().equals("lines")){
                        Gson gson = new Gson();
                        builder.addFormDataPart("lines", gson.toJson(requestDto.getLines()));
                    }
                    else builder.addFormDataPart(f.getName(), String.valueOf(f.get(requestDto)));
                }catch (Exception e){
                    System.out.println("Field Value Error "+f.getName());
                }
            }
            return builder.build();
        }
        catch (Exception e){e.printStackTrace();}
        return null;
    }

    public static void checkAcknowledgementStatus(
            Context context, Long movOrdHrdId, String customerNumber,Consumer<DeliveryAckRequestHeader> callback){
        try {
            ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
            API.getClient().create(DeliveryApi.class).getAcknowledgementStatus(movOrdHrdId, customerNumber)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(Call<DeliveryAckRequestHeader> call, Response<DeliveryAckRequestHeader> response) {
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
                        public void onFailure(Call<DeliveryAckRequestHeader> call, Throwable t) {
                            progressDialog.dismiss();
                            call.cancel();
                            t.printStackTrace();
                        }
                    });
        }catch (Exception e){e.printStackTrace();}
    }

    public static void fetchDeliveryPermission(Consumer<DeliveryPermission> callback){
        API.getClient().create(DeliveryApi.class).getDeliveryPermission()
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<DeliveryPermission> call, Response<DeliveryPermission> response) {
                        try {
                            if (response.code() == 200) {
                                callback.accept(response.body());
                            }
                        } catch (Exception e) {
                            Log.e("Error", "onFailure: ", e);
                        }
                    }

                    @Override
                    public void onFailure(Call<DeliveryPermission> call, Throwable t) {
                        call.cancel();
                        Log.e("Error", "onFailure: ", t);
                    }
                });
    }
}
