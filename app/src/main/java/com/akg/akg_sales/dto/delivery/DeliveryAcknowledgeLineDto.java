package com.akg.akg_sales.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class DeliveryAcknowledgeLineDto {
    private Boolean isHeader;
    private Long orderNumber;
    private String doNumber;
    private String itemDescription;
    private Double lineQuantity;
    private Double receivedQuantity;
    private String uomCode;

    public DeliveryAcknowledgeLineDto(String doNumber){
        isHeader = true;
        this.doNumber = doNumber;
    }

    public DeliveryAcknowledgeLineDto(String itemDescription, Double lineQuantity, String uomCode) {
        isHeader = false;
        this.itemDescription = itemDescription;
        this.lineQuantity = lineQuantity;
        this.receivedQuantity = lineQuantity;
        this.uomCode = uomCode;
    }
}
