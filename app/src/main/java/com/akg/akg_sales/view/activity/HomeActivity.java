package com.akg.akg_sales.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.akg.akg_sales.BuildConfig;
import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.CustomerApi;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.databinding.ActivityHomeBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.service.CustomerService;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.viewmodel.HomeViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    Gson gson = new Gson();
    ActivityHomeBinding homeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchCustomerListForUser();
        fetchOrderStatusFromServer();
        homeBinding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        homeBinding.setVm(new HomeViewModel(this));
        homeBinding.executePendingBindings();
        setAppVersion();
        //loadCart();
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
            updateCartForCurrentUser();
        }catch (Exception e){System.out.println(e.getMessage());}
    }

    private void updateCartForCurrentUser(){
        Set<Long> customerIds = CommonUtil.orderCart.keySet();
        for(Long id:customerIds){
            boolean idFound = false;
            for(CustomerDto c:CommonUtil.customers){
                if(c.getId().longValue()==id) {
                    idFound=true; break;
                }
            }
            if(!idFound) CommonUtil.orderCart.remove(id);
        }
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
        CustomerService.fetchCustomerListForUser(this,customers->{
            CommonUtil.customers = customers;
            loadCart();
        });
    }

    private void fetchOrderStatusFromServer(){
        OrderService.fetchOrderStatusFromServer(this,orderStatus ->
                CommonUtil.statusList = (ArrayList<OrderStatusDto>)orderStatus);
    }

    private void setAppVersion(){
        try {
            homeBinding.appVersion.setText("v"+BuildConfig.VERSION_NAME);
        }catch (Exception e){e.printStackTrace();}
    }
}