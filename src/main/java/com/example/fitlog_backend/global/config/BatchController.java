package com.example.fitlog_backend.global.config;

import com.example.fitlog_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BatchController {

  private final JobLauncher jobLauncher;
  private final Job similarityJob;

  @PostMapping("/api/admin/batch/similarity")
  public ResponseEntity<ApiResponse<?>> runSimilarityBatch() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(similarityJob, params);
    return ResponseEntity.ok(ApiResponse.success("배치 실행 완료", null));
  }
}