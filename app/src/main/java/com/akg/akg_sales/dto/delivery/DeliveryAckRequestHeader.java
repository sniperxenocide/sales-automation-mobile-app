package com.akg.akg_sales.dto.delivery;

import com.akg.akg_sales.util.CommonUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data @NoArgsConstructor @AllArgsConstructor
public class DeliveryAckRequestHeader {
    private Long id;
    private Long movOrdHdrId;
    private String movOrderNo;
    private String customerNumber;
    private String customerName;
    private String receivingTime;
    private String comment;
    private File attachment;
    private Boolean fullReceiving;
    private List<DeliveryAckRequestLine> lines;
    private String creationTime;
    private String deviceInfo;

    public String getFormattedReceivingTime(){
        return CommonUtil.getFormattedDateTime(receivingTime);
    }

    public String getFormattedCreationTime(){
        return CommonUtil.getFormattedDateTime(this.creationTime);
    }

}
