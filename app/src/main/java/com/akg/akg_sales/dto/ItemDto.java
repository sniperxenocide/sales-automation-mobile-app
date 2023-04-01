package com.akg.akg_sales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class ItemDto {
    private Long inventoryItemId;
    private String itemCode;
    private String description;
    private String uom;
}
