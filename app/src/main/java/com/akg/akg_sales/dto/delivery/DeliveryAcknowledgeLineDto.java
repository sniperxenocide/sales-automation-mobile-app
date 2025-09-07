package com.akg.akg_sales.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class DeliveryAcknowledgeLineDto {
    private Long id;
    private String serial;
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

    public DeliveryAcknowledgeLineDto(Long id,String doNumber,String itemDescription, Double lineQuantity,Double receivedQuantity, String uomCode,int serial) {
        isHeader = false;
        this.id = id;
        this.doNumber = doNumber;
        this.itemDescription = itemDescription;
        this.lineQuantity = lineQuantity;
        this.receivedQuantity = receivedQuantity==null?lineQuantity:receivedQuantity;
        this.uomCode = uomCode;
        this.serial = Integer.toString(serial);
    }
}
