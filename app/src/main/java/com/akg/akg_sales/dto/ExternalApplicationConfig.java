package com.akg.akg_sales.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalApplicationConfig implements Serializable {
    private String applicationName;
    private String applicationCode;
    private String baseUrl;
    private String urlParams;
}
