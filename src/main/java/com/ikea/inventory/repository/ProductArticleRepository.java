package com.ikea.inventory.repository;

import com.ikea.inventory.entity.ProductArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductArticleRepository extends JpaRepository<ProductArticleEntity,Long> {}