package com.blog.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.blog.app.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
