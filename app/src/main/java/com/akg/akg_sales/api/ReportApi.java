package com.akg.akg_sales.api;

import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.report.ReportDto;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ReportApi {

    @GET("/report-service/api/reports")
    Call<PageResponse<ReportDto>> getReportList();

    @GET("/report-service/api/report-data")
    Call<ResponseBody> getReportData(@QueryMap Map<String,String> filter);

    @GET("/report-service/api/modules")
    Call<PageResponse<ReportDto>> getReportModules();
}
