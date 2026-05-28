package com.example.fitlog_backend.domain.item.service;

import com.example.fitlog_backend.domain.item.entity.Fit;
import com.example.fitlog_backend.domain.item.entity.UserItem;
import com.example.fitlog_backend.domain.item.repository.UserItemRepository;
import com.example.fitlog_backend.domain.product.entity.Product;
import com.example.fitlog_backend.domain.product.repository.ProductRepository;
import com.example.fitlog_backend.domain.user.entity.User;
import com.example.fitlog_backend.domain.user.entity.UserStatus;
import com.example.fitlog_backend.domain.user.repository.UserRepository;
import com.example.fitlog_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserItemService {

  private final UserItemRepository userItemRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  @Transactional(readOnly = true)
  public List<UserItem> getMyItems(Long userId) {
    return userItemRepository.findByUserIdWithProduct(userId);
  }

  @Transactional
  public UserItem addItem(Long userId, Long productId, Integer size, String fit) {
    User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "제품을 찾을 수 없습니다."));

    if (userItemRepository.existsByUserIdAndProductId(userId, productId)) {
      throw new CustomException(HttpStatus.CONFLICT, "이미 등록된 제품입니다.");
    }

    UserItem item = UserItem.builder()
        .user(user)
        .product(product)
        .size(size)
        .fit(Fit.valueOf(fit))
        .build();

    return userItemRepository.save(item);
  }

  @Transactional
  public void updateItem(Long userId, Long itemId, Integer size, String fit) {
    UserItem item = userItemRepository.findByIdAndUserId(itemId, userId)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다."));
    item.update(size, Fit.valueOf(fit));
  }

  @Transactional
  public void deleteItem(Long userId, Long itemId) {
    UserItem item = userItemRepository.findByIdAndUserId(itemId, userId)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다."));
    userItemRepository.delete(item);
  }
}