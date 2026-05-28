package com.example.fitlog_backend.domain.user.controller;

import com.example.fitlog_backend.domain.user.entity.User;
import com.example.fitlog_backend.domain.user.service.UserService;
import com.example.fitlog_backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/api/auth/signup")
  public ResponseEntity<ApiResponse<?>> signup(@RequestBody @Valid SignupRequest request) {
    userService.signup(request.getEmail(), request.getPassword(), request.getNickname());
    return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", null));
  }

  @PostMapping("/api/auth/login")
  public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid LoginRequest request) {
    String token = userService.login(request.getEmail(), request.getPassword());
    return ResponseEntity.ok(ApiResponse.success(Map.of("token", token)));
  }

  @GetMapping("/api/users/me")
  public ResponseEntity<ApiResponse<?>> getMyInfo(Authentication authentication) {
    Long userId = (Long) authentication.getPrincipal();
    User user = userService.getMyInfo(userId);
    return ResponseEntity.ok(ApiResponse.success(Map.of(
        "id", user.getId(),
        "email", user.getEmail(),
        "nickname", user.getNickname()
    )));
  }

  @PatchMapping("/api/users/me")
  public ResponseEntity<ApiResponse<?>> updateNickname(
      Authentication authentication,
      @RequestBody @Valid UpdateNicknameRequest request) {
    Long userId = (Long) authentication.getPrincipal();
    userService.updateNickname(userId, request.getNickname());
    return ResponseEntity.ok(ApiResponse.success("닉네임이 수정되었습니다.", null));
  }

  @DeleteMapping("/api/users/me")
  public ResponseEntity<ApiResponse<?>> withdraw(Authentication authentication) {
    Long userId = (Long) authentication.getPrincipal();
    userService.withdraw(userId);
    return ResponseEntity.ok(ApiResponse.success("탈퇴가 완료되었습니다.", null));
  }

  @Data
  static class SignupRequest {
    @Email @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
  }

  @Data
  static class LoginRequest {
    @Email @NotBlank
    private String email;
    @NotBlank
    private String password;
  }

  @Data
  static class UpdateNicknameRequest {
    @NotBlank
    private String nickname;
  }
}