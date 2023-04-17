package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {

    @POST("/auth-service/api/authenticate")
    Call<User> authenticate(@Body User body);

}
