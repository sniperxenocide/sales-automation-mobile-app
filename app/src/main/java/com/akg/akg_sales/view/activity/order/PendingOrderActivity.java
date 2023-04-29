package com.akg.akg_sales.view.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.databinding.ActivityPendingOrderBinding;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.PendingOrderAdapter;
import com.akg.akg_sales.viewmodel.order.PendingOrderViewModel;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingOrderActivity extends AppCompatActivity {
    private ActivityPendingOrderBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
        fetchOrderFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchOrderFromServer();
    }


    private void loadPage(){
        binding= DataBindingUtil.setContentView(this,R.layout.activity_pending_order);
        binding.setVm(new PendingOrderViewModel(this));
        binding.setActivity(this);
    }

    private void loadPendingOrderListView(ArrayList<OrderDto> list){
        if(list.isEmpty()) CommonUtil.showToast(this,"No Orders Available",false);
        RecyclerView recyclerView = binding.pendingOrderListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        PendingOrderAdapter adapter = new PendingOrderAdapter(this,list);
        recyclerView.setAdapter(adapter);
    }

    private void fetchOrderFromServer(){
        try {
            API.getClient().create(OrderApi.class).getAllOrders()
            .enqueue(new Callback<PageResponse<OrderDto>>() {
                @Override
                public void onResponse(Call<PageResponse<OrderDto>> call, Response<PageResponse<OrderDto>> response) {
                    try {
                        if(response.code()==200){
                            PageResponse<OrderDto> page = response.body();
                            ArrayList<OrderDto> orders = (ArrayList<OrderDto>) page.getData();
                            loadPendingOrderListView(orders);
                        }else throw new Exception(response.code()+"."+response.message());
                    }catch (Exception e){
                        CommonUtil.showToast(getApplicationContext(),e.getMessage(),false);
                    }
                }
                @SneakyThrows @Override
                public void onFailure(Call<PageResponse<OrderDto>> call, Throwable t) {
                    call.cancel();
                    throw new Exception(t.getMessage());
                }
            });
        }catch (Exception e){
            CommonUtil.showToast(this,e.getMessage(),false);
            e.printStackTrace();
        }
    }

}