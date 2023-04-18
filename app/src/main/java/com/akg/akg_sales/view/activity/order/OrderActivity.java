package com.akg.akg_sales.view.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.ItemApi;
import com.akg.akg_sales.databinding.ActivityOrderBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.dto.item.ItemTypeDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.OrderItemAdapter;
import com.akg.akg_sales.viewmodel.order.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private ActivityOrderBinding orderBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomer();
        loadPage();
    }

    private void loadPage(){
        orderBinding = DataBindingUtil.setContentView(this,R.layout.activity_order);
        orderBinding.setActivity(this);
        orderBinding.setVm(new OrderViewModel(this));
        orderBinding.executePendingBindings();
    }

    private void updateItemList(ArrayList<ItemDto> list){
        RecyclerView recyclerView = orderBinding.itemListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        OrderItemAdapter adapter = new OrderItemAdapter(this,list);
        recyclerView.setAdapter(adapter);
    }

    public void fetchItemFromServer(Long subTypeId){
        API.getClient().create(ItemApi.class)
                .getOrderItems(CommonUtil.selectedCustomer.getId(),subTypeId)
                .enqueue(new Callback<List<ItemDto>>() {
                    @Override
                    public void onResponse(Call<List<ItemDto>> call, Response<List<ItemDto>> response) {
                        List<ItemDto> list = response.body();
                        if(list.isEmpty()) CommonUtil.showToast(getApplicationContext(),"No Items Found",false);
                        else updateItemList((ArrayList<ItemDto>) list);
                    }
                    @Override
                    public void onFailure(Call<List<ItemDto>> call, Throwable t) {call.cancel();}
                });
    }

    private void setCustomer(){
        if(CommonUtil.customers.size()>0)
            CommonUtil.selectedCustomer = CommonUtil.customers.get(0);
    }

}