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
import com.akg.akg_sales.dto.ItemDto;
import com.akg.akg_sales.dto.OrderDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.OrderItemAdapter;
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
            Call<List<OrderDto>> call = API.getClient().create(OrderApi.class).getAllOrders();
            call.enqueue(new Callback<List<OrderDto>>() {
                @Override
                public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                    ArrayList<OrderDto> orders = (ArrayList<OrderDto>) response.body();
                    loadPendingOrderListView(orders);
                }
                @SneakyThrows @Override
                public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                    call.cancel();
                    throw new Exception(t.getMessage());
                }
            });
        }catch (Exception e){
            CommonUtil.showToast(this,e.getMessage(),false);
            e.printStackTrace();
        }
    }

    private void loadDummyPendingOrder(ArrayList<OrderDto> list){
//        list.add(new OrderDto(1L,"20415.20230329.1","29-Mar-2023",
//                "20415","Bismillah Treaders","Pending Approval",235400.0));
//        list.add(new OrderDto(2L,"20415.20230329.2","29-Mar-2023",
//                "20415","Bismillah Treaders","Pending Approval",120500.0));
//        list.add(new OrderDto(3L,"20415.20230326.1","26-Mar-2023",
//                "20415","Bismillah Treaders","Awaiting Software Posting (Approved)",305000.0));
//        list.add(new OrderDto(4L,"20415.20230325.1","25-Mar-2023",
//                "20415","Bismillah Treaders","Order Posted.Wait for Notification",212500.0));

    }
}