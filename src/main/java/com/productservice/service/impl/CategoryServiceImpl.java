package com.productservice.service.impl;

import com.productservice.dto.request.CategoryRequestDto;
import com.productservice.dto.response.CategoryResponseDto;
import com.productservice.dto.response.CategoryTreeResponseDto;
import com.productservice.entity.CategoryEntity;
import com.productservice.exception.NotFoundException;
import com.productservice.mapper.CategoryMapper;
import com.productservice.repository.CategoryRepository;
import com.productservice.service.CategoryCacheService;
import com.productservice.service.CategoryService;
import com.productservice.util.CacheUtil;
import com.productservice.util.CategoryTreeUtil;
import com.productservice.util.constraints.ProductCacheConstraints;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.productservice.exception.ExceptionConstants.*;

@Slf4j
@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryCacheService categoryCacheService;
    private final CategoryRepository categoryRepository;
    private final ProductDocumentServiceImpl productSearchService;
    private final CacheUtil cacheUtil;

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        CategoryEntity category = findCategoryOrThrow(id);
        return CategoryMapper.toResponseDto(category);
    }

    @Override
    public List<CategoryTreeResponseDto> getCategoryTree() {
        return getCategoryTreeOrThrow();
    }

    @Override
    public List<CategoryTreeResponseDto> getSubcategories(Long parentId) {
        List<CategoryTreeResponseDto> categoryTree = getCategoryTreeOrThrow();
        return CategoryTreeUtil.findSubcategories(categoryTree, parentId);
    }

    @Override
    public void createCategory(CategoryRequestDto requestDto) {
        CategoryEntity category = CategoryMapper.toEntity(requestDto,true);
        enrichCategoryWithParent(category);
        CategoryEntity saved = categoryRepository.save(category);
        categoryRepository.save(category);
        String parentPath = "";
        if (saved.getParentId() != null) {
            CategoryEntity parent = findCategoryOrThrow(category.getParentId());
            parentPath = parent.getPath();
        }
        saved.setPath(parentPath + "/" + saved.getId());
        categoryRepository.save(saved);
        categoryCacheService.clearCategoryCache(category.getId());
        categoryCacheService.clearCategoryTreeCache(ProductCacheConstraints.CATEGORY_TREE_KEY.getKey());
    }

    private void enrichCategoryWithParent(CategoryEntity category) {
        if (category.getParentId() != null) {
            CategoryEntity parent = findCategoryOrThrow(category.getParentId());
            category.setLevel(parent.getLevel() + 1);
        } else {
            category.setPath("");
            category.setLevel(0);
        }
    }

    @Transactional
    public void updateCategory(Long id, CategoryRequestDto dto) {
        CategoryEntity category = findCategoryOrThrow(id);

        boolean parentChanged = !Objects.equals(category.getParentId(), dto.getParentId());

        category.setName(dto.getName());
        category.setParentId(dto.getParentId());

        if (parentChanged) {
            updateCategoryHierarchy(category);
        }

        categoryRepository.save(category);
        productSearchService.reindex(category.getName());
        categoryCacheService.clearCategoryTreeCache(ProductCacheConstraints.CATEGORY_TREE_KEY.getKey());
    }

    private void updateCategoryHierarchy(CategoryEntity category) {
        enrichCategoryWithParent(category);
        updateChildrenHierarchy(category);
    }

    private void updateChildrenHierarchy(CategoryEntity parentCategory) {
        List<CategoryEntity> children = getCategoryByParentOrThrow(parentCategory.getId());

        for (CategoryEntity child : children) {
            child.setPath(parentCategory.getPath() + "/" + parentCategory.getId());
            child.setLevel(parentCategory.getLevel() + 1);
            categoryRepository.save(child);
            updateChildrenHierarchy(child);
        }
    }

    @Override
    public void deleteCategory(Long id) {
        findCategoryOrThrow(id);
        categoryRepository.deleteById(id);
        log.info("product category with id {} deleted", id);
        categoryCacheService.clearCategoryCache(id);

    }

    private CategoryEntity findCategoryOrThrow(Long categoryId) {
        return categoryCacheService.getCategory(categoryId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_CATEGORY_NOT_FOUND.getMessage()));
    }

    private List<CategoryTreeResponseDto> getCategoryTreeOrThrow() {
        return categoryCacheService.getCategoryTree()
                .orElseThrow(() -> new NotFoundException(PRODUCT_CATEGORY_NOT_FOUND.getMessage()));
    }

    private List<CategoryEntity> getCategoryByParentOrThrow(Long parentId) {
        return categoryCacheService.getCategoryByParent(parentId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_CATEGORY_NOT_FOUND.getMessage()));
    }

}
