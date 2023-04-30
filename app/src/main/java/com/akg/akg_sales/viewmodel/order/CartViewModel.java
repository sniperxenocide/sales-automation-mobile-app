package com.akg.akg_sales.viewmodel.order;

import android.app.ProgressDialog;

import androidx.databinding.BaseObservable;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.CartActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;

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
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(activity);
        new ConfirmationDialog(activity, "Submit Order?", a->{
            OrderRequest postBody = getPostBody();
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
        ArrayList<CartItemDto> removable = new ArrayList<>();
        for (int i=0;i<CommonUtil.cartItems.size();i++){
            CartItemDto item = CommonUtil.cartItems.get(i);
            if(Objects.equals(item.getCustomerDto().getId(), activity.selectedCustomerId)){
                removable.add(item);
            }
        }
        CommonUtil.cartItems.removeAll(removable);
    }

    private OrderRequest getPostBody(){
        OrderRequest postBody = new OrderRequest();
        postBody.setCustomerId(activity.selectedCustomerId);
        for(CartItemDto i: CommonUtil.cartItems){
            if(Objects.equals(i.getCustomerDto().getId(), activity.selectedCustomerId)){
                postBody.addLine(i.getItemDto().getId(),i.getQuantity());
            }
        }
        return postBody;
    }

}
