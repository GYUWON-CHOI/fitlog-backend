package com.example.fitlog_backend.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String brand;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Category category;

  @Column(nullable = false)
  private Integer sizeMin;

  @Column(nullable = false)
  private Integer sizeMax;

  @Column(nullable = false)
  private Integer sizeStep;

  private String thumbnailUrl;

  @Column(name = "model_number")
  private String modelNumber;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public void update(String name, String brand, String thumbnailUrl, String modelNumber) {
    this.name = name;
    this.brand = brand;
    this.thumbnailUrl = thumbnailUrl;
    this.modelNumber = modelNumber;
  }
}