package com.productservice.service.impl;

import com.productservice.dto.request.ProductRequestDto;
import com.productservice.dto.response.ProductCategoryResponseDto;
import com.productservice.dto.response.ProductResponseDto;
import com.productservice.entity.ProductCategoryEntity;
import com.productservice.entity.ProductEntity;
import com.productservice.exception.ExceptionConstants;
import com.productservice.exception.NotFoundException;
import com.productservice.mapper.ProductCategoryMapper;
import com.productservice.mapper.ProductMapper;
import com.productservice.repository.ProductRepository;
import com.productservice.service.ProductCacheService;
import com.productservice.service.ProductCategoryService;
import com.productservice.service.ProductDocumentService;
import com.productservice.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.productservice.exception.ExceptionConstants.PRODUCT_NOT_FOUND;

@Slf4j
@AllArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryService categoryService;
    private final ProductCacheService productCacheService;
    private final ProductDocumentService productDocumentService;

    @Override
    public ProductResponseDto getProductById(Long id) {
        ProductEntity productEntity = findProductOrThrow(id);
        return ProductMapper.mapToDto(productEntity);
    }

    @Transactional
    @Override
    public void createProduct(ProductRequestDto product) {
        ProductCategoryResponseDto responseDto = categoryService.getProductCategoryById(product.getCategoryId());
        ProductCategoryEntity categoryEntity = ProductCategoryMapper.toEntity(product.getCategoryId(),responseDto);
        ProductEntity productEntity = ProductMapper.toEntity(null,product,categoryEntity);
        productEntity = productRepository.save(productEntity);
        productDocumentService.index(productEntity);
        log.info("Product created with id: {}, with name: {}", productEntity.getId(), productEntity.getName());
        productCacheService.clearProductCache(productEntity.getId());
    }

    @Override
    public void updateProduct(Long id, ProductRequestDto product) {
        ProductCategoryResponseDto responseDto = categoryService.getProductCategoryById(product.getCategoryId());
        findProductOrThrow(id);
        ProductCategoryEntity categoryEntity = ProductCategoryMapper.toEntity(product.getCategoryId(),responseDto);;
        ProductEntity productEntity = ProductMapper.toEntity(id,product,categoryEntity);
        productEntity = productRepository.save(productEntity);
        productDocumentService.update(productEntity);
        log.info("Product updated with id: {}", productEntity.getId());
        productCacheService.clearProductCache(productEntity.getId());
    }

    @Override
    public void deleteProduct(Long id) {
        findProductOrThrow(id);
        productRepository.deleteById(id);
        productDocumentService.delete(id);
        log.info("Product deleted with id: {}", id);
        productCacheService.clearProductCache(id);
    }

    private ProductEntity findProductOrThrow(Long productId) {
        return productCacheService.getProductFromCacheOrDB(productId).flatMap(products -> products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
        ).orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));
    }
}
