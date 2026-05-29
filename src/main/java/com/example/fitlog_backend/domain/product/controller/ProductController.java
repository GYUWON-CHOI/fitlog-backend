package com.example.fitlog_backend.domain.product.controller;

import com.example.fitlog_backend.domain.product.entity.Product;
import com.example.fitlog_backend.domain.product.service.ProductService;
import com.example.fitlog_backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  // 관리자 - 제품 등록
  @PostMapping("/api/admin/products")
  public ResponseEntity<ApiResponse<?>> create(@RequestBody @Valid CreateProductRequest request) {
    Product product = productService.create(
        request.getName(), request.getBrand(), request.getCategory(),
        request.getSizeMin(), request.getSizeMax(), request.getSizeStep(),
        request.getThumbnailUrl()
    );
    return ResponseEntity.ok(ApiResponse.success("제품이 등록되었습니다.", Map.of("id", product.getId())));
  }

  // 제품 목록 조회 (검색)
  @GetMapping("/api/products")
  public ResponseEntity<ApiResponse<?>> search(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) String category) {
    List<Product> products = productService.search(keyword, brand, category);
    List<Map<String, Object>> result = products.stream()
        .map(p -> {
          Map<String, Object> map = new java.util.HashMap<>();
          map.put("id", p.getId());
          map.put("name", p.getName());
          map.put("brand", p.getBrand());
          map.put("category", p.getCategory());
          map.put("sizeMin", p.getSizeMin());
          map.put("sizeMax", p.getSizeMax());
          map.put("sizeStep", p.getSizeStep());
          map.put("thumbnailUrl", p.getThumbnailUrl() != null ? p.getThumbnailUrl() : "");
          return map;
        })
        .toList();
    return ResponseEntity.ok(ApiResponse.success(result));
  }

  // 제품 상세 조회
  @GetMapping("/api/products/{id}")
  public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
    Product product = productService.getById(id);
    return ResponseEntity.ok(ApiResponse.success(Map.of(
        "id", product.getId(),
        "name", product.getName(),
        "brand", product.getBrand(),
        "category", product.getCategory(),
        "sizeMin", product.getSizeMin(),
        "sizeMax", product.getSizeMax(),
        "sizeStep", product.getSizeStep(),
        "thumbnailUrl", product.getThumbnailUrl() != null ? product.getThumbnailUrl() : ""
    )));
  }

  // 관리자 - 제품 수정
  @PatchMapping("/api/admin/products/{id}")
  public ResponseEntity<ApiResponse<?>> update(
      @PathVariable Long id,
      @RequestBody @Valid UpdateProductRequest request) {
    productService.update(id, request.getName(), request.getBrand(), request.getThumbnailUrl());
    return ResponseEntity.ok(ApiResponse.success("제품이 수정되었습니다.", null));
  }

  // 관리자 - 제품 삭제
  @DeleteMapping("/api/admin/products/{id}")
  public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.ok(ApiResponse.success("제품이 삭제되었습니다.", null));
  }

  @Data
  static class CreateProductRequest {
    @NotBlank private String name;
    @NotBlank private String brand;
    @NotBlank private String category;
    @NotNull private Integer sizeMin;
    @NotNull private Integer sizeMax;
    @NotNull private Integer sizeStep;
    private String thumbnailUrl;
  }

  @Data
  static class UpdateProductRequest {
    @NotBlank private String name;
    @NotBlank private String brand;
    private String thumbnailUrl;
  }
}