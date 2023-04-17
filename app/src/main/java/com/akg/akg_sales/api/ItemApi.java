package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.dto.item.ItemTypeDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ItemApi {

    @GET("/access-control-service/api/item/order-creation")
    Call<List<ItemDto>> getOrderItems(
            @Query("customerId") Long customerId,@Query("subTypeId") Long subTypeId);

    @GET("/access-control-service/api/item/order-item-type")
    Call<List<ItemTypeDto>> getItemTypes(@Query("customerId") Long customerId);
}
