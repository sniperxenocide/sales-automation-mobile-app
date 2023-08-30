package com.akg.akg_sales.api;

import com.akg.akg_sales.util.CommonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
//    public static String baseUrl = "http://10.10.144.31:5000";  // Dev
//    public static String baseUrl = "http://10.10.1.108:5000";  //Test Server

    //Test Server
    public static String baseUrl = "http://salesapp.test.abulkhairgroup.com:1111";

    //Production Server
//    public static String baseUrl = "http://salesapp.live.abulkhairgroup.com:1000";

    // Development Server
//    public static String baseUrl = "http://10.0.2.2:5000";

    //RSO Pahartali 01926670925
    //TSO Karnafully 01926670951

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
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
}
