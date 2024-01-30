package com.me.youtree.service;

import com.me.youtree.domain.User;
import com.me.youtree.domain.UserRole;
import com.me.youtree.dto.TokenDto;
import com.me.youtree.dto.UserDto;
import com.me.youtree.exception.ErrorCode;
import com.me.youtree.exception.YouTreeApplicationException;
import com.me.youtree.jwt.JwtTokenProvider;
import com.me.youtree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
        User user = userRepository.save(
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
    public TokenDto login(UserDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new YouTreeApplicationException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new YouTreeApplicationException(ErrorCode.INVALID_PASSWORD);

        user.updateRefreshToken(jwtTokenProvider.createRefreshToken());

        return new TokenDto(jwtTokenProvider.createToken(dto.email()), user.getRefreshToken());
    }

    @Transactional
    public TokenDto reIssue(TokenDto dto) {
        if (!jwtTokenProvider.validateTokenExpiration(dto.refreshToken()))
            throw new YouTreeApplicationException(ErrorCode.INVALID_TOKEN);

        User user = findUserByToken(dto);

        if (!user.getRefreshToken().equals(dto.refreshToken()))
            throw new YouTreeApplicationException(ErrorCode.INVALID_TOKEN);

        String accessToken = jwtTokenProvider.createToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        user.updateRefreshToken(refreshToken);

        return new TokenDto(accessToken, refreshToken);
    }

    public User findUserByToken(TokenDto dto) {
        Authentication auth = jwtTokenProvider.getAuthentication(dto.accessToken());
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new YouTreeApplicationException(ErrorCode.USER_NOT_FOUND));
    }
}
