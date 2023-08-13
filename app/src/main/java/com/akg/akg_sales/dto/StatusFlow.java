package com.akg.akg_sales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class StatusFlow {
    private Integer sequence;
    private Integer passed;
    private String status;

    // -1 = Yet to Pass
    // 0 = Currently Pending in this Status
    // 1 = Passed
}
