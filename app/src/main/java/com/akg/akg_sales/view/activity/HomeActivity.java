package com.akg.akg_sales.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.CustomerApi;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.databinding.ActivityHomeBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.viewmodel.HomeViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchCustomerListForUser();
        fetchOrderStatusFromServer();
        ActivityHomeBinding homeBinding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        homeBinding.setVm(new HomeViewModel(this));
        homeBinding.executePendingBindings();
        loadCart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("App is Closing.*****************************");
        storeCart();
    }

    private void loadCart(){
        try {
            SharedPreferences sp = getSharedPreferences("cart", Context.MODE_PRIVATE);
            Type type = new TypeToken<HashMap<Long, ArrayList<CartItemDto>>>(){}.getType();
            CommonUtil.orderCart = gson.fromJson(sp.getString("cart",""), type);
            if(CommonUtil.orderCart==null) CommonUtil.orderCart = new HashMap<>();
            System.out.println("Cart Loaded... "+CommonUtil.orderCart);
        }catch (Exception e){System.out.println(e.getMessage());}
    }

    private void storeCart(){
        try {
            String cartStr = gson.toJson(CommonUtil.orderCart);
            System.out.println("Storing Cart: "+cartStr);
            SharedPreferences sp = getSharedPreferences("cart", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("cart",cartStr);
            editor.apply();
        }catch (Exception e){System.out.println(e.getMessage());}
    }

    private void fetchCustomerListForUser(){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(this);
        API.getClient().create(CustomerApi.class).getCustomersForUser()
                .enqueue(new Callback<List<CustomerDto>>() {
                    @Override
                    public void onResponse(Call<List<CustomerDto>> call, Response<List<CustomerDto>> response) {
                        progressDialog.dismiss();
                        if(response.code()==200){
                            CommonUtil.customers = response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CustomerDto>> call, Throwable t) {
                        call.cancel();
                    }
                });
    }

    private void fetchOrderStatusFromServer(){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(this);
        API.getClient().create(OrderApi.class).getOrderStatus()
                .enqueue(new Callback<List<OrderStatusDto>>() {
                    @Override
                    public void onResponse(Call<List<OrderStatusDto>> call, Response<List<OrderStatusDto>> response) {
                        progressDialog.dismiss();
                        if(response.code()==200){
                            CommonUtil.statusList = (ArrayList<OrderStatusDto>) response.body();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<OrderStatusDto>> call, Throwable t) {
                        progressDialog.dismiss();
                        call.cancel();
                    }
                });
    }
}