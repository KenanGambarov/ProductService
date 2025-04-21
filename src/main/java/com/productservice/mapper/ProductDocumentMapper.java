package com.productservice.mapper;

import com.productservice.dto.response.ProductDocumentResponseDto;
import com.productservice.entity.ProductEntity;
import com.productservice.search.ProductDocument;

import java.util.List;
import java.util.stream.Collectors;

public class ProductDocumentMapper {

    public static ProductDocument mapToDto(ProductEntity entity) {
        return ProductDocument.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt())
                .categoryName(entity.getCategory().getName())
                .build();
    }

    public static List<ProductDocumentResponseDto> mapToDto(List<ProductDocument> documents) {
        return documents.stream().map(document -> ProductDocumentResponseDto.builder()
                .id(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .price(document.getPrice())
                .createdAt(document.getCreatedAt())
                .categoryName(document.getCategoryName())
                .build()).collect(Collectors.toList());
    }

}
