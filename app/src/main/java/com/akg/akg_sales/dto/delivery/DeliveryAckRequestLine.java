package com.akg.akg_sales.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeliveryAckRequestLine {
    private Long id;
    private Double receivedQuantity;
}
