package com.productservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {

    private String name;

    private String description;

    private Double price;

    private ProductCategoryResponseDto category;

}
