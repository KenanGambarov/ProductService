package com.productservice.service.impl;

import com.productservice.dto.request.ProductRequestDto;
import com.productservice.dto.response.ProductCategoryResponseDto;
import com.productservice.dto.response.ProductResponseDto;
import com.productservice.entity.ProductCategoryEntity;
import com.productservice.entity.ProductEntity;
import com.productservice.exception.NotFoundException;
import com.productservice.mapper.ProductCategoryMapper;
import com.productservice.repository.ProductRepository;
import com.productservice.service.ProductCategoryService;
import com.productservice.service.ProductService;
import com.productservice.util.CacheUtil;
import com.productservice.util.constraints.ProductCacheConstraints;
import com.productservice.util.constraints.ProductCacheDurationConstraints;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryService categoryService;
    private final CacheUtil cacheUtil;


    @Override
    public List<ProductResponseDto> getAllProducts() {
        return List.of();
    }


    @Override
    public ProductResponseDto getProductById(Long id) {
        ProductEntity productEntity = getProduct(id).orElseThrow(NotFoundException::new);
        ProductCategoryResponseDto responseDto = ProductCategoryResponseDto.builder()
                .name(productEntity.getCategory().getName())
                .build();
        return ProductResponseDto.builder()
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .category(responseDto)
                .build();
    }

    @Transactional
    @Override
    public void createProduct(ProductRequestDto product) {
        ProductCategoryResponseDto responseDto = categoryService.getProductCategoryById(product.getCategoryId());

        if(responseDto==null){
            throw new RuntimeException("Product category not found");
        }
        ProductCategoryEntity categoryEntity = ProductCategoryMapper.toEntity(product.getCategoryId(),responseDto);
        ProductEntity category = ProductEntity.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(categoryEntity)
                .build();
        category = productRepository.save(category);
        log.info("Product created with id: {}", category.getId());
        clearProductCache(category.getId());
    }

    @Override
    public void updateProduct(Long id, ProductRequestDto product) {
        ProductCategoryResponseDto responseDto = categoryService.getProductCategoryById(product.getCategoryId());
        if(responseDto==null){
            throw new RuntimeException("Product category not found");
        }
        Optional<ProductEntity> productEntity = getProduct(id);
        if(productEntity.isPresent()){
            ProductCategoryEntity categoryEntity = ProductCategoryMapper.toEntity(product.getCategoryId(),responseDto);;
            ProductEntity category = ProductEntity.builder()
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .category(categoryEntity)
                    .build();
            category = productRepository.save(category);
            log.info("Product updated with id: {}", category.getId());
            clearProductCache(category.getId());
        }else
            throw new RuntimeException("Product not found");


    }

    @Override
    public void deleteProduct(Long id) {
        Optional<ProductEntity> productEntity = getProduct(id);
        if(productEntity.isPresent()){
            productRepository.deleteById(id);
            log.info("Product deleted with id: {}", id);
            clearProductCache(id);
        }else
            throw new RuntimeException("Product not found");
    }

    private Optional<ProductEntity> getProduct(Long productId) {
        List<ProductEntity> items = cacheUtil.getOrLoad(ProductCacheConstraints.PRODUCT_KEY.getKey(productId),
                () ->{log.debug("Product with id {} added to cache", productId); return productRepository.findAllById(productId);},
                ProductCacheDurationConstraints.DAY.toDuration());

        return items.stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst();
    }



    private void clearProductCache(Long productId) {
        cacheUtil.deleteFromCache(ProductCacheConstraints.PRODUCT_KEY.getKey(productId));
        log.debug("Cache cleared for product {}", productId);
    }
}
