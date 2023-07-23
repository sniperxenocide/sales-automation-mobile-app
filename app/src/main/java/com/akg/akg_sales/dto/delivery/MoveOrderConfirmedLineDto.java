package com.akg.akg_sales.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class MoveOrderConfirmedLineDto {
    private Long id;
    private Long movOrdHdrId;
    private String movOrderNo;
    private String customerNumber;
    private String customerName;
    private Long orderNumber;
    private String orderedDate;
    private String doNumber;
    private String doDateTime;
    private String doConfirmDate;
    private String doStatus;
    private String itemCode;
    private String itemDescription;
    private Double lineQuantity;
    private Double shippedQty;
    private String uomCode;
    private String doWarehouseOrgName;
    private String shipToLocation;
    private String rowCreationTime;
}
