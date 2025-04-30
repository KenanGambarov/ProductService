package com.productservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ProductResponseDto implements Serializable {

    private String name;

    private String description;

    private Double price;

    private CategoryResponseDto category;

}
