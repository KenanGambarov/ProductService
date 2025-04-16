package com.productservice.controller;

import com.productservice.dto.response.ProductResponseDto;
import com.productservice.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/internal/v1/products")
public class InternalProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponseDto getProductById(@PathVariable("id") Long id){
        return productService.getProductById(id);
    }

}
