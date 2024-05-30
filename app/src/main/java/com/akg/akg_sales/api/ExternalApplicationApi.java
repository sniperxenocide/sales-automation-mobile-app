package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.ExternalApplicationConfig;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ExternalApplicationApi {

    @GET("/access-control-service/api/external-app/cms-config")
    Call<ExternalApplicationConfig> getCmsConfig();
}
