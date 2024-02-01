package com.me.youtree.service;

import com.me.youtree.domain.User;
import com.me.youtree.domain.UserRole;
import com.me.youtree.dto.UserDto;
import com.me.youtree.exception.ErrorCode;
import com.me.youtree.exception.YouTreeApplicationException;
import com.me.youtree.jwt.JwtTokenProvider;
import com.me.youtree.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserDto dto) {
        validateDuplicated(dto.email());
        userRepository.save(
                User.builder()
                        .email(dto.email())
                        .password(passwordEncoder.encode(dto.password()))
                        .authProvider(null)
                        .role(UserRole.USER)
                        .build());
    }

    public void validateDuplicated(String email) {
        if (userRepository.findByEmail(email).isPresent())
            throw new YouTreeApplicationException(ErrorCode.DUPLICATED_USER_NAME);
    }

    @Transactional
    public void reIssue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.extractRefreshToken(request)
                .orElseThrow(() -> new YouTreeApplicationException(ErrorCode.INVALID_TOKEN, "RefreshToken을 찾을 수 없습니다."));
        if (!jwtTokenProvider.validateTokenExpiration(refreshToken)) {
            throw new YouTreeApplicationException(ErrorCode.INVALID_TOKEN, "유효하지 않은 RefreshToken 입니다. 다시 로그인 해주세요.");
        }

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new YouTreeApplicationException(ErrorCode.INVALID_TOKEN, "RefreshToken을 찾을 수 없습니다."));

        String newRefreshToken = jwtTokenProvider.reIssue(response, user.getEmail());

        user.updateRefreshToken(newRefreshToken);
    }
}
