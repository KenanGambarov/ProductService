package com.productservice.service;

import com.productservice.dto.request.ProductCategoryRequestDto;
import com.productservice.dto.response.ProductCategoryResponseDto;

public interface ProductCategoryService {

    ProductCategoryResponseDto getProductCategoryById(Long id);

    void createProductCategory(ProductCategoryRequestDto requestDto);

    void updateProductCategory(Long id,ProductCategoryRequestDto requestDto);

    void deleteProductCategory(Long id);

}
