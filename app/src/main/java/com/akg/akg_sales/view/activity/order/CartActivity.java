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
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.CartItemAdapter;
import com.akg.akg_sales.viewmodel.order.CartViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {
    public ActivityCartBinding cartBinding;
    public RecyclerView recyclerView;
    public CustomerDto cSelectedCustomer;
    public HashMap<Long,ArrayList<CartItemDto>> cartMap = CommonUtil.orderCart;

    String[] customers;
    Long[] customerIds;

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
        ArrayList<CartItemDto> list = cartMap.get(cSelectedCustomer.getId());
        recyclerView = cartBinding.selectedItemListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        CartItemAdapter adapter = new CartItemAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    public void loadCustomerList(){
        AutoCompleteTextView tView=cartBinding.customerList;
        customers = new String[cartMap.size()];
        customerIds = new Long[cartMap.size()];
        int j=0;
        for (Long k: cartMap.keySet()){
            try {
                customers[j]=cartMap.get(k).get(0).getCustomerDto().getCustomerName();
                customerIds[j]=k;
                j++;
            }catch (Exception e){e.getMessage();}
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customers);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> onClickCustomer(tView,i));
        onClickCustomer(tView,0);
    }

    private void onClickCustomer(AutoCompleteTextView tView,int idx){
        cSelectedCustomer = cartMap.get(customerIds[idx]).get(0).getCustomerDto();
        tView.setText(cSelectedCustomer.getCustomerName(),false);
        loadCartListView();
    }

}