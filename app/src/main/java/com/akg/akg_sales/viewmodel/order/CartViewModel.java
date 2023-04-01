package com.akg.akg_sales.viewmodel.order;

import android.content.Intent;

import androidx.databinding.BaseObservable;

import com.akg.akg_sales.view.activity.order.CartActivity;
import com.akg.akg_sales.view.activity.order.OrderActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;

public class CartViewModel extends BaseObservable {
    CartActivity activity;
    public CartViewModel(CartActivity activity){
        this.activity=activity;
    }

    public void onClickSubmitBtn(){
        new ConfirmationDialog(activity, "Submit Order?", a->{

        });
    }
}
