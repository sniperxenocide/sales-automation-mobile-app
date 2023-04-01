package com.akg.akg_sales.viewmodel;

import android.content.Intent;

import androidx.databinding.BaseObservable;

import com.akg.akg_sales.view.activity.HomeActivity;
import com.akg.akg_sales.view.activity.order.OrderActivity;
import com.akg.akg_sales.view.activity.notification.NotificationActivity;
import com.akg.akg_sales.view.activity.order.PendingOrderActivity;

public class HomeViewModel extends BaseObservable {
    private HomeActivity activity;
    public HomeViewModel(HomeActivity activity){
        this.activity=activity;
    }

    public void onClickNotificationBtn(){
        Intent notificationIntent = new Intent(activity, NotificationActivity.class);
        activity.startActivity(notificationIntent);
    }

    public void onClickOrderBtn(){
        Intent pendingOrderIntent = new Intent(activity, PendingOrderActivity.class);
        activity.startActivity(pendingOrderIntent);
    }
}



