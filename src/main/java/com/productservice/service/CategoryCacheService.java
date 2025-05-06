package com.productservice.service;

import com.productservice.dto.response.CategoryTreeResponseDto;
import com.productservice.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryCacheService {

    Optional<List<CategoryTreeResponseDto>> getCategoryTree();

    Optional<CategoryEntity> getCategory(Long categoryId);

    void clearCategoryCache(Long categoryId);

    void clearCategoryTreeCache(String key);
}
