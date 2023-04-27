package com.akg.akg_sales.view.activity.order;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.databinding.ActivityOrderDetailBinding;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderLineDto;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.OrderLineAdapter;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    public boolean orderActionPermitted=false;
    public RecyclerView recyclerView;
    public ActivityOrderDetailBinding binding;
    public OrderDto orderDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            fetchOrderDetailFromServer(getIntent().getStringExtra("orderId"));
        }catch (Exception e){finish();}
    }

    private void loadPage(){
        if(orderDto==null) finish();
        binding = DataBindingUtil.setContentView(this,R.layout.activity_order_detail);
        binding.setActivity(this);
        binding.setVm(orderDto);
        binding.executePendingBindings();
        setOrderActionUi();
        loadOrderLines();
    }

    private void fetchOrderDetailFromServer(String orderId){
        API.getClient().create(OrderApi.class).getOrderDetail(orderId)
                .enqueue(new Callback<OrderDto>() {
                    @Override
                    public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
                        orderDto = response.body();
                        loadPage();
                    }

                    @Override
                    public void onFailure(Call<OrderDto> call, Throwable t) {
                        call.cancel();
                        finish();
                    }
                });
    }

    public void loadOrderLines(){
        recyclerView = binding.orderLinesList;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        OrderLineAdapter adapter = new OrderLineAdapter(this,
                (ArrayList<OrderLineDto>) orderDto.getOrderLines());
        recyclerView.setAdapter(adapter);
    }

    public void onClickApprove(){
        new ConfirmationDialog(this,"Approve Order?",i->{
            OrderRequest body = new OrderRequest();
            body.setOrderId(orderDto.getId()).setCustomerId(orderDto.getCustomerId());
            for (OrderLineDto l : orderDto.getOrderLines()) {
                body.addLine(l.getItemId(),l.getQuantity().intValue());
            }
            API.getClient().create(OrderApi.class).approveOrder(body)
                    .enqueue(new Callback<OrderDto>() {
                        @Override
                        public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
                            CommonUtil.showToast(getApplicationContext(),"Order Approved",true);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<OrderDto> call, Throwable t) {
                            CommonUtil.showToast(getApplicationContext(),"Order Approve Failed",false);
                        }
                    });
        });
    }

    public void onClickCancel(){
        new ConfirmationDialog(this,"Cancel Order?",i->{

        });
    }

    private void setOrderActionUi(){
        orderActionPermitted = Objects.equals(orderDto.getCurrentApproverUsername(),
                CommonUtil.loggedInUser.getUsername());

        if(orderActionPermitted){
            binding.orderAction.setVisibility(View.VISIBLE);
            binding.orderApprove.setOnClickListener(view -> onClickApprove());
            binding.orderCancel.setOnClickListener(view -> onClickCancel());
        }
        else binding.orderAction.setVisibility(View.GONE);
    }

}