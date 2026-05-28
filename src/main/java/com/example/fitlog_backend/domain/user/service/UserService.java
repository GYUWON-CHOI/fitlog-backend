package com.example.fitlog_backend.domain.user.service;

import com.example.fitlog_backend.domain.user.entity.User;
import com.example.fitlog_backend.domain.user.entity.UserStatus;
import com.example.fitlog_backend.domain.user.repository.UserRepository;
import com.example.fitlog_backend.global.exception.CustomException;
import com.example.fitlog_backend.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional
  public void signup(String email, String password, String nickname) {
    if (userRepository.existsByEmail(email)) {
      throw new CustomException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
    }

    User user = User.builder()
        .email(email)
        .password(passwordEncoder.encode(password))
        .nickname(nickname)
        .build();

    userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public String login(String email, String password) {
    User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
        .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new CustomException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    return jwtTokenProvider.generateToken(user.getId());
  }

  @Transactional(readOnly = true)
  public User getMyInfo(Long userId) {
    return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
  }

  @Transactional
  public void updateNickname(Long userId, String nickname) {
    User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
    user.updateNickname(nickname);
  }

  @Transactional
  public void withdraw(Long userId) {
    User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
    user.withdraw();
  }
}