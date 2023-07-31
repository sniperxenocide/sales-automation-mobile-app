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
import com.akg.akg_sales.view.dialog.DeliveryDetailReportDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DeliveryDetailActivity extends AppCompatActivity {
    ActivityDeliveryDetailBinding binding;
    public MoveOrderConfirmedHeaderDto moveConfirmedHeaderDto;
    public List<MoveOrderConfirmedLineDto> deliveryLines;

    Hashtable<String,HashSet<Long>> customerOrdersMap = new Hashtable<>();
    Hashtable<Long,HashSet<String>> orderDosMap = new Hashtable<>();
    Hashtable<String,ArrayList<MoveOrderConfirmedLineDto>> doItemsMap = new Hashtable<>();
    public Hashtable<String,Integer> dataFrequency = new Hashtable<>();

    DeliveryDetailReportDialog reportDialog;

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

                loadMaps();
                loadCustomerList();
                reportDialog = new DeliveryDetailReportDialog(this);
            });
        }catch (Exception e){e.printStackTrace();}

    }

    private void loadMaps(){
        for(MoveOrderConfirmedLineDto l:deliveryLines){
            try {
                String customer = l.getCustomerName() + " (" + l.getCustomerNumber() + ")";
                Long order = l.getOrderNumber();
                String doNumber = l.getDoNumber();
                if (customerOrdersMap.get(customer) == null) {
                    customerOrdersMap.put(customer, new HashSet<>());
                }
                customerOrdersMap.get(customer).add(order);

                if (orderDosMap.get(order) == null) {
                    orderDosMap.put(order, new HashSet<>());
                }
                orderDosMap.get(order).add(doNumber);

                if (doItemsMap.get(doNumber) == null) {
                    doItemsMap.put(doNumber, new ArrayList<>());
                }
                doItemsMap.get(doNumber).add(l);

                if(dataFrequency.get(customer) == null) dataFrequency.put(customer,1);
                else dataFrequency.put(customer,dataFrequency.get(customer)+1);
                if(dataFrequency.get(order.toString()) == null) dataFrequency.put(order.toString(),1);
                else dataFrequency.put(order.toString(),dataFrequency.get(order.toString())+1);
                doNumber = order+doNumber;  // Multiple Order can be in One DO
                if(dataFrequency.get(doNumber) == null) dataFrequency.put(doNumber,1);
                else dataFrequency.put(doNumber,dataFrequency.get(doNumber)+1);
            }catch (Exception e){e.printStackTrace();}
        }
        System.out.println(dataFrequency);
    }

    private void printMap(){
        try {
            System.out.println(customerOrdersMap);
            System.out.println(orderDosMap);
            for (String d:doItemsMap.keySet()){
                System.out.println(d);
                for(MoveOrderConfirmedLineDto l: Objects.requireNonNull(doItemsMap.get(d))){
                    System.out.println(l.getItemDescription()+" "+l.getLineQuantity()+" "+l.getUomCode());
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    String[] customerNames;
    private void loadCustomerList(){
        try {
            binding.customerListLayout.setHint("Customers ("+customerOrdersMap.size()+")");
            AutoCompleteTextView tView=binding.customerList;
            customerNames = new String[customerOrdersMap.size()];
            int idx=0;
            for(String k: customerOrdersMap.keySet()) {customerNames[idx] = k; idx++;}
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,customerNames);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onCustomerSelect(tView,i));
            onCustomerSelect(tView,0);
        }catch (Exception e){e.printStackTrace();}
    }

    private void onCustomerSelect(AutoCompleteTextView tView,int idx){
        try {
            tView.setText(customerNames[idx],false);
            loadOrderNumbers(customerNames[idx]);
        }catch (Exception e){e.printStackTrace();}
    }

    String[] orderNumbers;
    private void loadOrderNumbers(String customerKey){
        try {
            HashSet<Long> orderNumbersSet = customerOrdersMap.get(customerKey);
            binding.orderListLayout.setHint("Order Numbers ("+orderNumbersSet.size()+")");
            AutoCompleteTextView tView=binding.orderList;
            orderNumbers = new String[orderNumbersSet.size()];
            int idx=0;
            for(Long ord:orderNumbersSet) {orderNumbers[idx] = ord.toString(); idx++;}
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,orderNumbers);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onOrderSelect(tView,i));
            onOrderSelect(tView,0);
        }catch (Exception e){e.printStackTrace();}
    }

    private void onOrderSelect(AutoCompleteTextView tView,int idx){
        try {
            tView.setText(String.valueOf(orderNumbers[idx]),false);
            loadDoNumbers(Long.parseLong(orderNumbers[idx]));
        }catch (Exception e){e.printStackTrace();}
    }

    String[] doNumbers;
    private void loadDoNumbers(Long orderNumber){
        try {
            HashSet<String> doNumberSet = orderDosMap.get(orderNumber);
            binding.doListLayout.setHint("DO Numbers ("+doNumberSet.size()+")");
            AutoCompleteTextView tView=binding.doList;
            doNumbers = new String[doNumberSet.size()];
            int idx=0;
            for(String d:doNumberSet) {doNumbers[idx] = d; idx++;}
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,doNumbers);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onDoNumberSelect(tView,i));
            onDoNumberSelect(tView,0);
        }catch (Exception e){e.printStackTrace();}
    }

    private void onDoNumberSelect(AutoCompleteTextView tView,int idx){
        try {
            tView.setText(doNumbers[idx],false);
            loadItemListRecycleView(doNumbers[idx]);
        }catch (Exception e){e.printStackTrace();}
    }

    private void loadItemListRecycleView(String doNumber){
        ArrayList<MoveOrderConfirmedLineDto> items = doItemsMap.get(doNumber);
        binding.itemListHeader.setText("Items ("+items.size()+")");
        RecyclerView recyclerView = binding.deliveryLinesList;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DeliveryLineAdapter adapter = new DeliveryLineAdapter(this,items);
        recyclerView.setAdapter(adapter);
    }

    public void showReportView(){
        reportDialog.showReport();
    }

}