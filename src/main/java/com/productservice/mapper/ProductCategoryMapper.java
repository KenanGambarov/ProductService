package com.productservice.mapper;

import com.productservice.dto.response.ProductCategoryResponseDto;
import com.productservice.entity.ProductCategoryEntity;

public class ProductCategoryMapper {

    public static ProductCategoryEntity toEntity(Long id, ProductCategoryResponseDto dto) {
        return ProductCategoryEntity.builder()
                .id(id)
                .name(dto.getName())
                .build();
    }
}
