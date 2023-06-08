package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.payment.PaymentAccountDto;
import com.akg.akg_sales.dto.payment.PaymentMasterDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface PaymentApi {

    @GET("/payment-service/api/payment/master")
    Call<PaymentMasterDto> getPaymentMaster(@Query("operatingUnitId") String operatingUnitId);

    @POST("/payment-service/api/payment/create")
    Call<String> createPayment(@Body MultipartBody body);
}
