package com.akg.akg_sales.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.akg.akg_sales.BuildConfig;
import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.DeliveryApi;
import com.akg.akg_sales.api.HomeApi;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.databinding.ActivityHomeBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.HomeMenuItem;
import com.akg.akg_sales.dto.HomepagePermission;
import com.akg.akg_sales.dto.delivery.DeliveryPermission;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.service.CustomerService;
import com.akg.akg_sales.service.DeliveryService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.util.SPHelper;
import com.akg.akg_sales.view.activity.delivery.DeliveryListActivity;
import com.akg.akg_sales.view.activity.order.PendingOrderActivity;
import com.akg.akg_sales.view.activity.payment.PaymentListActivity;
import com.akg.akg_sales.view.adapter.HomeMenuAdapter;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {
    Gson gson = new Gson();
    ActivityHomeBinding homeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFirebaseUserId();
        fetchCustomerListForUser();
        fetchOrderStatusFromServer();
        homeBinding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        homeBinding.setActivity(this);
        homeBinding.executePendingBindings();
        setAppVersion();
        setWelcomeMsg();
        fetchDeliveryPermission();
        generateHomeMenu();
        enableBackPressLogoutDialog();

        if(CommonUtil.loggedInUser.getLoginCount()<=1) onClickResetPasswordBtn();
    }

    private void generateHomeMenu(){
        ArrayList<HomeMenuItem> list = new ArrayList<>();
        list.add(new HomeMenuItem("Order",R.drawable.order,PendingOrderActivity.class));
        list.add(new HomeMenuItem("Delivery",R.drawable.delivery2,DeliveryListActivity.class));
        list.add(new HomeMenuItem("Payment",R.drawable.payment,PaymentListActivity.class));
        list.add(new HomeMenuItem("Reset",R.drawable.reset_password, ResetPasswordActivity.class));

        HomeMenuAdapter adapter = new HomeMenuAdapter(this,list);
        homeBinding.homeGrid.setAdapter(adapter);
        fetchHomepagePermission(list,adapter);
    }

    private void fetchHomepagePermission(ArrayList<HomeMenuItem> list,HomeMenuAdapter adapter){
        if(!CommonUtil.shouldCallApiAfterInterval(this, SPHelper.KEY_NEXT_HOMEPAGE_PERMISSION_FETCH_TIMESTAMP)) {
            HomepagePermission permission = SPHelper.getDataFromSharedPref(this,
                    SPHelper.MASTER_DATA_PREF, SPHelper.KEY_HOMEPAGE_PERMISSION,HomepagePermission.class);
            if(permission!=null) {
                applyHomepagePermission(permission,list,adapter);
                return;
            }
        }

        ProgressDialog progressDialog = CommonUtil.showProgressDialog(this);
        API.getClient().create(HomeApi.class).getHomepagePermission()
                .enqueue(API.getCallback(this, permission->{
                    applyHomepagePermission(permission,list,adapter);
                    CommonUtil.setNextApiCallTimestamp(this, SPHelper.KEY_NEXT_HOMEPAGE_PERMISSION_FETCH_TIMESTAMP,10,16);
                    SPHelper.storeDataInSharedPref(HomeActivity.this, SPHelper.MASTER_DATA_PREF, SPHelper.KEY_HOMEPAGE_PERMISSION,permission);
                },e->{}, progressDialog));
    }

    private void applyHomepagePermission(HomepagePermission permission,ArrayList<HomeMenuItem> list,HomeMenuAdapter adapter){
        if(permission.getCmsAccess()) list.add(new HomeMenuItem("CMS",R.drawable.justice, ComplainManagementActivity.class));
        if(permission.getReportAccess()) list.add(new HomeMenuItem("Report",R.drawable.report, ReportWebActivity.class));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("App is Closing.*****************************");
        storeCart();
    }

    private void loadCart(){
        try {
            SharedPreferences sp = getSharedPreferences("cart", Context.MODE_PRIVATE);
            Type type = new TypeToken<HashMap<Long, ArrayList<CartItemDto>>>(){}.getType();
            CommonUtil.orderCart = gson.fromJson(sp.getString("cart",""), type);
            if(CommonUtil.orderCart==null) CommonUtil.orderCart = new HashMap<>();
            System.out.println("Cart Loaded... "+CommonUtil.orderCart);
            updateCartForCurrentUser();
        }catch (Exception e){System.out.println(e.getMessage());}
    }

    private void updateCartForCurrentUser(){
        Set<Long> customerIds = CommonUtil.orderCart.keySet();
        for(Long id:customerIds){
            boolean idFound = false;
            for(CustomerDto c:CommonUtil.customers){
                if(c.getId().longValue()==id) {
                    idFound=true; break;
                }
            }
            if(!idFound) CommonUtil.orderCart.remove(id);
        }
    }

    private void storeCart(){
        try {
            String cartStr = gson.toJson(CommonUtil.orderCart);
            System.out.println("Storing Cart: "+cartStr);
            SharedPreferences sp = getSharedPreferences("cart", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("cart",cartStr);
            editor.apply();
        }catch (Exception e){System.out.println(e.getMessage());}
    }

    private void fetchCustomerListForUser(){
        CustomerService.fetchCustomerListForUser(this,customers->{
            CommonUtil.customers = customers;
            if(CommonUtil.customers!=null && !CommonUtil.customers.isEmpty()){
                Collections.sort(CommonUtil.customers,
                        (object1, object2) -> object1.getCustomerName()
                                .compareTo(object2.getCustomerName()));}
            loadCart();
        });
    }

    private void fetchOrderStatusFromServer(){
        if(!CommonUtil.shouldCallApiAfterInterval(this, SPHelper.KEY_NEXT_ORDER_STATUS_FETCH_TIMESTAMP)) {
            Type type = new TypeToken<ArrayList<OrderStatusDto>>() {}.getType();
            CommonUtil.statusList = SPHelper.getDataFromSharedPref(this,
                    SPHelper.MASTER_DATA_PREF, SPHelper.KEY_ORDER_STATUS_LIST,type);
            if(CommonUtil.statusList!=null) return;
        }

        ProgressDialog progressDialog = CommonUtil.showProgressDialog(this);
        API.getClient().create(OrderApi.class).getOrderStatus()
                .enqueue(API.getCallback(this,list->{
                    CommonUtil.statusList = (ArrayList<OrderStatusDto>)list;
                    CommonUtil.setNextApiCallTimestamp(this, SPHelper.KEY_NEXT_ORDER_STATUS_FETCH_TIMESTAMP,25,31);
                    SPHelper.storeDataInSharedPref(HomeActivity.this, SPHelper.MASTER_DATA_PREF, SPHelper.KEY_ORDER_STATUS_LIST,CommonUtil.statusList);
                },e->{},progressDialog));
    }

    private void setAppVersion(){
        homeBinding.appVersion.setText("v"+BuildConfig.VERSION_NAME);
    }

    private void setWelcomeMsg(){
        String msg = "Welcome "+CommonUtil.loggedInUser.getUsername();
        homeBinding.welcomeUserText.setText(msg);
    }

    private void enableBackPressLogoutDialog(){
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new ConfirmationDialog(HomeActivity.this, "Do you want to logout?", m -> finish());
            }
        });
    }

    public void onClickResetPasswordBtn(){
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        this.startActivity(intent);
    }

    private void fetchDeliveryPermission(){
        if(!CommonUtil.shouldCallApiAfterInterval(this, SPHelper.KEY_NEXT_DELIVERY_PERMISSION_FETCH_TIMESTAMP)) {
            CommonUtil.deliveryPermission = SPHelper.getDataFromSharedPref(this, SPHelper.MASTER_DATA_PREF,
                    SPHelper.KEY_DELIVERY_PERMISSION,DeliveryPermission.class);
            if(CommonUtil.deliveryPermission!=null) return;
        }

        API.getClient().create(DeliveryApi.class).getDeliveryPermission()
                .enqueue(API.getCallback(this,permission->{
                    CommonUtil.deliveryPermission = permission;
                    CommonUtil.setNextApiCallTimestamp(this, SPHelper.KEY_NEXT_DELIVERY_PERMISSION_FETCH_TIMESTAMP,10,16);
                    SPHelper.storeDataInSharedPref(HomeActivity.this, SPHelper.MASTER_DATA_PREF, SPHelper.KEY_DELIVERY_PERMISSION,CommonUtil.deliveryPermission);
                },e->{}, null));
    }

}