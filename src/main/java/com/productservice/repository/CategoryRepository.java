package com.productservice.repository;

import com.productservice.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<ProductCategoryEntity,Long> {

    List<ProductCategoryEntity> findByPathStartingWith(String path);

    @Query("SELECT c FROM ProductCategoryEntity c WHERE c.parentId IS NULL")
    List<ProductCategoryEntity> findRootCategories();

}
