package com.akg.akg_sales.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SPHelper {
    public static final String API_CALL_TIMESTAMP_PREF = "API_CALL_TIMESTAMP_PREFS";
    public static final String MASTER_DATA_PREF = "MASTER_DATA_PREF";

    public static final String KEY_NEXT_APP_UPDATE_CHECK_TIMESTAMP = "KEY_NEXT_APP_UPDATE_CHECK_TIMESTAMP";
    public static final String KEY_NEXT_ORDER_STATUS_FETCH_TIMESTAMP = "KEY_NEXT_ORDER_STATUS_FETCH_TIMESTAMP";
    public static final String KEY_NEXT_DELIVERY_PERMISSION_FETCH_TIMESTAMP = "KEY_NEXT_DELIVERY_PERMISSION_FETCH_TIMESTAMP";
    public static final String KEY_NEXT_HOMEPAGE_PERMISSION_FETCH_TIMESTAMP = "KEY_NEXT_HOMEPAGE_PERMISSION_FETCH_TIMESTAMP";
    public static final String KEY_NEXT_ORDER_PERMISSION_FETCH_TIMESTAMP = "KEY_NEXT_ORDER_PERMISSION_FETCH_TIMESTAMP";
    public static final String KEY_NEXT_ORDER_TYPE_FETCH_TIMESTAMP = "KEY_NEXT_ORDER_TYPE_FETCH_TIMESTAMP";

    public static final String KEY_ORDER_STATUS_LIST = "KEY_ORDER_STATUS_LIST";
    public static final String KEY_ORDER_TYPE_LIST = "KEY_ORDER_TYPE_LIST";
    public static final String KEY_ORDER_PERMISSION = "KEY_ORDER_PERMISSION";
    public static final String KEY_DELIVERY_PERMISSION = "KEY_DELIVERY_PERMISSION";
    public static final String KEY_HOMEPAGE_PERMISSION = "KEY_HOMEPAGE_PERMISSION";

    public static final Map<String, SpDataConfig> SP_DATA_CONFIG_MAP = Map.ofEntries(
            Map.entry(KEY_NEXT_APP_UPDATE_CHECK_TIMESTAMP, new SpDataConfig(false, 20L, 30L)),
            Map.entry(KEY_NEXT_ORDER_STATUS_FETCH_TIMESTAMP, new SpDataConfig(false, 20L, 30L)),
            Map.entry(KEY_NEXT_DELIVERY_PERMISSION_FETCH_TIMESTAMP, new SpDataConfig(true,10L, 15L)),
            Map.entry(KEY_NEXT_HOMEPAGE_PERMISSION_FETCH_TIMESTAMP, new SpDataConfig(true,10L, 15L)),
            Map.entry(KEY_NEXT_ORDER_PERMISSION_FETCH_TIMESTAMP, new SpDataConfig(true,10L, 15L)),
            Map.entry(KEY_NEXT_ORDER_TYPE_FETCH_TIMESTAMP, new SpDataConfig(false,20L, 30L)),

            Map.entry(KEY_ORDER_STATUS_LIST, new SpDataConfig(false)),
            Map.entry(KEY_ORDER_TYPE_LIST, new SpDataConfig(false)),
            Map.entry(KEY_ORDER_PERMISSION, new SpDataConfig(true)),
            Map.entry(KEY_DELIVERY_PERMISSION, new SpDataConfig(true)),
            Map.entry(KEY_HOMEPAGE_PERMISSION, new SpDataConfig(true))
    );

    public static void storeDataInSharedPref(Context context,String prefName,String key,Object data) {
        String newKey = getUserWiseKey(key);
        Log.d("SharedPreferencesHelper", "storeDataInSharedPref: Storing Data in SharedPreferences PrefName: "+prefName+" Key: "+newKey+" Data: "+data);
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (data == null) {editor.remove(newKey).apply();return;}
        try {
            Gson gson = new Gson();
            String json = gson.toJson(data);
            editor.putString(newKey, json).apply();
        } catch (Exception e) {
            Log.e("SharedPreferencesHelper","storeDataInSharedPref: ",e);// or log properly
        }
    }

    public static <T> T getDataFromSharedPref(Context context,String prefName,String key,Type type) {
        try {
            String newKey = getUserWiseKey(key);
            Log.d("SharedPreferencesHelper", "getDataFromSharedPref: Getting Data from SharedPreferences PrefName: "+prefName+" Key: "+newKey);
            SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
            String json = prefs.getString(newKey, null);
            Log.d("SharedPreferencesHelper", "getDataFromSharedPref: Data: "+json);
            if (json == null) return null;
            return new Gson().fromJson(json, type);
        }catch (Exception e){
            Log.e("SharedPreferencesHelper","getDataFromSharedPref: ",e);// or log properly
        }
        return null;
    }

    public static boolean shouldCallApiAfterInterval(Context context,String key) {
        String newKey = getUserWiseKey(key);
        SharedPreferences prefs = context.getSharedPreferences(SPHelper.API_CALL_TIMESTAMP_PREF, Context.MODE_PRIVATE);
        long nextTime = prefs.getLong(newKey,0L);
        Log.d("COMMON_UTIL", "shouldCallApiAfterInterval: PREF: "+ SPHelper.API_CALL_TIMESTAMP_PREF+" key: "+newKey+" value: "+nextTime);
        if (nextTime == 0L || System.currentTimeMillis() > nextTime) {
            Log.d("COMMON_UTIL", "shouldCallApiAfterInterval: API Call possible.");
            return true;
        }
        Log.d("COMMON_UTIL", "shouldCallApiAfterInterval: Can't Call API Now.");
        return false;
    }

    // Interval in Hour
    public static void setNextApiCallTimestamp(Context context,String key){
        SharedPreferences prefs = context.getSharedPreferences(SPHelper.API_CALL_TIMESTAMP_PREF, Context.MODE_PRIVATE);
        prefs.edit().putLong(getUserWiseKey(key),getNextTimestamp(key)).apply();
    }

    private static String getUserWiseKey(String key){
        SpDataConfig c = SP_DATA_CONFIG_MAP.get(key);
        return ((c!=null && c.isUserWise()) && CommonUtil.loggedInUser!=null) ?
                (key+"_"+CommonUtil.loggedInUser.getUsername()) : key;
    }

    private static long getNextTimestamp(String key){
        SpDataConfig config = SP_DATA_CONFIG_MAP.get(key);
        if(config==null) return 0;
        return System.currentTimeMillis() + (ThreadLocalRandom.current()
                .nextLong(config.intervalMinHr, config.intervalMaxHr)* 3600 * 1000);
    }

    @Getter @AllArgsConstructor
    public static class SpDataConfig{
        private boolean userWise;
        private long intervalMinHr;
        private long intervalMaxHr;
        public SpDataConfig(Boolean userWise){this(userWise, 1L, 2L);}
    }
}
