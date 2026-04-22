package com.akg.akg_sales.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import java.lang.reflect.Type;

public class SPHelper {
    public static final String API_CALL_TIMESTAMP_PREF = "API_CALL_TIMESTAMP_PREFS";
    public static final String KEY_NEXT_APP_UPDATE_CHECK_TIMESTAMP = "KEY_NEXT_APP_UPDATE_CHECK_TIMESTAMP";
    public static final String KEY_NEXT_ORDER_STATUS_FETCH_TIMESTAMP = "KEY_NEXT_ORDER_STATUS_FETCH_TIMESTAMP";
    public static final String KEY_NEXT_DELIVERY_PERMISSION_FETCH_TIMESTAMP = "KEY_NEXT_DELIVERY_PERMISSION_FETCH_TIMESTAMP";
    public static final String KEY_NEXT_HOMEPAGE_PERMISSION_FETCH_TIMESTAMP = "KEY_NEXT_HOMEPAGE_PERMISSION_FETCH_TIMESTAMP";

    public static final String MASTER_DATA_PREF = "MASTER_DATA_PREF";
    public static final String KEY_ORDER_STATUS_LIST = "KEY_ORDER_STATUS_LIST";
    public static final String KEY_ORDER_TYPE_LIST = "KEY_ORDER_TYPE_LIST";
    public static final String KEY_ORDER_PERMISSION = "KEY_ORDER_PERMISSION";
    public static final String KEY_DELIVERY_PERMISSION = "KEY_DELIVERY_PERMISSION";
    public static final String KEY_HOMEPAGE_PERMISSION = "KEY_HOMEPAGE_PERMISSION";

    public static void storeDataInSharedPref(Context context, String prefName, String key, Object data) {
        Log.d("SharedPreferencesHelper", "storeDataInSharedPref: Storing Data in SharedPreferences PrefName: "+prefName+" Key: "+key+" Data: "+data);
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (data == null) {editor.remove(key).apply();return;}
        try {
            Gson gson = new Gson();
            String json = gson.toJson(data);
            editor.putString(key, json).apply();
        } catch (Exception e) {
            Log.e("SharedPreferencesHelper","storeDataInSharedPref: ",e);// or log properly
        }
    }

    public static <T> T getDataFromSharedPref(Context context, String prefName, String key, Type type) {
        try {
            Log.d("SharedPreferencesHelper", "getDataFromSharedPref: Getting Data from SharedPreferences PrefName: "+prefName+" Key: "+key);
            SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
            String json = prefs.getString(key, null);
            Log.d("SharedPreferencesHelper", "getDataFromSharedPref: Data: "+json);
            if (json == null) return null;
            return new Gson().fromJson(json, type);
        }catch (Exception e){
            Log.e("SharedPreferencesHelper","getDataFromSharedPref: ",e);// or log properly
        }
        return null;
    }
}
