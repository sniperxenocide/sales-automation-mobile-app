package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.HomepagePermission;
import com.akg.akg_sales.dto.item.ItemDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface HomeApi {

    @GET("/access-control-service/api/app-ui/homepage-permission")
    Call<HomepagePermission> getHomepagePermission();
}
