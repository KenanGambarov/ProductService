package com.productservice.service;

import com.productservice.dto.response.ProductResponseDto;
import com.productservice.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductCacheService {

//    Page<ProductResponseDto> getAllProductsFromCacheOrDB(Pageable pageable);

    Optional<List<ProductEntity>> getProductFromCacheOrDB(Long productId);

    void clearProductCache(Long productId);

}
