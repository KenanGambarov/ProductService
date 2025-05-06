package com.productservice.mapper;

import com.productservice.dto.request.CategoryRequestDto;
import com.productservice.dto.response.CategoryResponseDto;
import com.productservice.dto.response.CategoryTreeResponseDto;
import com.productservice.entity.CategoryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryEntity toEntity(Long id, CategoryResponseDto dto) {
        return CategoryEntity.builder()
                .id(id)
                .name(dto.getName())
                .build();
    }

    public static CategoryEntity toEntity(CategoryRequestDto dto, boolean isActive) {
        return CategoryEntity.builder()
                .name(dto.getName())
                .parentId(dto.getParentId())
                .isActive(isActive)
                .build();
    }

    public static CategoryResponseDto toResponseDto(CategoryEntity entity) {
        return CategoryResponseDto.builder()
                .name(entity.getName())
                .build();
    }

    public static Map<Long, CategoryTreeResponseDto> mapToDto(List<CategoryEntity> allCategories){
        return allCategories.stream()
                .collect(Collectors.toMap(
                        CategoryEntity::getId,
                        cat -> new CategoryTreeResponseDto(cat.getId(), cat.getName(), new ArrayList<>())
                ));
    }

    public static List<CategoryTreeResponseDto> buildTree(List<CategoryEntity> allCategories, Map<Long, CategoryTreeResponseDto> dtoMap) {
        List<CategoryTreeResponseDto> rootCategories = new ArrayList<>();

        for (CategoryEntity category : allCategories) {
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

}
