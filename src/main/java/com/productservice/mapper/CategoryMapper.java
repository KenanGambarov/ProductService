package com.productservice.mapper;

import com.productservice.dto.request.CategoryRequestDto;
import com.productservice.dto.response.CategoryResponseDto;
import com.productservice.entity.ProductCategoryEntity;

public class CategoryMapper {

    public static ProductCategoryEntity toEntity(Long id, CategoryResponseDto dto) {
        return ProductCategoryEntity.builder()
                .id(id)
                .name(dto.getName())
                .build();
    }

    public static ProductCategoryEntity toEntity(CategoryRequestDto dto, boolean isActive) {
        return ProductCategoryEntity.builder()
                .name(dto.getName())
                .parentId(dto.getParentId())
                .isActive(isActive)
                .build();
    }

    public static CategoryResponseDto toResponseDto(ProductCategoryEntity entity) {
        return CategoryResponseDto.builder()
                .name(entity.getName())
                .build();
    }





}
