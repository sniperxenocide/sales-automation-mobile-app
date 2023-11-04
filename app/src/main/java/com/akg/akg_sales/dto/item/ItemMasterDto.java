package com.akg.akg_sales.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class ItemMasterDto {
    List<ItemTypeDto> itemTypes;
    List<ItemBrandDto> itemBrands;
    List<ItemColorDto> itemColors;
}
