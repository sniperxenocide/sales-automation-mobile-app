package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderPermission;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.dto.order.OrderTypeDto;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface OrderApi {
    @GET("/order-service/api/order/all")
    Call<PageResponse<OrderDto>> getAllOrders(@QueryMap Map<String,String> filter);

    @GET("/order-service/api/order/detail")
    Call<OrderDto> getOrderDetail(@Query("orderId") String orderId);

    @POST("/order-service/api/order/create")
    Call<OrderDto> createOrder(@Body OrderRequest body);

    @POST("/order-service/api/order/attachment")
    Call<OrderDto> sendOrderAttachment(@Body MultipartBody body);

    @POST("/order-service/api/order/approve")
    Call<OrderDto> approveOrder(@Body OrderRequest body);

    @POST("/order-service/api/order/cancel")
    Call<OrderDto> cancelOrder(@Query("orderId") String orderId);

    @GET("/order-service/api/order/status/all")
    Call<List<OrderStatusDto>> getOrderStatus();

    @GET("/order-service/api/order/type/all")
    Call<List<OrderTypeDto>> getOrderTypes();

    @GET("/access-control-service/api/order/permission")
    Call<OrderPermission> getOrderPermission();
}
