package com.akg.akg_sales.view.activity;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.akg.akg_sales.BuildConfig;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityHomeBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.service.CustomerService;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.delivery.DeliveryListActivity;
import com.akg.akg_sales.view.activity.order.PendingOrderActivity;
import com.akg.akg_sales.view.activity.payment.PaymentListActivity;
import com.akg.akg_sales.view.dialog.GeneralDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {
    Gson gson = new Gson();
    ActivityHomeBinding homeBinding;
    private String defaultMsg = "Service not Available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchCustomerListForUser();
        fetchOrderStatusFromServer();
        homeBinding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        homeBinding.setActivity(this);
        homeBinding.executePendingBindings();
        setAppVersion();

        if(CommonUtil.loggedInUser.getLoginCount()<=1) onClickResetPasswordBtn();
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
                                .compareTo(object2.getCustomerName()));
            }
            loadCart();
        });
    }

    private void fetchOrderStatusFromServer(){
        OrderService.fetchOrderStatusFromServer(this,orderStatus ->
                CommonUtil.statusList = (ArrayList<OrderStatusDto>)orderStatus);
    }

    private void setAppVersion(){
        try {
            homeBinding.appVersion.setText("v"+BuildConfig.VERSION_NAME);
        }catch (Exception e){e.printStackTrace();}
    }


    public void onClickReportBtn(){
//        Intent reportIntent = new Intent(this, ReportActivity.class);
//        this.startActivity(reportIntent);
    }

    public void onClickOrderBtn(){
        Intent pendingOrderIntent = new Intent(this, PendingOrderActivity.class);
        this.startActivity(pendingOrderIntent);
    }

    public void onClickDeliveryBtn(){
        Intent intent = new Intent(this, DeliveryListActivity.class);
        this.startActivity(intent);
    }

    public void onClickPaymentBtn(){
        Intent intent = new Intent(this, PaymentListActivity.class);
        this.startActivity(intent);
    }

    public void onClickResetPasswordBtn(){
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        this.startActivity(intent);
    }
}