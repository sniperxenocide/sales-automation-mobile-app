package com.akg.akg_sales.dto.order;

import com.akg.akg_sales.dto.delivery.DeliveryOrderDto;
import com.akg.akg_sales.util.CommonUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class OrderDto {
    private Long id;
    private String orderNumber;
    private String oracleOrderNumber;
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
    private String currentApproverSalesDesk;
    private String currentStatus;
    private Double value;
    private String bookedDate;
    private Double bookedValue;
    private List<OrderLineDto> orderLines;
    private List<OrderApprovalDto> approvals;
    private String note;
    private String siteAddress;
    private List<DeliveryOrderDto> deliveryOrders;

    public String getCreationTime(){
        try {
            Date dt=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(creationTime);
            return (new SimpleDateFormat("dd-MMM-yyyy hh:mm a")).format(dt);
        }catch (Exception e){e.printStackTrace();}
        return creationTime;
    }

    public String getValue() {
        return CommonUtil.decimalToAccounting(this.value);
    }

    public String getBookedDate(){
        if(bookedDate==null || bookedDate.isEmpty()) return "--";
        try {
            Date dt=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(bookedDate);
            return (new SimpleDateFormat("dd-MMM-yyyy hh:mm a")).format(dt);
        }catch (Exception e){e.printStackTrace();}
        return bookedDate;
    }

    public String getBookedValue(){
        if(this.bookedValue==null) return "--";
        return CommonUtil.decimalToAccounting(this.bookedValue);
    }

    public String getOracleOrderNumber(){
        if(this.oracleOrderNumber==null) return "--";
        return this.oracleOrderNumber;
    }

    public String getCustomerName(){
        return this.customerName.replace("."," ")
                .replace("-"," ").replace(","," ");
    }
}
