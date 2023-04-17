package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.OrderDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OrderApi {
    @GET("/order-service/api/order/all")
    Call<List<OrderDto>> getAllOrders();

    @GET("/order-service/api/order/detail")
    Call<OrderDto> getOrderDetail(@Query("orderId") String orderId);
}
