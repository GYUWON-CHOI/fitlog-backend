package com.example.fitlog_backend.domain.recommendation.controller;

import com.example.fitlog_backend.domain.recommendation.service.RecommendationService;
import com.example.fitlog_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

  private final RecommendationService recommendationService;

  @GetMapping("/api/recommendations")
  public ResponseEntity<ApiResponse<?>> recommend(
      Authentication authentication,
      @RequestParam Long productId) {
    Long userId = (Long) authentication.getPrincipal();
    RecommendationService.RecommendationResult result =
        recommendationService.recommend(userId, productId);
    return ResponseEntity.ok(ApiResponse.success(result));
  }
}