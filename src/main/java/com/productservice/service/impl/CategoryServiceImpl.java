package com.productservice.service.impl;

import com.productservice.dto.request.CategoryRequestDto;
import com.productservice.dto.response.CategoryResponseDto;
import com.productservice.dto.response.CategoryTreeResponseDto;
import com.productservice.entity.ProductCategoryEntity;
import com.productservice.exception.NotFoundException;
import com.productservice.mapper.CategoryMapper;
import com.productservice.repository.CategoryRepository;
import com.productservice.service.CategoryCacheService;
import com.productservice.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.productservice.exception.ExceptionConstants.PRODUCT_NOT_FOUND;

@Slf4j
@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryCacheService categoryCacheService;
    private final CategoryRepository categoryRepository;
    private final ProductDocumentServiceImpl productSearchService;

    @Override
    public CategoryResponseDto getProductCategoryById(Long id) {
        ProductCategoryEntity category = findProductCategoryOrThrow(id);
        return CategoryMapper.toResponseDto(category);
    }

    @Override
    public List<CategoryTreeResponseDto> getCategoryTree() {
        List<ProductCategoryEntity> allCategories = categoryRepository.findAll();

        Map<Long, CategoryTreeResponseDto> dtoMap = allCategories.stream()
                .collect(Collectors.toMap(
                        ProductCategoryEntity::getId,
                        cat -> new CategoryTreeResponseDto(cat.getId(), cat.getName(), new ArrayList<>())
                ));

        List<CategoryTreeResponseDto> rootCategories = new ArrayList<>();

        for (ProductCategoryEntity category : allCategories) {
            CategoryTreeResponseDto dto = dtoMap.get(category.getId());
            if (category.getParentId() == null) {
                rootCategories.add(dto);
            } else {
                CategoryTreeResponseDto parentDto = dtoMap.get(category.getParentId());
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                }
            }
        }

        return rootCategories;
    }

//    @Override
//    public CategoryResponseDto getProductCategoryById(Long id) {
//        ProductCategoryEntity category = findProductCategoryOrThrow(id);
//        return CategoryResponseDto.builder()
//                .name(category.getName())
//                .build();
//    }

    @Override
    public List<CategoryResponseDto> getSubcategories(Long categoryId) {
        ProductCategoryEntity category = findProductCategoryOrThrow(categoryId);

        String pathPrefix = category.getPath() + "/" + category.getId();
        List<ProductCategoryEntity> subcategories = categoryRepository.findByPathStartingWith(pathPrefix);
        return subcategories.stream().map(CategoryMapper::toResponseDto).collect(Collectors.toList());
    }

    @Override
    public void createProductCategory(CategoryRequestDto requestDto) {
        ProductCategoryEntity category = CategoryMapper.toEntity(requestDto,true);
        enrichCategoryWithParent(category);
        categoryRepository.save(category);
        ProductCategoryEntity saved = categoryRepository.save(category);

        saved.setPath(saved.getPath() + "/" + saved.getId());
        categoryRepository.save(saved);
    }

    private void enrichCategoryWithParent(ProductCategoryEntity category) {
        if (category.getParentId() != null) {
            ProductCategoryEntity parent = categoryRepository.findById(category.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent not found"));

            category.setPath(parent.getPath() + "/" + parent.getId());
            category.setLevel(parent.getLevel() + 1);
        } else {
            category.setPath("");
            category.setLevel(0);
        }
    }

//    @Override
//    public void createProductCategory(ProductCategoryRequestDto requestDto) {
//        ProductCategoryEntity category = ProductCategoryMapper.toEntity(null, requestDto);
//        category = categoryRepository.save(category);
//        log.info("product category created with id {}", category.getId());
//        categoryCacheService.clearProductCategoryCache(category.getId());
//    }

    @Override
    public void updateProductCategory(Long id, CategoryRequestDto requestDto) {
        ProductCategoryEntity category = findProductCategoryOrThrow(id);
        category.setName(requestDto.getName());
        categoryRepository.save(category);
        productSearchService.reindex(category.getName());
        log.info("product category with id {} updated", id);
        categoryCacheService.clearProductCategoryCache(category.getId());
    }

    @Override
    public void deleteProductCategory(Long id) {
        findProductCategoryOrThrow(id);
        categoryRepository.deleteById(id);
        log.info("product category with id {} deleted", id);
        categoryCacheService.clearProductCategoryCache(id);

    }

    private ProductCategoryEntity findProductCategoryOrThrow(Long productId) {
        return categoryCacheService.getProductCategory(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));
    }

}
