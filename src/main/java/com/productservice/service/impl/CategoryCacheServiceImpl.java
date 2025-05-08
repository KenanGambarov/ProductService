package com.productservice.service.impl;

import com.productservice.dto.response.CategoryTreeResponseDto;
import com.productservice.entity.CategoryEntity;
import com.productservice.exception.NotFoundException;
import com.productservice.mapper.CategoryMapper;
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
import java.util.Map;
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
//    public Optional<List<CategoryEntity> > getCategoryTree() {
//        List<CategoryEntity>  categoryEntity = cacheUtil.getOrLoad(ProductCacheConstraints.CATEGORY_TREE_KEY.getKey(),
//                categoryRepository::findAll,
//                ProductCacheDurationConstraints.DAY.toDuration());
//        return Optional.ofNullable(categoryEntity);
//    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackCategoryTree")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackCategoryTree")
    public Optional<List<CategoryTreeResponseDto>> getCategoryTree() {
        List<CategoryTreeResponseDto> categories = cacheUtil.getOrLoad(ProductCacheConstraints.CATEGORY_TREE_KEY.getKey(),
                this::buildAndCacheCategoryTree,
                ProductCacheDurationConstraints.DAY.toDuration());
        return Optional.ofNullable(categories);
    }

    private List<CategoryTreeResponseDto> buildAndCacheCategoryTree() {
        List<CategoryEntity> allCategories = categoryRepository.findAll();
        Map<Long, CategoryTreeResponseDto> dtoMap = CategoryMapper.mapToDto(allCategories);
        return CategoryMapper.buildTree(allCategories, dtoMap);
    }


    public Optional<List<CategoryTreeResponseDto>> fallbackCategoryTree(Long categoryId, Throwable t) {
        log.error("Redis not available for product category tree {}, falling back to DB. Error: {}",categoryId, t.getMessage());
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackGetCategoryByParent")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackGetCategoryByParent")
    public Optional<List<CategoryEntity>> getCategoryByParent(Long parentId) {
        List<CategoryEntity> categoryEntityList = cacheUtil.getOrLoad(ProductCacheConstraints.CATEGORY_KEY.getKey(parentId),
                () -> categoryRepository.findByParentId(parentId),
                ProductCacheDurationConstraints.DAY.toDuration());
        return Optional.ofNullable(categoryEntityList);
    }

    public Optional<List<CategoryEntity>> fallbackGetCategoryByParent(Long parentId, Throwable t) {
        log.error("Redis not available for product category with parent {}, falling back to DB. Error: {}",parentId, t.getMessage());
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackGetCategory")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackGetCategory")
    public Optional<CategoryEntity> getCategory(Long categoryId) {
        CategoryEntity categoryEntity = cacheUtil.getOrLoad(ProductCacheConstraints.CATEGORY_KEY.getKey(categoryId),
                () -> {
                    Optional<CategoryEntity> category = categoryRepository.findById(categoryId);
                    if(log.isDebugEnabled()){
                        log.debug("Category with id {} added to cache", categoryId);
                    }
                    return category.orElseThrow(NotFoundException::new);
                },
                ProductCacheDurationConstraints.DAY.toDuration());
        return Optional.ofNullable(categoryEntity);
    }

    public Optional<CategoryEntity> fallbackGetCategory(Long categoryId, Throwable t) {
        log.error("Redis not available for product category {}, falling back to DB. Error: {}",categoryId, t.getMessage());
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackClearCategoryCache")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackClearCategoryCache")
    public void clearCategoryCache(Long categoryId) {
        cacheUtil.deleteFromCache(ProductCacheConstraints.CATEGORY_KEY.getKey(categoryId));
        if(log.isDebugEnabled()){
            log.debug("Cache cleared for category {}", categoryId);
        }

    }

    public void fallbackClearCategoryCache(Long productId, Throwable t) {
        log.warn("Redis not available to clear cache for product {}, ignoring. Error: {}", productId, t.getMessage());
    }

    @Override
    @CircuitBreaker(name = "redisBreaker", fallbackMethod = "fallbackClearCategoryTreeCache")
    @Retry(name = "redisRetry", fallbackMethod = "fallbackClearCategoryTreeCache")
    public void clearCategoryTreeCache(String key) {
        cacheUtil.deleteFromCache(key);
        if(log.isDebugEnabled()){
            log.debug("Cache cleared for category tree {}", key);
        }

    }

    public void fallbackClearCategoryTreeCache(String productId, Throwable t) {
        log.warn("Redis not available to clear cache for product tree {}, ignoring. Error: {}", productId, t.getMessage());
    }
}
