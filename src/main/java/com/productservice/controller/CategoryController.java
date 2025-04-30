package com.productservice.controller;

import com.productservice.dto.request.CategoryRequestDto;
import com.productservice.dto.response.CategoryResponseDto;
import com.productservice.dto.response.CategoryTreeResponseDto;
import com.productservice.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/product/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponseDto getProductCategoryById(@PathVariable("id") Long id) {
        return categoryService.getProductCategoryById(id);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryTreeResponseDto> getProductCategoryTree() {
        return categoryService.getCategoryTree();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProductCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        categoryService.createProductCategory(categoryRequestDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProductCategory(@PathVariable("id") Long id, @RequestBody CategoryRequestDto categoryRequestDto) {
        categoryService.updateProductCategory(id,categoryRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductCategory(@PathVariable("id") Long id) {
        categoryService.deleteProductCategory(id);
    }

}
