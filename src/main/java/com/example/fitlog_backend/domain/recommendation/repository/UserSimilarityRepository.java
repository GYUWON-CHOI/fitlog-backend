package com.example.fitlog_backend.domain.recommendation.repository;

import com.example.fitlog_backend.domain.recommendation.entity.UserSimilarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSimilarityRepository extends JpaRepository<UserSimilarity, Long> {

  @Query("""
            SELECT us FROM UserSimilarity us
            WHERE us.userA.id = :userId AND us.commonCount > 0
            ORDER BY us.similarity DESC
            """)
  List<UserSimilarity> findSimilarUsers(@Param("userId") Long userId);

  void deleteByUserAIdOrUserBId(Long userAId, Long userBId);
}