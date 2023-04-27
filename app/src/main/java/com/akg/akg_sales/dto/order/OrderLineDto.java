package com.akg.akg_sales.dto.order;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class OrderLineDto implements Serializable {
    private Long id;
    private Long orderHeaderId;
    private Integer serialNo;
    private Long itemId;
    private String itemCode;
    private String itemDescription;
    private Double quantity;
    private String uom;

}