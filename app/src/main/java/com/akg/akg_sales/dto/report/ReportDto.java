package com.akg.akg_sales.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReportDto {
    private Long id;
    private String reportName;
    private String reportModuleName;
    private String operatingUnit;
    private String reportUrl;
}
