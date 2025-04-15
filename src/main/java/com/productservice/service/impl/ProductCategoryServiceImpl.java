package com.productservice.service.impl;

import com.productservice.dto.request.ProductCategoryRequestDto;
import com.productservice.dto.response.ProductCategoryResponseDto;
import com.productservice.entity.ProductCategoryEntity;
import com.productservice.repository.ProductCategoryRepository;
import com.productservice.service.ProductCategoryCacheService;
import com.productservice.service.ProductCategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryCacheService categoryCacheService;
    private final ProductCategoryRepository categoryRepository;

    @Override
    public ProductCategoryResponseDto getProductCategoryById(Long id) {
        ProductCategoryEntity category = categoryCacheService.getProductCategory(id);
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
        categoryCacheService.clearProductCategoryCache(category.getId());
    }

    @Override
    public void updateProductCategory(Long id, ProductCategoryRequestDto requestDto) {
        ProductCategoryEntity category = categoryCacheService.getProductCategory(id);
        if (category==null) {
            category = ProductCategoryEntity.builder()
                    .id(id)
                    .name(requestDto.getName())
                    .build();
            categoryRepository.save(category);
            log.info("product category with id {} updated", id);
            categoryCacheService.clearProductCategoryCache(category.getId());
        }else
            throw new RuntimeException("Product category not found");
    }

    @Override
    public void deleteProductCategory(Long id) {
        ProductCategoryEntity category = categoryCacheService.getProductCategory(id);
        if (category==null) {
            categoryRepository.deleteById(id);
            log.info("product category with id {} deleted", id);
            categoryCacheService.clearProductCategoryCache(id);
        }else
            throw new RuntimeException("Product category not found");

    }


}
