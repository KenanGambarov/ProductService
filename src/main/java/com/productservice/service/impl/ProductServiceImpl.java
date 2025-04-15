package com.productservice.service.impl;

import com.productservice.dto.request.ProductRequestDto;
import com.productservice.dto.response.ProductCategoryResponseDto;
import com.productservice.dto.response.ProductResponseDto;
import com.productservice.entity.ProductCategoryEntity;
import com.productservice.entity.ProductEntity;
import com.productservice.exception.NotFoundException;
import com.productservice.mapper.ProductCategoryMapper;
import com.productservice.mapper.ProductMapper;
import com.productservice.repository.ProductRepository;
import com.productservice.service.ProductCacheService;
import com.productservice.service.ProductCategoryService;
import com.productservice.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryService categoryService;
    private final ProductCacheService productCacheService;


    @Override
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productCacheService.getAllProductsFromCacheOrDB(pageable);
    }


    @Override
    public ProductResponseDto getProductById(Long id) {
        ProductEntity productEntity = getProduct(id).orElseThrow(NotFoundException::new);
        return ProductMapper.mapToDto(productEntity);
    }

    @Transactional
    @Override
    public void createProduct(ProductRequestDto product) {
        ProductCategoryResponseDto responseDto = categoryService.getProductCategoryById(product.getCategoryId());

        if(responseDto==null){
            throw new RuntimeException("Product category not found");
        }
        ProductCategoryEntity categoryEntity = ProductCategoryMapper.toEntity(product.getCategoryId(),responseDto);
        ProductEntity productEntity = ProductMapper.toEntity(null,product,categoryEntity);
        productEntity = productRepository.save(productEntity);
        log.info("Product created with id: {}", productEntity.getId());
        productCacheService.clearProductCache(productEntity.getId());
    }

    @Override
    public void updateProduct(Long id, ProductRequestDto product) {
        ProductCategoryResponseDto responseDto = categoryService.getProductCategoryById(product.getCategoryId());
        if(responseDto==null){
            throw new RuntimeException("Product category not found");
        }
        Optional<ProductEntity> optionalProduct = getProduct(id);
        if(optionalProduct.isPresent()){
            ProductCategoryEntity categoryEntity = ProductCategoryMapper.toEntity(product.getCategoryId(),responseDto);;
            ProductEntity productEntity = ProductMapper.toEntity(id,product,categoryEntity);
            productEntity = productRepository.save(productEntity);
            log.info("Product updated with id: {}", productEntity.getId());
            productCacheService.clearProductCache(productEntity.getId());
        }else
            throw new RuntimeException("Product not found");


    }

    @Override
    public void deleteProduct(Long id) {
        Optional<ProductEntity> productEntity = getProduct(id);
        if(productEntity.isPresent()){
            productRepository.deleteById(id);
            log.info("Product deleted with id: {}", id);
            productCacheService.clearProductCache(id);
        }else
            throw new RuntimeException("Product not found");
    }

    private Optional<ProductEntity> getProduct(Long productId) {
        List<ProductEntity> items = productCacheService.getProductFromCacheOrDB(productId);

        return items.stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst();
    }
}
