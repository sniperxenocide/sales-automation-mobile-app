package com.akg.akg_sales.dto.order;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OrderRequest {
    private Long customerId;
    private List<OrderLineRequest> lines = new ArrayList<>();

    public void addLine(Long itemId,Integer quantity){
        lines.add(new OrderLineRequest(itemId,quantity));
    }

    private class OrderLineRequest{
        private Long itemId;
        private Integer quantity;

        public OrderLineRequest(Long itemId, Integer quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }
    }
}
