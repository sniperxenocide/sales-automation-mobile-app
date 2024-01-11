package com.akg.akg_sales.api;

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
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
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

//    public static String baseUrl = testServerLocalAccess;
//    public static String baseUrl = cgdProdServerPublicAccess;
    public static String baseUrl = ceramicProdServerPublicAccess;

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    String token = "";
                    if(CommonUtil.loggedInUser!=null && !CommonUtil.loggedInUser.getToken().isEmpty())
                        token = "Bearer "+CommonUtil.loggedInUser.getToken();
                    Request newRequest  = chain.request().newBuilder()
                            .addHeader("Authorization", token ).build();
                    return chain.proceed(newRequest);
                }).retryOnConnectionFailure(false).build();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build();
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
