package com.productservice.service;

import com.productservice.dto.request.CategoryRequestDto;
import com.productservice.dto.response.CategoryResponseDto;
import com.productservice.dto.response.CategoryTreeResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto getCategoryById(Long id);

    List<CategoryTreeResponseDto> getCategoryTree();

    List<CategoryTreeResponseDto> getSubcategories(Long parentId);

    void createCategory(CategoryRequestDto requestDto);

    void updateCategory(Long id, CategoryRequestDto requestDto);

    void deleteCategory(Long id);


}
