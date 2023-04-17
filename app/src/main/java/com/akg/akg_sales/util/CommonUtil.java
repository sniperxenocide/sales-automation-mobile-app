package com.akg.akg_sales.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.User;

import java.util.List;

public class CommonUtil {

    public static User loggedInUser = null;
    public static List<CustomerDto> customers;

    public static void showToast(Context context, String msg, boolean isSuccess){
        try{
            String red = "#6e3436";
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
}
