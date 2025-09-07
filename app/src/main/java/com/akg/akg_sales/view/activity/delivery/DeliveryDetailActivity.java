package com.akg.akg_sales.view.activity.delivery;

import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityDeliveryDetailBinding;
import com.akg.akg_sales.dto.delivery.DeliveryAckRequestHeader;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedHeaderDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.service.DeliveryService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.delivery.DeliveryLineAdapter;
import com.akg.akg_sales.view.dialog.DeliveryAcknowledgeDialog;
import com.akg.akg_sales.view.dialog.DeliveryDetailReportDialog;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DeliveryDetailActivity extends AppCompatActivity {
    private static final String TAG = "DeliveryDetailActivity";
    ActivityDeliveryDetailBinding binding;
    public MoveOrderConfirmedHeaderDto moveConfirmedHeaderDto;
    public List<MoveOrderConfirmedLineDto> deliveryLines;

    public Hashtable<String,HashSet<Long>> customerOrdersMap = new Hashtable<>();
    public Hashtable<Long,HashSet<String>> orderDosMap = new Hashtable<>();
    public Hashtable<String,ArrayList<MoveOrderConfirmedLineDto>> doItemsMap = new Hashtable<>();
    public Hashtable<String,Integer> dataFrequency = new Hashtable<>();

    DeliveryDetailReportDialog reportDialog;
    DeliveryAcknowledgeDialog deliveryAcknowledgeDialog;
    DeliveryAckRequestHeader deliveryAckRequestHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFirebaseUserId();
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
        }catch (Exception e){Log.e(TAG, "Error: ",e );}

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
            }catch (Exception e){Log.e(TAG, "Error: ",e );}
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
        }catch (Exception e){Log.e(TAG, "Error: ",e );}
    }

    String[] customerNames;
    String selectedCustomer="";
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
        }catch (Exception e){Log.e(TAG, "Error: ",e );}
    }

    private void onCustomerSelect(AutoCompleteTextView tView,int idx){
        try {
            tView.setText(customerNames[idx],false);
            selectedCustomer = customerNames[idx];
            if(CommonUtil.deliveryPermission.getCanViewDeliveryReceiving()) fetchAcknowledgementStatus();
            loadOrderNumbers(customerNames[idx]);
        }catch (Exception e){Log.e(TAG, "Error: ",e );}
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
        }catch (Exception e){Log.e(TAG, "Error: ",e );}
    }

    private void onOrderSelect(AutoCompleteTextView tView,int idx){
        try {
            tView.setText(String.valueOf(orderNumbers[idx]),false);
            loadDoNumbers(Long.parseLong(orderNumbers[idx]));
        }catch (Exception e){Log.e(TAG, "Error: ",e );}
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
        }catch (Exception e){Log.e(TAG, "Error: ",e );}
    }

    private void onDoNumberSelect(AutoCompleteTextView tView,int idx){
        try {
            tView.setText(doNumbers[idx],false);
            loadItemListRecycleView(doNumbers[idx]);
        }catch (Exception e){Log.e(TAG, "Error: ",e );}
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

    public void doReceivingDialog(){
        if(deliveryAckRequestHeader==null
                && !CommonUtil.deliveryPermission.getCanSubmitDeliveryReceiving())
            return;

        deliveryAcknowledgeDialog = new DeliveryAcknowledgeDialog(this,selectedCustomer,deliveryAckRequestHeader);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                Uri uri = data.getData();
                assert uri != null;
                deliveryAcknowledgeDialog.onAttachmentSelected(uri.getPath());
            }catch (Exception e){
                Log.e(TAG, "onActivityResult: ",e);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAcknowledgementStatus(){
        String customerNumber = selectedCustomer.split("\\(")[1].replace(")","");
        DeliveryService.checkAcknowledgementStatus(
                this,moveConfirmedHeaderDto.getMoveOrderHeaderId(),
                customerNumber, res -> {
                    Log.d("T", "Acknowledgement Status: "+res);
                    updateUIWithAcknowledgementStatus(res);
                });
    }

    public void updateUIWithAcknowledgementStatus(DeliveryAckRequestHeader ackRequestHeader){
        this.deliveryAckRequestHeader = ackRequestHeader;
        binding.rcvStatusBox.setVisibility(VISIBLE);
        if(ackRequestHeader==null) {
            if(CommonUtil.deliveryPermission.getCanSubmitDeliveryReceiving()){
                binding.deliveryAck.setText("RECEIVE");
                binding.deliveryAck.setBackgroundResource(R.drawable.red_background);
                binding.rcvStatusTxt.setText("NOT RECEIVED");
                binding.rcvStatusTxt.setTextColor(Color.RED);
            }
            else {
                binding.deliveryAck.setText("");
                binding.deliveryAck.setBackgroundResource(R.drawable.red_background);
                binding.rcvStatusTxt.setText("NOT RECEIVED");
                binding.rcvStatusTxt.setTextColor(Color.RED);
            }
        }
        else {
            binding.deliveryAck.setText("VIEW");
            if(ackRequestHeader.getFullReceiving()){
                binding.deliveryAck.setBackgroundResource(R.drawable.deep_green_background);
                binding.rcvStatusTxt.setText("FULL RECEIVE");
                binding.rcvStatusTxt.setTextColor(Color.parseColor("#6dbd8b"));
            }
            else {
                binding.deliveryAck.setBackgroundResource(R.drawable.yellow_background);
                binding.rcvStatusTxt.setText("PARTIAL RECEIVE");
                binding.rcvStatusTxt.setTextColor(Color.parseColor("#f58d78"));
            }
        }
    }

}