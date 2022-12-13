package com.akg.akg_sales.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeliveryConfirmedLineDto {
    private Long id;
    private Long orderNumber;
    private String orderedDate;
    private String doNumber;
    private String doDate;
    private String doStatus;
    private Long orderedItemId;
    private String itemCode;
    private String itemShortName;
    private Double quantity;
    private String uom;
    private String rowCreationTime;
}
