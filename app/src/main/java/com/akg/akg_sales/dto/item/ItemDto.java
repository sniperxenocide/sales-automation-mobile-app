package com.akg.akg_sales.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class ItemDto {
    private Long id;
    private String itemCode;
    private String itemDescription;
    private String primaryUom;
    private String secondaryUom;
}
