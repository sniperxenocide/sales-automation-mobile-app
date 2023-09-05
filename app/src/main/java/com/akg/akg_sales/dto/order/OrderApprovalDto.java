package com.akg.akg_sales.dto.order;

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
        if(approvalTime==null || approvalTime.isEmpty()) return "--";
        try {
            Date dt=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(approvalTime);
            return (new SimpleDateFormat("dd-MMM-yyyy hh:mm a")).format(dt);
        }catch (Exception e){e.printStackTrace();}
        return approvalTime;
    }

}
