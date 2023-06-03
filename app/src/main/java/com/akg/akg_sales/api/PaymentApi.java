package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.payment.PaymentAccountDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PaymentApi {

    @GET("/payment-service/api/payment/accounts")
    Call<List<PaymentAccountDto>> getPaymentAccounts(@Query("operatingUnitId") String operatingUnitId);
}
