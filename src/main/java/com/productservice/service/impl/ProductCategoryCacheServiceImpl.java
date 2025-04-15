package com.productservice.service.impl;

import com.productservice.entity.ProductCategoryEntity;
import com.productservice.exception.NotFoundException;
import com.productservice.repository.ProductCategoryRepository;
import com.productservice.service.ProductCategoryCacheService;
import com.productservice.util.CacheUtil;
import com.productservice.util.constraints.ProductCacheConstraints;
import com.productservice.util.constraints.ProductCacheDurationConstraints;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProductCategoryCacheServiceImpl implements ProductCategoryCacheService {

    private final CacheUtil cacheUtil;
    private final ProductCategoryRepository categoryRepository;


    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackGetCategory")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackGetCategory")
    public ProductCategoryEntity getProductCategory(Long categoryId) {

        return cacheUtil.getOrLoad(ProductCacheConstraints.PRODUCT_CATEGORY_KEY.getKey(categoryId),
                () -> {
                    log.debug("Category with id {} added to cache", categoryId);
                    Optional<ProductCategoryEntity> category = categoryRepository.findById(categoryId);
                    return category.orElseThrow(NotFoundException::new);
                },
                ProductCacheDurationConstraints.DAY.toDuration());
    }

    public ProductCategoryEntity fallbackGetCategory(Long categoryId, Throwable t) {
        log.error("Redis not available for product category {}, falling back to DB. Error: {}",categoryId, t.getMessage());
        return categoryRepository.findById(categoryId).orElseThrow(NotFoundException::new);
    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackClearProductCategoryCache")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackClearProductCategoryCache")
    public void clearProductCategoryCache(Long categoryId) {
        cacheUtil.deleteFromCache(ProductCacheConstraints.PRODUCT_CATEGORY_KEY.getKey(categoryId));
        log.debug("Cache cleared for category {}", categoryId);
    }

    public void fallbackClearProductCategoryCache(Long productId, Throwable t) {
        log.warn("Redis not available to clear cache for product {}, ignoring. Error: {}", productId, t.getMessage());
    }
}
