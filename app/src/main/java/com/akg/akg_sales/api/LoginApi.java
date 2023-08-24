package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.AppVersion;
import com.akg.akg_sales.dto.ResetPasswordDto;
import com.akg.akg_sales.dto.User;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginApi {

    @POST("/auth-service/api/authenticate")
    Call<User> authenticate(@Body User body);

    @POST("/auth-service/api/reset-password")
    Call<User> resetPassword(@Body ResetPasswordDto body);

    @GET("/version-check")
    Call<AppVersion> getLatestVersion();

    @POST("/auth-service/api/fcm/token/update")
    Call<User> submitNewFcmToken(@Body RequestBody fcmToken);

}
