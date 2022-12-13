package com.akg.akg_sales.dto.notification;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OrderBookedHeaderDto {
    private Long id;
    private Long orderNumber;
    private Double orderValue;
    private String business;
    private String customerNumber;
    private String customerName;
    private String division;
    private String region;
    private String territory;
    private String phoneNumber;
    private String bookedDate;

    private List<OrderBookedLineDto> orderLines;
}
