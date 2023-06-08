package com.akg.akg_sales.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PaymentTypeDto {
    private Long id;
    private String paymentTypeCode;
    private String description;
}
