package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.CustomerDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CustomerApi {

    @GET("/access-control-service/api/customer/user/all")
    Call<List<CustomerDto>> getCustomersForUser();
}
