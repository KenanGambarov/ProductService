package com.productservice.service.impl;

import com.productservice.entity.ProductCategoryEntity;
import com.productservice.exception.NotFoundException;
import com.productservice.repository.CategoryRepository;
import com.productservice.service.CategoryCacheService;
import com.productservice.util.CacheUtil;
import com.productservice.util.constraints.ProductCacheConstraints;
import com.productservice.util.constraints.ProductCacheDurationConstraints;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class CategoryCacheServiceImpl implements CategoryCacheService {

    private final CacheUtil cacheUtil;
    private final CategoryRepository categoryRepository;


//    @Override
//    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackCategoryTree")
//    @Retry(name = "redisRetry", fallbackMethod = "fallbackCategoryTree")
//    public Optional<ProductCategoryEntity> getCategoryTree(Long categoryId) {
//        ProductCategoryEntity categoryEntity = cacheUtil.getOrLoad(ProductCacheConstraints.PRODUCT_CATEGORY_KEY.getKey(categoryId),
//                () -> {
//                    List<ProductCategoryEntity> category = categoryRepository.findAll();
//                    if(log.isDebugEnabled()){
//                        log.debug("Category with id {} added to cache", categoryId);
//                    }
//                    return category.stream()..orElseThrow(NotFoundException::new);
//                },
//                ProductCacheDurationConstraints.DAY.toDuration());
//        return Optional.ofNullable(categoryEntity);
//    }
//
//    public Optional<ProductCategoryEntity> fallbackCategoryTree(Long categoryId, Throwable t) {
//        log.error("Redis not available for product category {}, falling back to DB. Error: {}",categoryId, t.getMessage());
//        return Optional.empty();
//    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackGetCategory")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackGetCategory")
    public Optional<ProductCategoryEntity> getProductCategory(Long categoryId) {
        ProductCategoryEntity categoryEntity = cacheUtil.getOrLoad(ProductCacheConstraints.PRODUCT_CATEGORY_KEY.getKey(categoryId),
                () -> {
                    Optional<ProductCategoryEntity> category = categoryRepository.findById(categoryId);
                    if(log.isDebugEnabled()){
                        log.debug("Category with id {} added to cache", categoryId);
                    }
                    return category.orElseThrow(NotFoundException::new);
                },
                ProductCacheDurationConstraints.DAY.toDuration());
        return Optional.ofNullable(categoryEntity);
    }

    public Optional<ProductCategoryEntity> fallbackGetCategory(Long categoryId, Throwable t) {
        log.error("Redis not available for product category {}, falling back to DB. Error: {}",categoryId, t.getMessage());
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackClearProductCategoryCache")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackClearProductCategoryCache")
    public void clearProductCategoryCache(Long categoryId) {
        cacheUtil.deleteFromCache(ProductCacheConstraints.PRODUCT_CATEGORY_KEY.getKey(categoryId));
        if(log.isDebugEnabled()){
            log.debug("Cache cleared for category {}", categoryId);
        }

    }

    public void fallbackClearProductCategoryCache(Long productId, Throwable t) {
        log.warn("Redis not available to clear cache for product {}, ignoring. Error: {}", productId, t.getMessage());
    }
}
