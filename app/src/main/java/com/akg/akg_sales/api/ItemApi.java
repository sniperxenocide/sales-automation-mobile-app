package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.dto.item.ItemMaster;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ItemApi {

    @GET("/access-control-service/api/item/order-creation")
    Call<List<ItemDto>> getOrderItems(@QueryMap Map<String,String> filter);

    @GET("/access-control-service/api/item/master")
    Call<ItemMaster> getItemMaster(@Query("customerId") Long customerId);
}
