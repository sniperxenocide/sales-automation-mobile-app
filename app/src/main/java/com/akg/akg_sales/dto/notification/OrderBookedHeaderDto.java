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
    private String operatingUnit;
    private String customerNumber;
    private String customerName;
    private String bookedDate;
    private String orderedDate;
    private List<OrderBookedLineDto> orderLines;
}
