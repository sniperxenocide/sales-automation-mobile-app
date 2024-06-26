package com.akg.akg_sales.api;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.core.util.Consumer;

import com.akg.akg_sales.util.CommonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    public static String devEnvUrl = "http://10.0.2.2:5000";      // Dev
    public static String testServerLocalAccess = "http://10.10.1.108:5000";  //Test Server
    public static String testServerPublicAccess = "http://salesapp.test.abulkhairgroup.com:1111";
    public static String cgdProdServerLocalAccess = "http://10.10.1.112:5000";  //Production Server
    public static String ceramicProdServerLocalAccess = "http://10.10.1.114:5000";  //Production Server
    public static String cgdProdServerPublicAccess = "http://salesapp.live.abulkhairgroup.com:1000";
    public static String ceramicProdServerPublicAccess = "http://akcilsalesapp.live.abulkhairgroup.com:3000";
    public static HashMap<String,String> baseUrlMap;

//    public static String baseUrl = devEnvUrl;

    public static String baseUrl = testServerLocalAccess;
//    public static String baseUrl = testServerPublicAccess;

//    public static String baseUrl = cgdProdServerPublicAccess;
//    public static String baseUrl = cgdProdServerLocalAccess;

//    public static String baseUrl = ceramicProdServerPublicAccess;
//    public static String baseUrl = ceramicProdServerLocalAccess;

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor).addInterceptor(chain -> {
                    String token = "";
                    if(CommonUtil.loggedInUser!=null && !CommonUtil.loggedInUser.getToken().isEmpty())
                        token = "Bearer "+CommonUtil.loggedInUser.getToken();
                    Request newRequest  = chain.request().newBuilder()
                            .addHeader("Authorization", token ).build();
                    return chain.proceed(newRequest);
                }).retryOnConnectionFailure(false).build();
        return new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
                .client(client).build();
    }

    public static <T>Callback<T> getCallback(Context context, Consumer<T> callback, ProgressDialog progress){
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                progress.dismiss();
                try {
                    if(response.code()==200){
                        callback.accept(response.body());
                    }
                    else throw new Exception(response.code()+"."+response.message());
                }catch (Exception e){
                    CommonUtil.showToast(context,e.getMessage(),false);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                progress.dismiss();
                call.cancel();
                CommonUtil.showToast(context,t.getMessage(),false);
            }
        };
    }

    public static String cgdBusinessLabel = "CGD";
    public static String ceramicBusinessLabel = "Ceramic";

    public static void loadServerUrl(){
        baseUrlMap = new HashMap<>();
//        loadServerUrlDevelopment();
        loadServerUrlTestLocalAccess();
//        loadServerUrlTest();
//        loadServerUrlProd();

    }

    private static void loadServerUrlDevelopment(){
        baseUrlMap.put(cgdBusinessLabel,devEnvUrl);
        baseUrlMap.put(ceramicBusinessLabel,devEnvUrl);
    }

    private static void loadServerUrlTestLocalAccess(){
        baseUrlMap.put(cgdBusinessLabel,testServerLocalAccess);
        baseUrlMap.put(ceramicBusinessLabel,testServerLocalAccess);
    }

    private static void loadServerUrlTest(){
        baseUrlMap.put(cgdBusinessLabel,testServerPublicAccess);
        baseUrlMap.put(ceramicBusinessLabel,testServerPublicAccess);
    }

    private static void loadServerUrlProd(){
        baseUrlMap.put(cgdBusinessLabel,cgdProdServerPublicAccess);
        baseUrlMap.put(ceramicBusinessLabel,ceramicProdServerPublicAccess);
    }
}
