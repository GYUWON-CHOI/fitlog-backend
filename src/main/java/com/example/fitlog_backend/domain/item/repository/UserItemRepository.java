package com.example.fitlog_backend.domain.item.repository;

import com.example.fitlog_backend.domain.item.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

  List<UserItem> findByUserId(Long userId);

  Optional<UserItem> findByIdAndUserId(Long id, Long userId);

  boolean existsByUserIdAndProductId(Long userId, Long productId);

  @Query("SELECT ui FROM UserItem ui JOIN FETCH ui.product WHERE ui.user.id = :userId")
  List<UserItem> findByUserIdWithProduct(@Param("userId") Long userId);
}