package com.akg.akg_sales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AppVersion {
    private int versionCode;
    private String versionName;
    private String description;
    private String releaseDate;
    private String downloadUrl;
}
