package com.productservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CategoryResponseDto implements Serializable {

    private String name;

}
