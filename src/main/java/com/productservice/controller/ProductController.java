package com.productservice.controller;

import com.productservice.dto.request.ProductRequestDto;
import com.productservice.dto.response.ProductDocumentResponseDto;
import com.productservice.dto.response.ProductResponseDto;
import com.productservice.service.ProductDocumentService;
import com.productservice.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/products")
public class ProductController {

private final ProductService productService;
private final ProductDocumentService productDocumentService;

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public Page<ProductResponseDto> getAllProducts(@PageableDefault(size=10, page=1)
//                                                   Pageable pageable) {
//        return productService.getAllProducts(pageable);
//    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDocumentResponseDto> getProducts(@RequestParam(value = "keyword", defaultValue = "")  String keyword,
                                                        @RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                                        @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
                                                        @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {
        return productDocumentService.search(keyword,page,size,sortBy,sortDirection);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponseDto getProductById(@PathVariable("id") Long id){
        return productService.getProductById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequestDto product) {
         productService.createProduct(product);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct(@PathVariable("id") Long id, @RequestBody ProductRequestDto product) {
         productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
    }

}
