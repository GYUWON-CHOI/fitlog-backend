package com.example.fitlog_backend.domain.recommendation.service;

import com.example.fitlog_backend.domain.item.entity.Fit;
import com.example.fitlog_backend.domain.item.entity.UserItem;
import com.example.fitlog_backend.domain.item.repository.UserItemRepository;
import com.example.fitlog_backend.domain.product.entity.Product;
import com.example.fitlog_backend.domain.product.repository.ProductRepository;
import com.example.fitlog_backend.domain.recommendation.entity.UserSimilarity;
import com.example.fitlog_backend.domain.recommendation.repository.UserSimilarityRepository;
import com.example.fitlog_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {

  private final UserSimilarityRepository userSimilarityRepository;
  private final UserItemRepository userItemRepository;
  private final ProductRepository productRepository;

  @Transactional(readOnly = true)
  public RecommendationResult recommend(Long userId, Long productId) {

    // 1. 제품 존재 여부 확인
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "제품을 찾을 수 없습니다."));

    // 2. 유사 유저 목록 조회
    List<UserSimilarity> similarities = userSimilarityRepository.findSimilarUsers(userId);

    if (similarities.isEmpty()) {
      return RecommendationResult.noData(product);
    }

    // 3. 유사 유저 ID 목록 추출
    List<Long> similarUserIds = similarities.stream()
        .map(s -> s.getUserB().getId())
        .toList();

    // 4. 유사 유저들의 해당 제품 아이템 조회 (Fetch Join 사용)
    List<UserItem> similarUserItems = new ArrayList<>();
    for (Long similarUserId : similarUserIds) {
      userItemRepository.findByUserIdWithProduct(similarUserId).stream()
          .filter(item -> item.getProduct().getId().equals(productId))
          .forEach(similarUserItems::add);
    }

    if (similarUserItems.isEmpty()) {
      return RecommendationResult.noData(product);
    }

    // 5. 사이즈 + 핏 통계 집계
    Map<String, Integer> distribution = new LinkedHashMap<>();
    for (UserItem item : similarUserItems) {
      String key = item.getSize() + "_" + item.getFit().name();
      distribution.merge(key, 1, Integer::sum);
    }

    // 6. 가장 많은 사이즈 추출
    String recommendedKey = distribution.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);

    int recommendedSize = 0;
    Fit recommendedFit = null;
    if (recommendedKey != null) {
      String[] parts = recommendedKey.split("_");
      recommendedSize = Integer.parseInt(parts[0]);
      recommendedFit = Fit.valueOf(parts[1]);
    }

    // 7. 분포 데이터 구성
    int totalUsers = similarUserItems.size();
    List<DistributionItem> distributionList = distribution.entrySet().stream()
        .map(entry -> {
          String[] parts = entry.getKey().split("_");
          int size = Integer.parseInt(parts[0]);
          Fit fit = Fit.valueOf(parts[1]);
          int count = entry.getValue();
          int percentage = (int) Math.round((double) count / totalUsers * 100);
          return new DistributionItem(size, fit, count, percentage);
        })
        .sorted(Comparator.comparingInt(DistributionItem::size))
        .toList();

    // 8. 신뢰도 계산
    String confidence = totalUsers >= 10 ? "HIGH"
        : totalUsers >= 5 ? "MEDIUM"
            : "LOW";

    return new RecommendationResult(
        product.getId(),
        product.getName(),
        recommendedSize,
        recommendedFit,
        confidence,
        totalUsers,
        distributionList
    );
  }

  public record RecommendationResult(
      Long productId,
      String productName,
      Integer recommendedSize,
      Fit recommendedFit,
      String confidence,
      Integer totalUsers,
      List<DistributionItem> distribution
  ) {
    static RecommendationResult noData(Product product) {
      return new RecommendationResult(
          product.getId(),
          product.getName(),
          null, null, "NO_DATA", 0,
          Collections.emptyList()
      );
    }
  }

  public record DistributionItem(
      int size,
      Fit fit,
      int count,
      int percentage
  ) {}
}