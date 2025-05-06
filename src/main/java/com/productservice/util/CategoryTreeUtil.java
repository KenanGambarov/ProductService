package com.productservice.util;

import com.productservice.dto.response.CategoryTreeResponseDto;

import java.util.List;

public class CategoryTreeUtil {

    public static List<CategoryTreeResponseDto> findSubcategories(List<CategoryTreeResponseDto> categories, Long parentId) {
        for (CategoryTreeResponseDto category : categories) {
            if (category.getId().equals(parentId)) {
                return category.getChildren();
            } else {
                List<CategoryTreeResponseDto> found = findSubcategories(category.getChildren(), parentId);
                if (found != null) return found;
            }
        }
        return null;
    }

}
