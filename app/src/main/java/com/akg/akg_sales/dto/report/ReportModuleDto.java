package com.akg.akg_sales.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReportModuleDto {
    private Long id;
    private String moduleName;
    private String description;
}
