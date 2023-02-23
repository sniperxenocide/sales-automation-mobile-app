package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.notification.DeliveryConfirmedHeaderDto;
import com.akg.akg_sales.dto.notification.OrderBookedHeaderDto;
import com.akg.akg_sales.dto.notification.PaymentDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NotificationApi {

    @GET("/notification-service/api/order-booked/all")  //@Query("customerNumber") String customerNumber
    Call<PageResponse<OrderBookedHeaderDto>> getOrderBooked();

    @GET("/notification-service/api/order-booked/one")
    Call<OrderBookedHeaderDto> getOrderBookedDetail(@Query("id") String id);

    @GET("/notification-service/api/delivery-confirmed/all")
    Call<PageResponse<DeliveryConfirmedHeaderDto>> getDeliveryConfirmed();

    @GET("/notification-service/api/delivery-confirmed/one")
    Call<DeliveryConfirmedHeaderDto> getDeliveryConfirmedDetail(@Query("id") String id);

    @GET("/notification-service/api/payment/all")
    Call<PageResponse<PaymentDto>> getAllPaymentCleared();

}
