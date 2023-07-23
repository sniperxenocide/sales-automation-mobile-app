package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedHeaderDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DeliveryApi {

    @GET("/notification-service/api/move-order/confirmed")
    Call<PageResponse<MoveOrderConfirmedHeaderDto>> getMoveOrderConfirmedHeaders(@QueryMap Map<String,String> filter);

    @GET("/notification-service/api/move-order/confirmed/lines")
    Call<List<MoveOrderConfirmedLineDto>> getMoveOrderConfirmedLines(@QueryMap Map<String,String> filter);

}
