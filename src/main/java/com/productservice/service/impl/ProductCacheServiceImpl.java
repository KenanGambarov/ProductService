package com.productservice.service.impl;

import com.productservice.entity.ProductEntity;
import com.productservice.repository.ProductRepository;
import com.productservice.service.ProductCacheService;
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
public class ProductCacheServiceImpl implements ProductCacheService {

    private final CacheUtil cacheUtil;
    private final ProductRepository productRepository;

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackGetProduct")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackGetProduct")
    public Optional<List<ProductEntity>> getProductFromCacheOrDB(Long productId) {
        List<ProductEntity> products = cacheUtil.getOrLoad(ProductCacheConstraints.PRODUCT_KEY.getKey(productId),
                () -> {
                    if (log.isDebugEnabled() ) {
                        log.debug("Product with id {} added to cache", productId);
                    }
                    return productRepository.findAllById(productId);
                },
                ProductCacheDurationConstraints.DAY.toDuration());
        return Optional.ofNullable(products);
    }

    public Optional<List<ProductEntity>> fallbackGetProduct(Long productId, Throwable t) {
        log.error("Redis not available for product {}, falling back to DB. Error: {}", productId, t.getMessage());
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackClearProductCache")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackClearProductCache")
    public void clearProductCache(Long productId) {
        cacheUtil.deleteFromCache(ProductCacheConstraints.PRODUCT_KEY.getKey(productId));
        if (log.isDebugEnabled() ) {
            log.debug("Cache cleared for product {}", productId);
        }

    }

    public void fallbackClearProductCache(Long productId, Throwable t) {
        log.warn("Redis not available to clear cache for product {}, ignoring. Error: {}", productId, t.getMessage());
    }
}
