package com.akg.akg_sales.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CustomerDto implements Serializable {
    private Long id;
    private Long oracleCustomerId;
    private String oracleCustomerCode;
    private String customerName;
    private String customerPhone;
    private String address;
    private String operatingUnitId;
    private String operatingUnit;
    private String marketSegment;
    private String salesDesk;

}