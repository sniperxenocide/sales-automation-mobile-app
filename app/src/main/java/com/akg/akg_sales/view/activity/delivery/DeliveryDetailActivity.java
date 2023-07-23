package com.akg.akg_sales.view.activity.delivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityDeliveryDetailBinding;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedHeaderDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.service.DeliveryService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.delivery.DeliveryLineAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeliveryDetailActivity extends AppCompatActivity {
    ActivityDeliveryDetailBinding binding;
    public MoveOrderConfirmedHeaderDto moveConfirmedHeaderDto;
    List<MoveOrderConfirmedLineDto> deliveryLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
        fetchDeliveryDetail();
    }

    private void loadPage(){
        try {
            moveConfirmedHeaderDto = (MoveOrderConfirmedHeaderDto) getIntent().getSerializableExtra("moveHeader");
        }catch (Exception e){e.printStackTrace();}
        binding = DataBindingUtil.setContentView(this,R.layout.activity_delivery_detail);
        binding.setActivity(this);
        binding.executePendingBindings();
        binding.scrollView.smoothScrollTo(0,0);
    }

    private void fetchDeliveryDetail(){
        try {
            HashMap<String,String> filter = (HashMap<String, String>) getIntent().getSerializableExtra("filter");
            filter.put("movOrderNo",moveConfirmedHeaderDto.getMovOrderNo());
            DeliveryService.fetchDeliveryDetailLinesFromServer(this,filter,res->{
                deliveryLines = res;
                System.out.println("Lines: "+deliveryLines.size());

                loadCustomerList();
            });
        }catch (Exception e){e.printStackTrace();}

    }

    HashMap<String,String> customerMap;
    String[] customerNames;
    private void loadCustomerList(){
        try {
            customerMap = new HashMap<>();
            AutoCompleteTextView tView=binding.customerList;
            for(MoveOrderConfirmedLineDto l:deliveryLines) customerMap.put(l.getCustomerName()+" ("+l.getCustomerNumber()+")",l.getCustomerNumber());
            binding.customerListLayout.setHint("Customers ("+customerMap.size()+")");
            customerNames = new String[customerMap.size()];
            int idx=0;
            for(String k: customerMap.keySet()) {customerNames[idx] = k; idx++;}
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,customerNames);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onCustomerSelect(tView,i));
            onCustomerSelect(tView,0);
        }catch (Exception e){e.printStackTrace();}
    }

    private void onCustomerSelect(AutoCompleteTextView tView,int idx){
        try {
            tView.setText(customerNames[idx],false);
            loadOrderNumbers(customerMap.get(customerNames[idx]));
        }catch (Exception e){e.printStackTrace();}
    }

    ArrayList<Long> orderNumberList = new ArrayList<>();
    private void loadOrderNumbers(String customerNumber){
        try {
            orderNumberList.clear();
            AutoCompleteTextView tView=binding.orderList;
            for(MoveOrderConfirmedLineDto l:deliveryLines){
                if(l.getCustomerNumber().equals(customerNumber) && !orderNumberList.contains(l.getOrderNumber()) )
                    orderNumberList.add(l.getOrderNumber());
            }
            binding.orderListLayout.setHint("Order Numbers ("+orderNumberList.size()+")");
            String[] orders = new String[orderNumberList.size()];
            for(int i=0;i<orderNumberList.size();i++) orders[i] = orderNumberList.get(i).toString();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,orders);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onOrderSelect(tView,i));
            onOrderSelect(tView,0);
        }catch (Exception e){e.printStackTrace();}
    }

    private void onOrderSelect(AutoCompleteTextView tView,int idx){
        try {
            tView.setText(String.valueOf(orderNumberList.get(idx)),false);
            loadDoNumbers(orderNumberList.get(idx));
        }catch (Exception e){e.printStackTrace();}
    }

    ArrayList<String> doNumberList = new ArrayList<>();
    private void loadDoNumbers(Long orderNumber){
        try {
            doNumberList.clear();
            AutoCompleteTextView tView=binding.doList;
            for(MoveOrderConfirmedLineDto l:deliveryLines){
                if(l.getOrderNumber().longValue()==orderNumber && !doNumberList.contains(l.getDoNumber()) )
                    doNumberList.add(l.getDoNumber());
            }
            binding.doListLayout.setHint("DO Numbers ("+doNumberList.size()+")");
            String[] doNumbers = new String[doNumberList.size()];
            for(int i=0;i<doNumberList.size();i++) doNumbers[i] = doNumberList.get(i);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,doNumbers);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onDoNumberSelect(tView,i));
            onDoNumberSelect(tView,0);
        }catch (Exception e){e.printStackTrace();}
    }

    private void onDoNumberSelect(AutoCompleteTextView tView,int idx){
        try {
            tView.setText(doNumberList.get(idx),false);
            loadItemListRecycleView(doNumberList.get(idx));
        }catch (Exception e){e.printStackTrace();}
    }

    private void loadItemListRecycleView(String doNumber){
        ArrayList<MoveOrderConfirmedLineDto> items = new ArrayList<>();
        for(MoveOrderConfirmedLineDto l:deliveryLines){
            if(l.getDoNumber().equals(doNumber)) items.add(l);
        }
        binding.itemListHeader.setText("Items ("+items.size()+")");
        RecyclerView recyclerView = binding.deliveryLinesList;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DeliveryLineAdapter adapter = new DeliveryLineAdapter(this,items);
        recyclerView.setAdapter(adapter);
    }
}