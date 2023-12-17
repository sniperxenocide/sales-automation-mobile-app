package com.akg.akg_sales.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor @AllArgsConstructor
public class PaymentStatus {
    private Long id;
    private String statusCode;
    private String statusText;
    private Integer sequence;

}