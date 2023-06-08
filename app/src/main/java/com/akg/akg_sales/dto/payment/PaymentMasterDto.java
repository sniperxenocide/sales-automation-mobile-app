package com.akg.akg_sales.dto.payment;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PaymentMasterDto {
    private List<PaymentAccountDto> paymentAccounts;
    private List<PaymentTypeDto> paymentTypes;
    private List<BankDto> banks;
}
