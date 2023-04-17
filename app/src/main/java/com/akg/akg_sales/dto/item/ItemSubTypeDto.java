package com.akg.akg_sales.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data @NoArgsConstructor @AllArgsConstructor @Accessors(chain = true)
public class ItemSubTypeDto {
    private Long id;
    private String subType;
}
