package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.dto.order.OrderStatusDto;

import java.util.List;
import java.util.Map;

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

    @POST("/order-service/api/order/approve")
    Call<OrderDto> approveOrder(@Body OrderRequest body);

    @POST("/order-service/api/order/cancel")
    Call<OrderDto> cancelOrder(@Query("orderId") String orderId);

    @GET("/order-service/api/order/status/all")
    Call<List<OrderStatusDto>> getOrderStatus();
}
