package com.example.fitlog_backend.domain.user.repository;

import com.example.fitlog_backend.domain.user.entity.User;
import com.example.fitlog_backend.domain.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  Optional<User> findByEmailAndStatus(String email, UserStatus status);

  Optional<User> findByIdAndStatus(Long id, UserStatus status);
}