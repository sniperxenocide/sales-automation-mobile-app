package com.akg.akg_sales.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.webkit.CookieManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.databinding.DataBindingUtil;

import com.akg.akg_sales.BuildConfig;
import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.databinding.ActivityHomeBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.HomeMenuItem;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.service.CommonService;
import com.akg.akg_sales.service.CustomerService;
import com.akg.akg_sales.service.DeliveryService;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.delivery.DeliveryListActivity;
import com.akg.akg_sales.view.activity.order.OrderActivity;
import com.akg.akg_sales.view.activity.order.PendingOrderActivity;
import com.akg.akg_sales.view.activity.payment.PaymentListActivity;
import com.akg.akg_sales.view.adapter.HomeMenuAdapter;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.view.dialog.GeneralDialog;
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

        CommonService.fetchHomepagePermission(this,permission->{
            if(permission.getCmsAccess()) list.add(new HomeMenuItem("CMS",R.drawable.justice, ComplainManagementActivity.class));
            if(permission.getReportAccess()) list.add(new HomeMenuItem("Report",R.drawable.report, ReportWebActivity.class));

            adapter.notifyDataSetChanged();
        });
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

    private void setWelcomeMsg(){
        String msg = "Welcome "+CommonUtil.loggedInUser.getUsername();
        homeBinding.welcomeUserText.setText(msg);
    }

    @Override
    public void onBackPressed(){
        new ConfirmationDialog(this,"Do you want to logout?", m-> finish());
    }




    public void onClickResetPasswordBtn(){
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        this.startActivity(intent);
    }

    private void fetchDeliveryPermission(){
        DeliveryService.fetchDeliveryPermission(permission-> CommonUtil.deliveryPermission = permission);
    }

}