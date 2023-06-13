package com.akg.akg_sales.dto.payment;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PaymentDto {
    private Long id;
    private String paymentNumber;
    private String bankAccountNumber;
    private String accountHolderName;
    private String bankName;
    private String branchName;
    private Double paymentAmount;
    private String paymentType;
    private String paymentDate;
    private String receiptNumber;
    private String customerBankName;
    private String customerBankBranch;
    private String customerAccountNumber;
    private String customerNumber;
    private String customerName;
    private Long createdByUserId;
    private String createdByUsername;

    public String getPaymentDate(){
        try {
            return this.paymentDate.split("T")[0];
        }catch (Exception e){return this.paymentDate;}

    }
}
