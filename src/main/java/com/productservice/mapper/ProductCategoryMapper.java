package com.productservice.mapper;

import com.productservice.dto.request.ProductCategoryRequestDto;
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

    public static ProductCategoryEntity toEntity(Long id, ProductCategoryRequestDto dto) {
        if (id==null)
            return ProductCategoryEntity.builder()
                    .name(dto.getName())
                    .build();
        else
            return ProductCategoryEntity.builder()
                .id(id)
                .name(dto.getName())
                .build();
    }





}
