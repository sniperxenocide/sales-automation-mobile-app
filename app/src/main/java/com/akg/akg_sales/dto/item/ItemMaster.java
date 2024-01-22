package com.akg.akg_sales.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class ItemMaster {
    private Boolean itemTypeActive;
    private Boolean itemSubTypeActive;
    private Boolean itemBrandActive;
    private Boolean itemColorActive;
    private Boolean itemGradeActive;
    private Boolean itemShapeActive;
    List<ItemCategory> categories;
}
