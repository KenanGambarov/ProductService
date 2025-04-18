package com.productservice.controller;

import com.productservice.dto.request.ProductCategoryRequestDto;
import com.productservice.dto.response.ProductCategoryResponseDto;
import com.productservice.service.ProductCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/product/category")
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductCategoryResponseDto getProductCategoryById(@PathVariable("id") Long id) {
        return categoryService.getProductCategoryById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProductCategory(@RequestBody ProductCategoryRequestDto categoryRequestDto) {
        categoryService.createProductCategory(categoryRequestDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProductCategory(@PathVariable("id") Long id, @RequestBody ProductCategoryRequestDto categoryRequestDto) {
        categoryService.updateProductCategory(id,categoryRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductCategory(@PathVariable("id") Long id) {
        categoryService.deleteProductCategory(id);
    }

}
