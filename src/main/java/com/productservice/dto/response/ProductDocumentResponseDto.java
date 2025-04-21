package com.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDocumentResponseDto {

    private Long id;

    private String name;

    private String description;

    private double price;

    private Date createdAt;

    private String categoryName;

}
