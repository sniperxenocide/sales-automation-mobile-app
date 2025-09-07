package com.akg.akg_sales.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.akg.akg_sales.dto.delivery.DeliveryPermission;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.User;
import com.akg.akg_sales.dto.order.OrderPermission;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CommonUtil {

    public static User loggedInUser = null;
    public static List<CustomerDto> customers;
    public static ArrayList<OrderStatusDto> statusList;
    public static HashMap<Long,ArrayList<CartItemDto>> orderCart = new HashMap<>();
    public static OrderPermission orderPermission;
    public static DeliveryPermission deliveryPermission;

    public static String deviceModel;
    public static String deviceId;
    public static String devicePhone;
    public static Location gpsLocation;
    public static String gpsAddress;
    public static String appVersion;

    public static void printCart(){
        for (Long k: orderCart.keySet()){
            System.out.println(k+" "+orderCart.get(k).get(0).getCustomerDto().getCustomerName());
            for (CartItemDto c: orderCart.get(k)){
                System.out.println("***["+c.getItemDto().getItemDescription()+" = "+c.getQuantity()+"],");
            }
        }
    }

    public static JSONObject getDeviceInfoJson(){
        JSONObject deviceInfo = new JSONObject();
        try {
            deviceInfo.put("deviceModel",CommonUtil.deviceModel);
            deviceInfo.put("deviceId",CommonUtil.deviceId);
            deviceInfo.put("devicePhone",CommonUtil.devicePhone);
            if(gpsLocation !=null){
                deviceInfo.put("gpsLocation", gpsLocation.getLatitude()+","+gpsLocation.getLongitude());
                deviceInfo.put("gpsAddress",gpsAddress);
            }
        }catch (Exception e){e.printStackTrace();}
        return deviceInfo;
    }

    public static void showToast(Context context, String msg, boolean isSuccess){
        try{
            String red = "#f54248";
            String color = "#0bbf32";  //green
            if(!isSuccess) color = red;
            Toast toast = Toast.makeText(context, Html.fromHtml("<font color='"+color+"' ><b>" + msg + "</b></font>"), Toast.LENGTH_LONG);
            toast.show();
        }catch (Exception e){
            System.out.println("***** Exception in ShowToast() Function");
        }
    }

    public static void setDialogWindowParams(Context context, Dialog dialog){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(0.95*displayMetrics.widthPixels);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static ProgressDialog showProgressDialog(Context context){
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public static String decimalToAccounting(Double val){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("##,##,##,###.##");
        return formatter.format(val);
    }

    public static String getFormattedDateTime(String dateTime){
        if(dateTime==null || dateTime.isEmpty()) return "--";
        try {
            Date dt=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateTime);
            return (new SimpleDateFormat("dd-MMM-yyyy hh:mm a")).format(dt);
        }catch (Exception e){e.printStackTrace();}
        return dateTime;
    }

    public static void setFirebaseUserId(){
        if(CommonUtil.loggedInUser!=null)
            FirebaseCrashlytics.getInstance().setUserId(CommonUtil.loggedInUser.getUsername());
    }
}
