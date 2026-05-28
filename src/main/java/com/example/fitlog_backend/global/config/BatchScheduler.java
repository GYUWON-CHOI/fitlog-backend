package com.example.fitlog_backend.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BatchScheduler {

  private final JobLauncher jobLauncher;
  private final Job similarityJob;

  // 매일 새벽 2시 실행
  @Scheduled(cron = "0 0 2 * * *")
  public void runSimilarityJob() {
    try {
      JobParameters params = new JobParametersBuilder()
          .addLong("time", System.currentTimeMillis())
          .toJobParameters();
      jobLauncher.run(similarityJob, params);
    } catch (Exception e) {
      log.error("유사도 배치 실행 실패", e);
    }
  }
}