package com.akg.akg_sales.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class OrderDto {
    private Long id;
    private String orderNumber;
    private String creationTime;
    private String customerNumber;
    private String customerName;
    private String salesDeskName;
    private String marketSegmentName;
    private String salesEmployeeName;
    private String status;
    private Double value;
}
