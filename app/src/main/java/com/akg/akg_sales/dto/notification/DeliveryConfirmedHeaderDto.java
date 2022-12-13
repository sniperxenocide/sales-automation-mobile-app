package com.akg.akg_sales.dto.notification;

import java.time.LocalDateTime;
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
    private String business;
    private String movOrderNo;
    private String moStatus;
    private String confirmedDate;
    private String vehicleNo;
    private String driverName;
    private String driverMobile;
    private Long customerId;
    private String customerNumber;
    private String customerName;
    private String customerCategoryCode;
    private String customerPhone;
    private String division;
    private String region;
    private String territory;
    private String soPhoneNumber;
    private String rsoPhoneNumber;
    private String dmoPhoneNumber;
    private List<DeliveryConfirmedLineDto> deliveryConfirmedLines;
    private String rowCreationTime;
}
