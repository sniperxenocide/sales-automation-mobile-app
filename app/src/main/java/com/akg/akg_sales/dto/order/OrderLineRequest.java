package com.akg.akg_sales.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OrderLineRequest {
    private Long itemId;
    private Double quantity;
}
