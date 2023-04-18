package com.akg.akg_sales.view.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityCartBinding;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.CartItemAdapter;
import com.akg.akg_sales.viewmodel.order.CartViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {
    public ActivityCartBinding cartBinding;
    public Long selectedCustomerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
    }

    private void loadPage(){
        cartBinding = DataBindingUtil.setContentView(this,R.layout.activity_cart);
        cartBinding.setActivity(this);
        cartBinding.setVm(new CartViewModel(this));
        cartBinding.executePendingBindings();
        loadCustomerList();
    }

    public void loadCartListView(){
        ArrayList<CartItemDto> list = new ArrayList<>();
        for(CartItemDto c:CommonUtil.cartItems)
            if(Objects.equals(c.getCustomerDto().getId(), selectedCustomerId))
                list.add(c);
        RecyclerView recyclerView = cartBinding.selectedItemListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        CartItemAdapter adapter = new CartItemAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    private void loadCustomerList(){
        AutoCompleteTextView tView=cartBinding.customerList;
        String[] customers = new String[CommonUtil.customers.size()];
        for (int i=0;i< CommonUtil.customers.size();i++)
            customers[i]=CommonUtil.customers.get(i).getCustomerName();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customers);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> onClickCustomer(i));

        initialClick();
    }

    private void onClickCustomer(int idx){
        this.selectedCustomerId = CommonUtil.customers.get(idx).getId();
        loadCartListView();
    }

    private void initialClick(){
        AutoCompleteTextView tView=cartBinding.customerList;
        tView.setText(CommonUtil.customers.get(0).getCustomerName());
        onClickCustomer(0);
    }

}