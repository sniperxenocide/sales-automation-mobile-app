package com.akg.akg_sales.dto.notification;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class PaymentClearedDto implements Serializable {
    private Long id;
    private Long cashReceiptId;
    private Long customerId;
    private String receiptNumber;
    private String receiptDate;
    private String customerNumber;
    private String customerName;
    private String customerCategoryCode;
    private String customerPhone;
    private String division;
    private String region;
    private String territory;
    private String depositDate;
    private Long orgId;
    private String currencyCode;
    private Double amount;
    private String receiptCurrStatus;
    private String trxDate;
    private String glDate;
    private Double acctdAmount;
    private Long bankId;
    private Long bankAccountId;
    private String bankAccountNum;
    private String bankName;
    private String bankBranchName;
    private String creationDate;
    private String lastUpdateDate;
    private String soPhoneNumber;
    private String rsoPhoneNumber;
    private String dmoPhoneNumber;
    private String rowCreationTime;
}
