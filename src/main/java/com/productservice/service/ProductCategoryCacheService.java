package com.productservice.service;

import com.productservice.entity.ProductCategoryEntity;

public interface ProductCategoryCacheService {

    ProductCategoryEntity getProductCategory(Long categoryId);

    void clearProductCategoryCache(Long categoryId);
}
