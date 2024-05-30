package com.akg.akg_sales.service;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.core.util.Consumer;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.ExternalApplicationApi;
import com.akg.akg_sales.api.HomeApi;
import com.akg.akg_sales.dto.ExternalApplicationConfig;
import com.akg.akg_sales.dto.HomepagePermission;
import com.akg.akg_sales.util.CommonUtil;

import java.lang.reflect.Method;

public class CommonService {

    public static void fetchHomepagePermission(Context context, Consumer<HomepagePermission> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(HomeApi.class).getHomepagePermission()
                .enqueue(API.getCallback(context, callback, progressDialog));
    }

    public static void fetchComplaintHandlingConfig(Context context, Consumer<ExternalApplicationConfig> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(context);
        API.getClient().create(ExternalApplicationApi.class).getCmsConfig()
                .enqueue(API.getCallback(context, callback, progressDialog));
    }
}


