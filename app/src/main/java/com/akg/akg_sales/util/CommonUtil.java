package com.akg.akg_sales.util;

import android.content.Context;
import android.text.Html;
import android.widget.Toast;

import com.akg.akg_sales.dto.UserDto;

public class CommonUtil {

    public static UserDto loggedInUser = null;

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
}
