package com.akg.akg_sales.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeliveryOrderDto {
    private Long id;
    private Long orgId;
    private Long customerId;
    private String customerNumber;
    private String customerName;
    private String doNumber;
    private String doDate;
    private Long doHdrId;
    private Long orderNumber;
    private Long orderHeaderId;
    private Long orderLineId;
    private String parentLineNumber;
    private Long inventoryItemId;
    private String itemCode;
    private String itemDescription;
    private String uomCode;
    private Double doQuantity;
}
