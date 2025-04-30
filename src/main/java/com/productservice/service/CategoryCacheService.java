package com.productservice.service;

import com.productservice.entity.ProductCategoryEntity;

import java.util.Optional;

public interface CategoryCacheService {

    Optional<ProductCategoryEntity> getProductCategory(Long categoryId);

    void clearProductCategoryCache(Long categoryId);
}
