package com.akg.akg_sales.view.activity.order;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityOrderBinding;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.OrderItemAdapter;
import com.akg.akg_sales.view.dialog.ItemFilterDialog;
import java.util.ArrayList;
import java.util.Arrays;

public class OrderActivity extends AppCompatActivity {
    public ActivityOrderBinding orderBinding;
    ArrayList<ItemDto> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateCartBtnLabel();
    }

    private void loadPage(){
        orderBinding = DataBindingUtil.setContentView(this,R.layout.activity_order);
        orderBinding.setActivity(this);
        orderBinding.executePendingBindings();
        setCustomer();
        enableItemSearch();
    }

    private void updateItemList(ArrayList<ItemDto> list){
        if(list.isEmpty())
            CommonUtil.showToast(getApplicationContext(),"No Matching Items Found",false);

        RecyclerView recyclerView = orderBinding.itemListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        OrderItemAdapter adapter = new OrderItemAdapter(this,list);
        recyclerView.setAdapter(adapter);
    }

    private void enableItemSearch(){
        orderBinding.itemSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<ItemDto> tmpItemList = new ArrayList<>();
                if(s==null || s.length()==0) tmpItemList = itemList;
                else if(s.length()<=10){
                    for(ItemDto i:itemList){
                        String dataText = (i.getItemCode()+" "+i.getItemDescription()).toUpperCase();
                        boolean match = true;
                        for (String w:s.toString().split(" ")){
                            if(!dataText.contains(w.toUpperCase())) {
                                match = false;
                                break;
                            }
                        }
                        if(match) tmpItemList.add(i);
                    }
                }
                updateItemList(tmpItemList);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        disableSearchField();
    }

    private void disableSearchField(){
        orderBinding.itemSearchBox.setVisibility(View.GONE);
        orderBinding.applyFilterMsgText.setVisibility(View.VISIBLE);
    }

    private void enableSearchField(){
        orderBinding.itemSearchBox.setVisibility(View.VISIBLE);
        orderBinding.applyFilterMsgText.setVisibility(View.GONE);
    }

    public void fetchItemFromServer(Long subTypeId){
        OrderService.fetchItemFromServer(this,subTypeId,list->{
            itemList = (ArrayList<ItemDto>) list;
            updateItemList(itemList);
            if(itemList.isEmpty()) disableSearchField();
            else enableSearchField();
        });
    }

    private void setCustomer(){
        if(CommonUtil.customers.size()>0){
            AutoCompleteTextView tView=orderBinding.customerList;
            String[] customers = new String[CommonUtil.customers.size()];
            for (int i=0;i< CommonUtil.customers.size();i++)
                customers[i]=CommonUtil.customers.get(i).getCustomerName();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customers);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onCustomerSelect(tView,i));
            onCustomerSelect(tView,0);
        }
    }

    private void onCustomerSelect(AutoCompleteTextView tView,int idx){
        tView.setText(CommonUtil.customers.get(idx).getCustomerName(),false);
        CommonUtil.selectedCustomer = CommonUtil.customers.get(idx);
        updateCartBtnLabel();
        itemList.clear();
        updateItemList(itemList);
        disableSearchField();
    }

    public void onClickFilter(){
        new ItemFilterDialog(this);
    }

    public void onClickCart(){
        ArrayList<CartItemDto> cartItems = CommonUtil.orderCart.get(CommonUtil.selectedCustomer.getId());
        if(cartItems==null || cartItems.isEmpty()){
            CommonUtil.showToast(this,"Please add Items to Cart First",false);
            return;
        }
        Intent cartIntent = new Intent(this, CartActivity.class);
        startActivity(cartIntent);
    }

    public void updateCartBtnLabel(){
        ArrayList<CartItemDto> cartItems = CommonUtil.orderCart.get(CommonUtil.selectedCustomer.getId());
        int size = cartItems==null?0:cartItems.size();
        orderBinding.cartBtnLabel.setText("("+size+")");
    }

}