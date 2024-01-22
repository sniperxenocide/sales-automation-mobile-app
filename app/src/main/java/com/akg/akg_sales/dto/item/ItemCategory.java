package com.akg.akg_sales.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCategory {
    private ItemTypeDto itemType;
    private ItemSubTypeDto itemSubType;
    private ItemBrandDto itemBrand;
    private ItemColorDto itemColor;
    private ItemGradeDto itemGrade;
    private ItemShapeDto itemShape;
}
