package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.delivery.DeliveryAckRequestHeader;
import com.akg.akg_sales.dto.delivery.DeliveryPermission;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedHeaderDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.dto.order.OrderPermission;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface DeliveryApi {

    @GET("/notification-service/api/move-order/confirmed")
    Call<PageResponse<MoveOrderConfirmedHeaderDto>> getMoveOrderConfirmedHeaders(@QueryMap Map<String,String> filter);

    @GET("/notification-service/api/move-order/confirmed/lines")
    Call<List<MoveOrderConfirmedLineDto>> getMoveOrderConfirmedLines(@QueryMap Map<String,String> filter);

    @POST("/notification-service/api/move-order/acknowledge")
    Call<DeliveryAckRequestHeader> acknowledgeDelivery(@Body MultipartBody body);

    @GET("/notification-service/api/move-order/acknowledge")
    Call<DeliveryAckRequestHeader> getAcknowledgementStatus(@Query("movOrdHrdId") Long movOrdHrdId,@Query("customerNumber") String customerNumber);

    @GET("/access-control-service/api/delivery/permission")
    Call<DeliveryPermission> getDeliveryPermission();
}
