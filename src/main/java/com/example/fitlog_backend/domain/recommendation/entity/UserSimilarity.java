package com.example.fitlog_backend.domain.recommendation.entity;

import com.example.fitlog_backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_similarities",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id_a", "user_id_b"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserSimilarity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id_a", nullable = false)
  private User userA;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id_b", nullable = false)
  private User userB;

  @Column(nullable = false)
  private Double similarity;

  @Column(nullable = false)
  private Integer commonCount;

  @Column(nullable = false)
  private LocalDateTime calculatedAt;

  @PrePersist
  @PreUpdate
  protected void onCalculate() {
    this.calculatedAt = LocalDateTime.now();
  }
}