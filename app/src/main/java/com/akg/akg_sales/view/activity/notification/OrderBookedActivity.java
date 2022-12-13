package com.akg.akg_sales.view.activity.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.NotificationApi;
import com.akg.akg_sales.databinding.ActivityOrderBookedBinding;
import com.akg.akg_sales.dto.notification.OrderBookedHeaderDto;
import com.akg.akg_sales.dto.notification.OrderBookedLineDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.OrderBookedAdapter;
import com.akg.akg_sales.view.adapter.OrderBookedLineAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderBookedActivity extends AppCompatActivity {

    ActivityOrderBookedBinding orderBookedBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String orderId = getIntent().getStringExtra("orderId");
        fetchOrderDetail(orderId);
    }

    private void fetchOrderDetail(String orderId){
        System.out.println(orderId);
        NotificationApi api = API.getClient().create(NotificationApi.class);
        Call<OrderBookedHeaderDto> call = api.getOrderBookedDetail(orderId);
        call.enqueue(new Callback<OrderBookedHeaderDto>() {
            @Override
            public void onResponse(Call<OrderBookedHeaderDto> call, Response<OrderBookedHeaderDto> response) {
                OrderBookedHeaderDto headerDto = response.body();
                loadPage(headerDto);
            }

            @Override
            public void onFailure(Call<OrderBookedHeaderDto> call, Throwable t) {
                call.cancel();
                finish();
                CommonUtil.showToast(getApplicationContext(),"Failed to fetch Order Detail",false);
            }
        });
    }

    private void loadPage(OrderBookedHeaderDto headerDto){
        orderBookedBinding = DataBindingUtil.setContentView(this,R.layout.activity_order_booked);
        orderBookedBinding.setActivity(this);
        orderBookedBinding.setVm(headerDto);
        orderBookedBinding.executePendingBindings();
        loadOrderLineList((ArrayList<OrderBookedLineDto>) headerDto.getOrderLines());
    }

    private void loadOrderLineList(ArrayList<OrderBookedLineDto> lines){
        RecyclerView recyclerView = orderBookedBinding.orderLinesList;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        OrderBookedLineAdapter adapter = new OrderBookedLineAdapter(this,lines);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, layoutManager.getOrientation()));
    }
}