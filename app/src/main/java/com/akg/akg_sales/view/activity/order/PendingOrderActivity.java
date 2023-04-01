package com.akg.akg_sales.view.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityPendingOrderBinding;
import com.akg.akg_sales.dto.ItemDto;
import com.akg.akg_sales.dto.OrderDto;
import com.akg.akg_sales.view.adapter.OrderItemAdapter;
import com.akg.akg_sales.view.adapter.PendingOrderAdapter;
import com.akg.akg_sales.viewmodel.order.PendingOrderViewModel;

import java.util.ArrayList;

public class PendingOrderActivity extends AppCompatActivity {
    private ActivityPendingOrderBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
        loadPendingOrderListView();
    }

    private void loadPage(){
        binding= DataBindingUtil.setContentView(this,R.layout.activity_pending_order);
        binding.setVm(new PendingOrderViewModel(this));
        binding.setActivity(this);
    }

    private void loadPendingOrderListView(){
        ArrayList<OrderDto> list = new ArrayList<>();
        loadDummyPendingOrder(list);
        RecyclerView recyclerView = binding.pendingOrderListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        PendingOrderAdapter adapter = new PendingOrderAdapter(this,list);
        recyclerView.setAdapter(adapter);
    }

    private void loadDummyPendingOrder(ArrayList<OrderDto> list){
        list.add(new OrderDto(1L,"20415.20230329.1","29-Mar-2023",
                "20415","Bismillah Treaders","Pending Approval",235400.0));
        list.add(new OrderDto(2L,"20415.20230329.2","29-Mar-2023",
                "20415","Bismillah Treaders","Pending Approval",120500.0));
        list.add(new OrderDto(3L,"20415.20230326.1","26-Mar-2023",
                "20415","Bismillah Treaders","Awaiting Software Posting (Approved)",305000.0));
        list.add(new OrderDto(4L,"20415.20230325.1","25-Mar-2023",
                "20415","Bismillah Treaders","Order Posted.Wait for Notification",212500.0));

    }
}