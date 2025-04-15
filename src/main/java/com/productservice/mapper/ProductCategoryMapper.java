package com.productservice.mapper;

import com.productservice.dto.response.ProductCategoryResponseDto;
import com.productservice.dto.response.ProductResponseDto;
import com.productservice.entity.ProductCategoryEntity;
import com.productservice.entity.ProductEntity;

public class ProductCategoryMapper {

    public static ProductCategoryEntity toEntity(Long id, ProductCategoryResponseDto dto) {
        return ProductCategoryEntity.builder()
                .id(id)
                .name(dto.getName())
                .build();
    }

    public static ProductResponseDto mapToDto(ProductEntity e) {
        ProductCategoryResponseDto categoryDto = ProductCategoryResponseDto.builder()
                .name(e.getCategory().getName())
                .build();

        return ProductResponseDto.builder()
                .name(e.getName())
                .description(e.getDescription())
                .price(e.getPrice())
                .category(categoryDto)
                .build();
    }
}
