package com.productservice.mapper;

import com.productservice.dto.request.ProductRequestDto;
import com.productservice.dto.response.CategoryResponseDto;
import com.productservice.dto.response.ProductResponseDto;
import com.productservice.entity.CategoryEntity;
import com.productservice.entity.ProductEntity;

public class ProductMapper {

    public static ProductEntity toEntity(Long id,ProductRequestDto product, CategoryEntity categoryEntity){
        if (id==null)
            return ProductEntity.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(categoryEntity)
                .build();
        else
            return ProductEntity.builder()
                .id(id)
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(categoryEntity)
                .build();
    }

    public static ProductResponseDto mapToDto(ProductEntity e) {
        CategoryResponseDto categoryDto = CategoryResponseDto.builder()
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
