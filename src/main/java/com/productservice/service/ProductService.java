package com.productservice.service;

import com.productservice.dto.request.ProductRequestDto;
import com.productservice.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto getProductById(Long id);

    void createProduct(ProductRequestDto product);

    void updateProduct(Long id, ProductRequestDto product);

    void deleteProduct(Long id);
}
