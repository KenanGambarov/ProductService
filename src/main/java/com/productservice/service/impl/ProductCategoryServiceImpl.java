package com.productservice.service.impl;

import com.productservice.dto.request.ProductCategoryRequestDto;
import com.productservice.dto.response.ProductCategoryResponseDto;
import com.productservice.entity.ProductCategoryEntity;
import com.productservice.exception.NotFoundException;
import com.productservice.repository.ProductCategoryRepository;
import com.productservice.service.ProductCategoryService;
import com.productservice.util.CacheUtil;
import com.productservice.util.constraints.ProductCacheConstraints;
import com.productservice.util.constraints.ProductCacheDurationConstraints;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final CacheUtil cacheUtil;

    @Override
    public ProductCategoryResponseDto getProductCategoryById(Long id) {
        ProductCategoryEntity category = getProductCategory(id);
        return ProductCategoryResponseDto.builder()
                .name(category.getName())
                .build();
    }

    @Override
    public void createProductCategory(ProductCategoryRequestDto requestDto) {
        ProductCategoryEntity category = ProductCategoryEntity.builder()
                .name(requestDto.getName())
                .build();
        category = categoryRepository.save(category);
        log.info("product category created with id {}", category.getId());
        clearCategoryCache(category.getId());
    }

    @Override
    public void updateProductCategory(Long id, ProductCategoryRequestDto requestDto) {
        ProductCategoryEntity category = getProductCategory(id);
        if (category==null) {
            category = ProductCategoryEntity.builder()
                    .id(id)
                    .name(requestDto.getName())
                    .build();
            categoryRepository.save(category);
            log.info("product category with id {} updated", id);
            clearCategoryCache(category.getId());
        }else
            throw new RuntimeException("Product category not found");
    }

    @Override
    public void deleteProductCategory(Long id) {
        ProductCategoryEntity category = getProductCategory(id);
        if (category==null) {
            categoryRepository.deleteById(id);
            log.info("product category with id {} deleted", id);
            clearCategoryCache(id);
        }else
            throw new RuntimeException("Product category not found");

    }

    private ProductCategoryEntity getProductCategory(Long categoryId) {

        return cacheUtil.getOrLoad(ProductCacheConstraints.PRODUCT_CATEGORY_KEY.getKey(categoryId),
                () -> {
                    log.debug("Category with id {} added to cache", categoryId);
                    Optional<ProductCategoryEntity> category = categoryRepository.findById(categoryId);
                    return category.orElseThrow(NotFoundException::new);
                },
                ProductCacheDurationConstraints.DAY.toDuration());
    }

    private void clearCategoryCache(Long categoryId) {
        cacheUtil.deleteFromCache(ProductCacheConstraints.PRODUCT_CATEGORY_KEY.getKey(categoryId));
        log.debug("Cache cleared for category {}", categoryId);
    }
}
