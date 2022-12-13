package com.akg.akg_sales.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookedLineDto {
    private Long id;
    private String orderedItem;
    private String description;
    private String orderQuantityUom;
    private Double orderedQuantity;
}
