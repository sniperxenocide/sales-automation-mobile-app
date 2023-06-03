package com.akg.akg_sales.dto.payment;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PaymentAccountDto implements Serializable {
    private Long id;
    private String bankAccountNumber="";
    private String bankAccountName="";
    private String accountHolderName="";
    private String bankName="";
    private String bankBranchName="";
}