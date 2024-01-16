package com.akg.akg_sales.dto.delivery;

import com.akg.akg_sales.util.CommonUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveOrderConfirmedHeaderDto implements Serializable {
    private Long orgId;
    private Long moveOrderHeaderId;
    private String movOrderNo;
    private String vehicleNo;
    private String driverName;
    private String driverMobile;
    private String operatingUnit;
    private String movOrderTime;
    private String movOrderStatus;
    private String moveWarehouseOrgName;
    private String moveConfirmedDate;
    private String gateOutDate;
    private String transporterName;
    private String rowCreationTime;

    public String getMoveConfirmedDate(){
        return CommonUtil.getFormattedDateTime(moveConfirmedDate);
    }

    public String getGateOutDate(){
        return CommonUtil.getFormattedDateTime(gateOutDate);
    }
}
