package com.akg.akg_sales.dto.notification;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryConfirmedHeaderDto {
    private Long id;
    private Long orgId;
    private String movOrderNo;
    private String vehicleNo;
    private String driverName;
    private String driverMobile;
    private String operatingUnit;
    private String movOrderTime;
    private String movOrderStatus;
    private String moveWarehouseOrgName;
    private String moveConfirmedDate;
    private String transporterName;
    private List<DeliveryConfirmedLineDto> deliveryConfirmedLines;
    private String rowCreationTime;

    //delivery location needed
}
