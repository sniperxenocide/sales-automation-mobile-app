package com.akg.akg_sales.viewmodel.order;

import android.app.ProgressDialog;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.CartActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                                System.out.println(orderDto);
                                CommonUtil.showToast(activity,"Order Created Successfully",true);
                                removeCartItems();
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

}
