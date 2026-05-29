package com.example.fitlog_backend.domain.item.controller;

import com.example.fitlog_backend.domain.item.entity.UserItem;
import com.example.fitlog_backend.domain.item.service.UserItemService;
import com.example.fitlog_backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserItemController {

  private final UserItemService userItemService;

  @GetMapping("/api/items")
  public ResponseEntity<ApiResponse<?>> getMyItems(Authentication authentication) {
    Long userId = (Long) authentication.getPrincipal();
    List<UserItem> items = userItemService.getMyItems(userId);
    List<Map<String, Object>> result = items.stream()
        .map(item -> {
          Map<String, Object> map = new HashMap<>();
          map.put("id", item.getId());
          map.put("productId", item.getProduct().getId());
          map.put("productName", item.getProduct().getName());
          map.put("brand", item.getProduct().getBrand());
          map.put("size", item.getSize());
          map.put("fit", item.getFit());
          map.put("thumbnailUrl", item.getProduct().getThumbnailUrl() != null ? item.getProduct().getThumbnailUrl() : "");
          return map;
        })
        .toList();
    return ResponseEntity.ok(ApiResponse.success(result));
  }

  @PostMapping("/api/items")
  public ResponseEntity<ApiResponse<?>> addItem(
      Authentication authentication,
      @RequestBody @Valid AddItemRequest request) {
    Long userId = (Long) authentication.getPrincipal();
    UserItem item = userItemService.addItem(userId, request.getProductId(),
        request.getSize(), request.getFit());
    return ResponseEntity.ok(ApiResponse.success("아이템이 등록되었습니다.",
        Map.of("id", item.getId())));
  }

  @PatchMapping("/api/items/{id}")
  public ResponseEntity<ApiResponse<?>> updateItem(
      Authentication authentication,
      @PathVariable Long id,
      @RequestBody @Valid UpdateItemRequest request) {
    Long userId = (Long) authentication.getPrincipal();
    userItemService.updateItem(userId, id, request.getSize(), request.getFit());
    return ResponseEntity.ok(ApiResponse.success("아이템이 수정되었습니다.", null));
  }

  @DeleteMapping("/api/items/{id}")
  public ResponseEntity<ApiResponse<?>> deleteItem(
      Authentication authentication,
      @PathVariable Long id) {
    Long userId = (Long) authentication.getPrincipal();
    userItemService.deleteItem(userId, id);
    return ResponseEntity.ok(ApiResponse.success("아이템이 삭제되었습니다.", null));
  }

  @Data
  static class AddItemRequest {
    @NotNull private Long productId;
    @NotNull private Integer size;
    @NotBlank private String fit;
  }

  @Data
  static class UpdateItemRequest {
    @NotNull private Integer size;
    @NotBlank private String fit;
  }
}