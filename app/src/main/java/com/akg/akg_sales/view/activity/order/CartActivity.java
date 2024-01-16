package com.akg.akg_sales.view.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityCartBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.CustomerSiteDto;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.order.OrderTypeDto;
import com.akg.akg_sales.service.CustomerService;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.order.CartItemAdapter;
import com.akg.akg_sales.viewmodel.order.CartViewModel;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {
    public ActivityCartBinding cartBinding;
    public RecyclerView recyclerView;
    public CustomerDto cSelectedCustomer;
    public CustomerSiteDto cSelectedSite;
    public OrderTypeDto selectedOrderType;
    public String attachmentPath;
    public HashMap<Long,ArrayList<CartItemDto>> cartMap = CommonUtil.orderCart;
    public List<CustomerSiteDto> customerSites = new ArrayList<>();
    public List<OrderTypeDto> orderTypes = new ArrayList<>();

    String[] customers;
    Long[] customerIds;
    String[] custSites;
    String[] orderTypeStr;

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
        fetchOrderTypes();
    }

    public void loadCartListView(){
        ArrayList<CartItemDto> list = cartMap.get(cSelectedCustomer.getId());
        if(list==null) return;
        cartBinding.selectedItemLabel.setText("Selected Items ("+list.size()+"):");
        recyclerView = cartBinding.selectedItemListview;
        recyclerView.setItemViewCacheSize(list.size());
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
                CustomerDto c = cartMap.get(k).get(0).getCustomerDto();
                customers[j]=c.getCustomerName()+" ("+c.getOracleCustomerCode()+")";
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
        tView.setText(cSelectedCustomer.getCustomerName()
                +" ("+cSelectedCustomer.getOracleCustomerCode()+")",false);
        loadCartListView();
        calculateOrderValue();

        customerSites = new ArrayList<>();
        loadSiteList();
        CustomerService.fetchCustomerSites(this,cSelectedCustomer.getId(),res->{
            customerSites = res;
            loadSiteList();
        });
    }

    private void loadSiteList(){
        try {
            cSelectedSite = null;
            AutoCompleteTextView tView=cartBinding.customerSiteList;
            handleSiteTyping(tView);
            custSites = new String[customerSites.size()+1];
            custSites[0] = "----";
            for (CustomerSiteDto s: customerSites){
                int idx = customerSites.indexOf(s)+1;
                custSites[idx]=s.getAddress();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, custSites);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onClickSite(tView));
//            tView.setText(custSites[0],false);
        }catch (Exception e){}
    }

    private void onClickSite(AutoCompleteTextView tView){
        for(CustomerSiteDto s:customerSites){
            if(s.getAddress().equals(tView.getText().toString())){
                cSelectedSite = s;
                break;
            }
            cSelectedSite = null;
        }
        cartBinding.siteWarning.setVisibility(View.GONE);
        System.out.println(cSelectedSite);
    }

    private void handleSiteTyping(AutoCompleteTextView tView){
        tView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(cSelectedSite==null || !s.toString().equals(cSelectedSite.getAddress()))
                {
                    cSelectedSite=null;
                    cartBinding.siteWarning.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void fetchOrderTypes(){
        OrderService.fetchOrderTypes(this,types->{
            this.orderTypes = types;
            loadOrderTypeList();
        });
    }

    private void loadOrderTypeList(){
        try {
            selectedOrderType = null;
            AutoCompleteTextView tView=cartBinding.orderTypeList;
            orderTypeStr = new String[orderTypes.size()];
            for (OrderTypeDto t: orderTypes){
                int idx = orderTypes.indexOf(t);
                orderTypeStr[idx]=t.getOrderType();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, orderTypeStr);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onClickType(tView,i));
            onClickType(tView,0);
        }catch (Exception e){}
    }

    private void onClickType(AutoCompleteTextView tView,int idx){
        try {
            selectedOrderType = orderTypes.get(idx);
            tView.setText(selectedOrderType.getOrderType(),false);
        }catch (Exception e){}
    }

    public void calculateOrderValue(){
        double value = 0.0;
        for(CartItemDto c: Objects.requireNonNull(cartMap.get(cSelectedCustomer.getId()))){
            value = value + c.getQuantity()*c.getItemDto().getUnitPrice();
        }
        cartBinding.orderValue.setText("Gross Value:\n"+CommonUtil.decimalToAccounting(value)+" Tk");
    }

    public void onClickAttachment(){
        ImagePicker.with(this).crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(500)			//Final image size will be less than 500 KB(Optional)
                .maxResultSize(800, 600)	//Final image resolution will be less than 800 x 600(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                Uri uri = data.getData();
                System.out.println(uri.getPath());
                attachmentPath = uri.getPath();
                String[] paths = uri.getPath().split("/");
                cartBinding.attachment.setText(paths[paths.length-1]);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }



}