package com.akg.akg_sales.dto.notification;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class PaymentDto implements Serializable {
    private Long id;
    private Long cashReceiptId;
    private Long cashReceiptHistoryId;
    private Long customerId;
    private String receiptNumber;
    private String receiptDate;
    private String receiptMethod;
    private String customerNumber;
    private String customerName;
    private String depositDate;
    private Long orgId;
    private String operatingUnit;
    private String status;
    private String type;
    private String currencyCode;
    private Double amount;
    private String receiptCurrStatus;
    private String trxDate;
    private String glDate;
    private String bankAccountNum;
    private String bankName;
    private String bankBranchName;
    private String creationDate;
    private String lastUpdateDate;
    private String rowCreationTime;
}
