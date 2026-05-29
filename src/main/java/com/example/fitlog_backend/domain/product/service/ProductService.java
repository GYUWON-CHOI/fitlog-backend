package com.example.fitlog_backend.domain.product.service;

import com.example.fitlog_backend.domain.product.entity.Category;
import com.example.fitlog_backend.domain.product.entity.Product;
import com.example.fitlog_backend.domain.product.repository.ProductRepository;
import com.example.fitlog_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  @Transactional
  public Product create(String name, String brand, String category,
      Integer sizeMin, Integer sizeMax, Integer sizeStep,
      String thumbnailUrl, String modelNumber) {
    if (productRepository.existsByNameAndBrand(name, brand)) {
      throw new CustomException(HttpStatus.CONFLICT, "이미 등록된 제품입니다.");
    }

    Product product = Product.builder()
        .name(name)
        .brand(brand)
        .category(Category.valueOf(category))
        .sizeMin(sizeMin)
        .sizeMax(sizeMax)
        .sizeStep(sizeStep)
        .thumbnailUrl(thumbnailUrl)
        .modelNumber(modelNumber)
        .build();

    return productRepository.save(product);
  }

  @Transactional(readOnly = true)
  public List<Product> search(String keyword, String brand, String category) {
    return productRepository.searchProducts(keyword, brand, category);
  }

  @Transactional(readOnly = true)
  public Product getById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "제품을 찾을 수 없습니다."));
  }

  @Transactional
  public void update(Long id, String name, String brand, String thumbnailUrl, String modelNumber) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "제품을 찾을 수 없습니다."));
    product.update(name, brand, thumbnailUrl, modelNumber);
  }

  @Transactional
  public void delete(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "제품을 찾을 수 없습니다."));
    productRepository.delete(product);
  }
}