package com.example.fitlog_backend.domain.product.repository;

import com.example.fitlog_backend.domain.product.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {

  List<Product> searchProducts(String keyword, String brand, String category);
}