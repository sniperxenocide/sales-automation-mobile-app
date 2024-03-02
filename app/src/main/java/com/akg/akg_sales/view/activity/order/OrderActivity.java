package com.akg.akg_sales.view.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityOrderBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.dto.item.ItemMaster;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.order.OrderItemAdapter;
import com.akg.akg_sales.view.dialog.ItemFilterDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {
    public ActivityOrderBinding orderBinding;
    ArrayList<ItemDto> itemList = new ArrayList<>();
    public int selectedCustomerIdx = -1;
    public List<CustomerDto> customerList = CommonUtil.customers;
    public ItemMaster itemMaster = new ItemMaster();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFirebaseUserId();
        loadPage();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateCartBtnLabel();
        orderBinding.itemSearchBox.getText().clear();
        updateItemList(itemList);
    }

    private void loadPage(){
        orderBinding = DataBindingUtil.setContentView(this,R.layout.activity_order);
        orderBinding.setActivity(this);
        orderBinding.executePendingBindings();
        setCustomer();
        enableItemSearch();
        //fetchItemMaster();
    }

    private void updateItemList(ArrayList<ItemDto> list){
        if(list.isEmpty())
            CommonUtil.showToast(getApplicationContext(),"No Matching Items Found",false);

        orderBinding.selectItemLabel.setText("Select Item ("+list.size()+"):");
        RecyclerView recyclerView = orderBinding.itemListview;
        recyclerView.setItemViewCacheSize(list.size());
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
                ArrayList<ItemDto> searchItemList = new ArrayList<>();
                if(s==null || s.length()==0) searchItemList = itemList;
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
                        if(match) searchItemList.add(i);
                    }
                }
                updateItemList(searchItemList);
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

    private void fetchItemMaster(){
        OrderService.fetchItemMasterFromServer(this,
                customerList.get(selectedCustomerIdx).getId(),
                res-> itemMaster = res);
    }

    public void fetchItemFromServer(Map<String,String> filter){
        filter.put("customerId",customerList.get(selectedCustomerIdx).getId().toString());
        OrderService.fetchItemFromServer(this,filter,list->{
            itemList = (ArrayList<ItemDto>) list;
            updateItemList(itemList);
            if(itemList.isEmpty()) disableSearchField();
            else enableSearchField();
        });
    }

    private void setCustomer(){
        if(customerList!=null && customerList.size()>0){
            AutoCompleteTextView tView=orderBinding.customerList;
            String[] customers = new String[customerList.size()];
            for (int i=0;i< customerList.size();i++)
                customers[i]=customerList.get(i).getCustomerName()+" ("+customerList.get(i).getOracleCustomerCode()+")";
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customers);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onCustomerSelect(tView,i));
            onCustomerSelect(tView,0);
        }
    }

    private void onCustomerSelect(AutoCompleteTextView tView,int idx){
        tView.setText(customerList.get(idx).getCustomerName()+" ("+customerList.get(idx).getOracleCustomerCode()+")",false);

        selectedCustomerIdx = idx;

        itemList.clear();
        updateItemList(itemList);

        fetchItemMaster();

        updateCartBtnLabel();
        disableSearchField();
    }

    public void onClickFilter(){
        new ItemFilterDialog(this);
    }

    public void onClickCart(){
        if(CommonUtil.orderCart.isEmpty()){
            CommonUtil.showToast(this,"Please add Items to Cart First",false);
            return;
        }
        Intent cartIntent = new Intent(this, CartActivity.class);
        startActivity(cartIntent);
    }

    public void updateCartBtnLabel(){
        ArrayList<CartItemDto> cartItems = CommonUtil.orderCart.get(customerList.get(selectedCustomerIdx).getId());
        int size = cartItems==null?0:cartItems.size();
        orderBinding.cartBtnLabel.setText("("+size+")");
    }

}