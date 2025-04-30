package com.productservice.service;

import com.productservice.dto.request.CategoryRequestDto;
import com.productservice.dto.response.CategoryResponseDto;
import com.productservice.dto.response.CategoryTreeResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto getProductCategoryById(Long id);

    List<CategoryTreeResponseDto> getCategoryTree();

    List<CategoryResponseDto> getSubcategories(Long categoryId);

    void createProductCategory(CategoryRequestDto requestDto);

    void updateProductCategory(Long id, CategoryRequestDto requestDto);

    void deleteProductCategory(Long id);


}
