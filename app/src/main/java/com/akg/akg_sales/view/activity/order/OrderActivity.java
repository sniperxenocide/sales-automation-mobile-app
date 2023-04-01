package com.akg.akg_sales.view.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityOrderBinding;
import com.akg.akg_sales.dto.ItemDto;
import com.akg.akg_sales.view.adapter.OrderItemAdapter;
import com.akg.akg_sales.viewmodel.order.OrderViewModel;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    private ActivityOrderBinding orderBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
        updateItemList();
    }

    private void loadPage(){
        orderBinding = DataBindingUtil.setContentView(this,R.layout.activity_order);
        orderBinding.setActivity(this);
        orderBinding.setVm(new OrderViewModel(this));
        orderBinding.executePendingBindings();
    }

    private void updateItemList(){
        ArrayList<ItemDto> list = new ArrayList<>();
        loadDummyItems(list);
        RecyclerView recyclerView = orderBinding.itemListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        OrderItemAdapter adapter = new OrderItemAdapter(this,list);
        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(
//                this, layoutManager.getOrientation()));
    }

    private void loadDummyItems(ArrayList<ItemDto> list){
        for (int i=0;i<5;i++)
            list.add(new ItemDto((long)(i+1),"POW.FCMP.0"+(i+1)*100,
                    "Marks FCMP "+100*(i+1)+"gm","CTN"));
        for (int i=0;i<5;i++)
            list.add(new ItemDto((long)(i+1),"TEA.SYLN.0"+(i+1)*100,
                    "Seylon Family Blend "+100*(i+1)+"gm","CTN"));
    }
}