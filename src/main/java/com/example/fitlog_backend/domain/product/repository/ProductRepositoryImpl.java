package com.example.fitlog_backend.domain.product.repository;

import com.example.fitlog_backend.domain.product.entity.Category;
import com.example.fitlog_backend.domain.product.entity.Product;
import com.example.fitlog_backend.domain.product.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Product> searchProducts(String keyword, String brand, String category) {
    QProduct product = QProduct.product;

    return queryFactory
        .selectFrom(product)
        .where(
            keywordContains(keyword),
            brandEquals(brand),
            categoryEquals(category)
        )
        .orderBy(product.name.asc())
        .fetch();
  }

  private BooleanExpression keywordContains(String keyword) {
    return StringUtils.hasText(keyword) ? QProduct.product.name.containsIgnoreCase(keyword) : null;
  }

  private BooleanExpression brandEquals(String brand) {
    return StringUtils.hasText(brand) ? QProduct.product.brand.eq(brand) : null;
  }

  private BooleanExpression categoryEquals(String category) {
    if (!StringUtils.hasText(category)) return null;
    try {
      return QProduct.product.category.eq(Category.valueOf(category));
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}