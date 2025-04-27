package com.productservice.service;

import com.productservice.dto.request.ProductRequestDto;
import com.productservice.dto.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

//    Page<ProductResponseDto> getAllProducts(Pageable pageable);

    ProductResponseDto getProductById(Long id);

    void createProduct(ProductRequestDto product);

    void updateProduct(Long id, ProductRequestDto product);

    void deleteProduct(Long id);
}
