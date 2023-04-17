package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.UserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {

    @POST("/auth-service/api/authenticate")
    Call<UserDto> authenticate(@Body UserDto body);

}
