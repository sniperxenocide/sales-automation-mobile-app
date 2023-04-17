package com.akg.akg_sales.viewmodel.order;

import android.content.Intent;

import androidx.databinding.BaseObservable;

import com.akg.akg_sales.view.activity.order.OrderActivity;
import com.akg.akg_sales.view.activity.order.PendingOrderActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;

public class PendingOrderViewModel extends BaseObservable {
    PendingOrderActivity activity;

    public PendingOrderViewModel(PendingOrderActivity activity){
        this.activity=activity;
    }

    public void onClickNewOrder(){
        Intent orderIntent = new Intent(activity, OrderActivity.class);
        activity.startActivity(orderIntent);
    }

    public void onClickFilter(){

    }
}
