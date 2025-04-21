package com.productservice.service;

import com.productservice.dto.response.ProductDocumentResponseDto;
import com.productservice.entity.ProductEntity;

import java.util.List;

public interface ProductDocumentService {

    void index(ProductEntity entity);

    void update(ProductEntity entity);

    List<ProductDocumentResponseDto> search(String keyword);

    void delete(Long id);

    void reindex(String productName);
}
