package com.akg.akg_sales.dto;

import com.akg.akg_sales.dto.item.ItemDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartItemDto {
    private ItemDto itemDto;
    private Double quantity;
}
