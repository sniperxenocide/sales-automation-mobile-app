package com.akg.akg_sales.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.CustomerApi;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.databinding.ActivityHomeBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchCustomerListForUser();
        fetchOrderStatusFromServer();
        ActivityHomeBinding homeBinding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        homeBinding.setVm(new HomeViewModel(this));
        homeBinding.executePendingBindings();
    }

    private void fetchCustomerListForUser(){
        API.getClient().create(CustomerApi.class).getCustomersForUser()
                .enqueue(new Callback<List<CustomerDto>>() {
                    @Override
                    public void onResponse(Call<List<CustomerDto>> call, Response<List<CustomerDto>> response) {
                        CommonUtil.customers = response.body();
                    }

                    @Override
                    public void onFailure(Call<List<CustomerDto>> call, Throwable t) {
                        call.cancel();
                    }
                });
    }

    private void fetchOrderStatusFromServer(){
        API.getClient().create(OrderApi.class).getOrderStatus()
                .enqueue(new Callback<List<OrderStatusDto>>() {
                    @Override
                    public void onResponse(Call<List<OrderStatusDto>> call, Response<List<OrderStatusDto>> response) {
                        if(response.code()==200){
                            CommonUtil.statusList = (ArrayList<OrderStatusDto>) response.body();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<OrderStatusDto>> call, Throwable t) {
                        call.cancel();
                    }
                });
    }
}