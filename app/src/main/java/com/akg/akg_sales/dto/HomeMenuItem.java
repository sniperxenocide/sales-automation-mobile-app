package com.akg.akg_sales.dto;

import android.content.Context;
import android.content.Intent;

import com.akg.akg_sales.view.activity.order.PendingOrderActivity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class HomeMenuItem {
    private String title;
    private int icon;
    private Class<?> activityClass;
}
