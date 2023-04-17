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
    private static Retrofit retrofit = null;
    private static String noAuth = "/auth-service/api/authenticate";
    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    String url = chain.request().url().encodedPath();
                    if(noAuth.equalsIgnoreCase(url)){
                        System.out.println("**************No Auth");
                        return chain.proceed(chain.request());
                    }
                    else if (CommonUtil.loggedInUser==null || CommonUtil.loggedInUser.getToken().isEmpty()){
                        return null;
                    }
                    else {
                        String token = "Bearer "+CommonUtil.loggedInUser.getToken();
                        Request newRequest  = chain.request().newBuilder()
                                .addHeader("Authorization", token )
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build();
        return retrofit;
    }
}
