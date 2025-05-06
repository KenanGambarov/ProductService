package com.productservice.repository;

import com.productservice.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {

    List<CategoryEntity> findByPathStartingWith(String path);
//
//    @Query("SELECT c FROM CategoryEntity c WHERE c.parentId IS NULL")
//    List<CategoryEntity> findRootCategories();

    List<CategoryEntity> findByParentId(Long id);

}
