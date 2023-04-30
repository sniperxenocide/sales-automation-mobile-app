package com.akg.akg_sales.viewmodel.order;

import android.content.Intent;

import androidx.databinding.BaseObservable;

import com.akg.akg_sales.view.activity.order.CartActivity;
import com.akg.akg_sales.view.activity.order.OrderActivity;
import com.akg.akg_sales.view.dialog.ItemFilterDialog;

public class OrderViewModel extends BaseObservable {
    public OrderActivity activity;
    public OrderViewModel(OrderActivity activity){
        this.activity=activity;
    }

    public void onClickFilter(){
        ItemFilterDialog dialog = new ItemFilterDialog(activity);
    }

    public void onClickCart(){
        Intent cartIntent = new Intent(activity, CartActivity.class);
        activity.startActivity(cartIntent);
    }
}
