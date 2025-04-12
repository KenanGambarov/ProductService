package com.productservice.repository;

import com.productservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity,Long>{


    @EntityGraph(attributePaths = {"category"})
    List<ProductEntity> findAllById(Long productId);
}
