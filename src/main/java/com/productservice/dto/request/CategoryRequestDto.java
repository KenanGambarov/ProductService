package com.productservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {


    private String name;

    private Long parentId; // null olanda root-dir

    private String path; // Məs: /1/3/7

    private Integer level; // Root = 0, Altlar 1, 2 və s.

    private Boolean isActive;

}
