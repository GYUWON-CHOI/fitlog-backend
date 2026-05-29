package com.example.fitlog_backend.global.config;

import com.example.fitlog_backend.domain.item.entity.UserItem;
import com.example.fitlog_backend.domain.item.repository.UserItemRepository;
import com.example.fitlog_backend.domain.recommendation.entity.UserSimilarity;
import com.example.fitlog_backend.domain.recommendation.repository.UserSimilarityRepository;
import com.example.fitlog_backend.domain.user.entity.User;
import com.example.fitlog_backend.domain.user.entity.UserStatus;
import com.example.fitlog_backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimilarityCalculationBatch {

  private final UserRepository userRepository;
  private final UserItemRepository userItemRepository;
  private final UserSimilarityRepository userSimilarityRepository;

  @Bean
  public Job similarityJob(JobRepository jobRepository, Step similarityStep) {
    return new JobBuilder("similarityJob", jobRepository)
        .start(similarityStep)
        .build();
  }

  @Bean
  public Step similarityStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("similarityStep", jobRepository)
        .tasklet(similarityTasklet(), transactionManager)
        .build();
  }

  @Bean
  public Tasklet similarityTasklet() {
    return (contribution, chunkContext) -> {
      log.info("유사도 계산 배치 시작");

      // 1. 모든 활성 유저 조회
      List<User> users = userRepository.findAll().stream()
          .filter(u -> u.getStatus() == UserStatus.ACTIVE)
          .toList();

      // 2. 유저별 보유 아이템 맵 구성
      Map<Long, List<UserItem>> userItemMap = users.stream()
          .collect(Collectors.toMap(
              User::getId,
              u -> userItemRepository.findByUserId(u.getId())
          ));

      // 3. 기존 유사도 데이터 전체 삭제 후 flush
      userSimilarityRepository.deleteAll();
      userSimilarityRepository.flush();

      // 4. 모든 유저 쌍 유사도 계산
      List<UserSimilarity> similarities = new ArrayList<>();

      for (int i = 0; i < users.size(); i++) {
        for (int j = i + 1; j < users.size(); j++) {
          User userA = users.get(i);
          User userB = users.get(j);

          List<UserItem> itemsA = userItemMap.get(userA.getId());
          List<UserItem> itemsB = userItemMap.get(userB.getId());

          SimilarityResult result = calculate(itemsA, itemsB);

          if (result.commonCount() > 0) {
            similarities.add(UserSimilarity.builder()
                .userA(userA)
                .userB(userB)
                .similarity(result.score())
                .commonCount(result.commonCount())
                .build());
          }
        }
      }

      userSimilarityRepository.saveAll(similarities);
      log.info("유사도 계산 완료 - {} 쌍 저장", similarities.size());

      return RepeatStatus.FINISHED;
    };
  }

  private SimilarityResult calculate(List<UserItem> itemsA, List<UserItem> itemsB) {
    // A의 보유 아이템을 productId → UserItem 맵으로 변환
    Map<Long, UserItem> mapA = itemsA.stream()
        .collect(Collectors.toMap(
            item -> item.getProduct().getId(),
            item -> item
        ));

    int totalScore = 0;
    int commonCount = 0;

    for (UserItem itemB : itemsB) {
      Long productId = itemB.getProduct().getId();
      UserItem itemA = mapA.get(productId);

      if (itemA != null) {
        commonCount++;
        if (itemA.getSize().equals(itemB.getSize()) &&
            itemA.getFit().equals(itemB.getFit())) {
          totalScore += 3; // 사이즈 + 핏 일치
        } else if (itemA.getSize().equals(itemB.getSize())) {
          totalScore += 2; // 사이즈만 일치
        }
        // 불일치는 +0
      }
    }

    double similarity = commonCount > 0
        ? (double) totalScore / (commonCount * 3)
        : 0.0;

    return new SimilarityResult(similarity, commonCount);
  }

  private record SimilarityResult(double score, int commonCount) {}
}