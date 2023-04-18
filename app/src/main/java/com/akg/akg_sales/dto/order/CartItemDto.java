package com.akg.akg_sales.dto.order;

import com.akg.akg_sales.dto.CustomerDto;
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
    private CustomerDto customerDto;
    private ItemDto itemDto;
    private Integer quantity;
}
