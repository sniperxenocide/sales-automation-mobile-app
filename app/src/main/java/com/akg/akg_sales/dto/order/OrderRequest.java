package com.akg.akg_sales.dto.order;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data @NoArgsConstructor @AllArgsConstructor
@Accessors(chain = true)
public class OrderRequest {
    private Long orderId;
    private Long customerId;
    private List<OrderLineRequest> lines = new ArrayList<>();
    private String note;
    private Long siteId;

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
