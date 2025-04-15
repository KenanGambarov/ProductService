package com.productservice.service.impl;

import com.productservice.dto.response.ProductResponseDto;
import com.productservice.entity.ProductEntity;
import com.productservice.mapper.ProductCategoryMapper;
import com.productservice.repository.ProductRepository;
import com.productservice.service.ProductCacheService;
import com.productservice.util.CacheUtil;
import com.productservice.util.constraints.ProductCacheConstraints;
import com.productservice.util.constraints.ProductCacheDurationConstraints;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ProductCacheServiceImpl implements ProductCacheService {

    private final CacheUtil cacheUtil;
    private final ProductRepository productRepository;

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackGetAllProducts")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackGetAllProducts")
    public Page<ProductResponseDto> getAllProductsFromCacheOrDB(Pageable pageable) {
        String cacheKey = ProductCacheConstraints.PRODUCT_LIST_KEY.getKey(pageable.getPageNumber(), pageable.getPageSize());

        return cacheUtil.getOrLoad(
                cacheKey,
                () -> {
                    log.debug("Loading product page {} from DB and caching", pageable.getPageNumber());
                    return productRepository.findAllBy(pageable).map(ProductCategoryMapper::mapToDto);
                },
                ProductCacheDurationConstraints.DAY.toDuration()
        );
    }

    public Page<ProductResponseDto> fallbackGetAllProducts(Pageable pageable, Throwable t) {
        log.error("Redis not available for pageable products, falling back to DB. Error: {}", t.getMessage());
        return productRepository.findAllBy(pageable).map(ProductCategoryMapper::mapToDto);
    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackGetProduct")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackGetProduct")
    public List<ProductEntity> getProductFromCacheOrDB(Long productId){
        return cacheUtil.getOrLoad(ProductCacheConstraints.PRODUCT_KEY.getKey(productId),
                () ->{log.debug("Product with id {} added to cache", productId); return productRepository.findAllById(productId);},
                ProductCacheDurationConstraints.DAY.toDuration());
    }

    public List<ProductEntity> fallbackGetProduct(Long productId, Throwable t) {
        log.error("Redis not available for product {}, falling back to DB. Error: {}",productId, t.getMessage());
        return productRepository.findAllById(productId);
    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackClearProductCache")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackClearProductCache")
    public void clearProductCache(Long productId) {
        cacheUtil.deleteFromCache(ProductCacheConstraints.PRODUCT_KEY.getKey(productId));
        log.debug("Cache cleared for product {}", productId);
    }

    public void fallbackClearProductCache(Long productId, Throwable t) {
        log.warn("Redis not available to clear cache for product {}, ignoring. Error: {}", productId, t.getMessage());
    }
}
