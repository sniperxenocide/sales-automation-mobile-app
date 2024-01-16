package com.akg.akg_sales.dto.order;

import com.akg.akg_sales.util.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OrderApprovalDto {
    private Long id;
    private int sequenceNo;
    private String salesDeskName;
    private String approverUsername;
    private boolean currentFlag;
    private String approvalTime;

    public String getApprovalTime(){
        return CommonUtil.getFormattedDateTime(approvalTime);
    }

}
