package com.akg.akg_sales.view.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityCartBinding;
import com.akg.akg_sales.dto.CartItemDto;
import com.akg.akg_sales.dto.ItemDto;
import com.akg.akg_sales.view.adapter.CartItemAdapter;
import com.akg.akg_sales.viewmodel.order.CartViewModel;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding cartBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
        loadCartListView();
    }

    private void loadPage(){
        cartBinding = DataBindingUtil.setContentView(this,R.layout.activity_cart);
        cartBinding.setActivity(this);
        cartBinding.setVm(new CartViewModel(this));
        cartBinding.executePendingBindings();
    }

    private void loadCartListView(){
        ArrayList<CartItemDto> list = new ArrayList<>();
        loadDummyItems(list);
        RecyclerView recyclerView = cartBinding.selectedItemListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        CartItemAdapter adapter = new CartItemAdapter(this,list);
        recyclerView.setAdapter(adapter);
    }

    private void loadDummyItems(ArrayList<CartItemDto> list){
        for (int i=0;i<4;i++)
            list.add(new CartItemDto(new ItemDto((long)(i+1),"POW.FCMP.0"+(i+1)*100,
                    "Marks FCMP "+100*(i+1)+"gm","CTN"),20.0));
        for (int i=0;i<2;i++)
            list.add(new CartItemDto(new ItemDto((long)(i+1),"TEA.SYLN.0"+(i+1)*100,
                    "Seylon Family Blend "+100*(i+1)+"gm","CTN"),12.0));
    }
}