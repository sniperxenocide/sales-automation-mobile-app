package com.akg.akg_sales.viewmodel.order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.LoginActivity;
import com.akg.akg_sales.view.activity.order.CartActivity;
import com.akg.akg_sales.view.activity.order.PendingOrderActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.PartMap;

public class CartViewModel extends BaseObservable {
    CartActivity activity;
    public CartViewModel(CartActivity activity){
        this.activity=activity;
    }

    public void onClickSubmitBtn(){
        new ConfirmationDialog(activity,
                "Order Confirmation.\n\nAttention! Actual Value of this Order will be Confirmed after Order Book.",
                a->{
            OrderRequest postBody = getPostBody();
            if(postBody==null){
                CommonUtil.showToast(activity,"No Item Selected",false);
                return;
            }
            ProgressDialog progressDialog = CommonUtil.showProgressDialog(activity);
            API.getClient().create(OrderApi.class).createOrder(postBody)
                    .enqueue(new Callback<OrderDto>() {
                        @Override
                        public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
                            progressDialog.dismiss();
                            if(response.code()==200){
                                OrderDto orderDto = response.body();
                                CommonUtil.showToast(activity,"Order Created Successfully",true);
                                removeCartItems();

                                //Sending Attachment
                                sendOrderAttachment(orderDto.getId().toString());

                                // Returning to Order List Page
                                Intent intent = new Intent(activity, PendingOrderActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                            else {
                                CommonUtil.showToast(activity,response.code()+"."+response.message(),false);
                            }

                        }

                        @Override
                        public void onFailure(Call<OrderDto> call, Throwable t) {
                            progressDialog.dismiss();
                            call.cancel();
                            CommonUtil.showToast(activity,"Order Creation Failed."+t.getMessage(),false);
                        }
                    });
        });
    }

    private void removeCartItems(){
        activity.cartMap.remove(activity.cSelectedCustomer.getId());
    }

    private OrderRequest getPostBody(){
        OrderRequest postBody = new OrderRequest();
        ArrayList<CartItemDto> items = activity.cartMap.get(activity.cSelectedCustomer.getId());
        if(items==null){
            CommonUtil.showToast(activity,"Item List Empty",false);
            return null;
        }
        postBody.setCustomerId(activity.cSelectedCustomer.getId());
        if(activity.cSelectedSite!=null){
            postBody.setSiteId(activity.cSelectedSite.getId());
        }
        for(CartItemDto i: items){
            postBody.addLine(i.getItemDto().getId(),i.getQuantity());
        }
        postBody.setNote(getNote());
        try {
            postBody.setDeviceInfo(CommonUtil.getDeviceInfoJson().toString());
        }catch (Exception e){e.printStackTrace();}
        postBody.setOrderTypeId(activity.selectedOrderType.getId());

        return postBody;
    }


    private String getNote(){
        try {
            JSONObject noteObject = new JSONObject();
            String note = Objects.requireNonNull(activity.cartBinding.noteField.getText()).toString();
            if(note.trim().length()==0) return null;
            noteObject.put(activity.cSelectedCustomer.getCustomerName()+" ("
                            +activity.cSelectedCustomer.getOracleCustomerCode()+")",note);
            return noteObject.toString();
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    private void sendOrderAttachment(String orderId){
        try {
            File attachment = new File(Objects.requireNonNull(activity.attachmentPath));
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), attachment);
            builder.addFormDataPart("attachment",attachment.getName(), fileRequestBody);
            builder.addFormDataPart("orderId",orderId);
            MultipartBody body = builder.build();

            OrderService.sendOrderAttachment(activity,body,h->{
                CommonUtil.showToast(activity,"Order Attachment Posted",true);
            });
        }
        catch (Exception e){e.printStackTrace();}
    }

}
