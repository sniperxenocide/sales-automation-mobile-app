package com.akg.akg_sales.viewmodel;

import android.content.Intent;

import androidx.databinding.BaseObservable;

import com.akg.akg_sales.view.activity.HomeActivity;
import com.akg.akg_sales.view.activity.order.OrderActivity;
import com.akg.akg_sales.view.activity.notification.NotificationActivity;
import com.akg.akg_sales.view.activity.order.PendingOrderActivity;
import com.akg.akg_sales.view.activity.payment.NewPaymentActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.view.dialog.GeneralDialog;

public class HomeViewModel extends BaseObservable {
    private HomeActivity activity;
    public HomeViewModel(HomeActivity activity){
        this.activity=activity;
    }
    private String defaultMsg = "Service not Available";

    public void onClickNotificationBtn(){
//        Intent notificationIntent = new Intent(activity, NotificationActivity.class);
//        activity.startActivity(notificationIntent);
        new GeneralDialog(activity,defaultMsg);
    }

    public void onClickOrderBtn(){
        Intent pendingOrderIntent = new Intent(activity, PendingOrderActivity.class);
        activity.startActivity(pendingOrderIntent);
    }

    public void onClickDeliveryBtn(){
        new GeneralDialog(activity,defaultMsg);
    }

    public void onClickPaymentBtn(){
        Intent intent = new Intent(activity, NewPaymentActivity.class);
        activity.startActivity(intent);
    }
}



