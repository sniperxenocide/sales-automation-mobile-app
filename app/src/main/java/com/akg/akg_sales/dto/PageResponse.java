package com.akg.akg_sales.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private Long recordCount;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private List<T> data;
}
