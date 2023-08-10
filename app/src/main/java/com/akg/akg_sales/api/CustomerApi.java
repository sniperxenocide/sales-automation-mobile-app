package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.CustomerSiteDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CustomerApi {

    @GET("/access-control-service/api/customer/user/all")
    Call<List<CustomerDto>> getCustomersForUser();

    @GET("/access-control-service/api/customer/site")
    Call<List<CustomerSiteDto>> getCustomerSites(@Query("customerId") Long customerId,@Query("siteUseCode") String siteUseCode);
}
