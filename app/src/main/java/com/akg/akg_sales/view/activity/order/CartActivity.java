package com.akg.akg_sales.view.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityCartBinding;
import com.akg.akg_sales.dto.CartItemDto;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.util.CommonUtil;
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
        RecyclerView recyclerView = cartBinding.selectedItemListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        CartItemAdapter adapter = new CartItemAdapter(this, (ArrayList<CartItemDto>) CommonUtil.cartItems);
        recyclerView.setAdapter(adapter);
    }

}