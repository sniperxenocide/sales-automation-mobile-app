package com.akg.akg_sales.dto.order;

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
    private Long customerId;
    private String customerNumber;
    private String customerName;
    private String salesDeskName;
    private String marketSegmentName;
    private String salesEmployeeName;
    private String operatingUnit;
    private Long currentApproverUserId;
    private String currentApproverUsername;
    private String currentStatus;
    private Double value;
    private List<OrderLineDto> orderLines;
}
