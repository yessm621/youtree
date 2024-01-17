package com.me.youtree.service;

import com.me.youtree.domain.User;
import com.me.youtree.domain.UserRole;
import com.me.youtree.dto.UserSignUpDto;
import com.me.youtree.exception.ErrorCode;
import com.me.youtree.exception.YouTreeApplicationException;
import com.me.youtree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(UserSignUpDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new YouTreeApplicationException(ErrorCode.DUPLICATED_USER_NAME, "이미 존재하는 이메일입니다.");
        }

        if (userRepository.findByEmail(dto.getNickname()).isPresent()) {
            throw new YouTreeApplicationException(ErrorCode.DUPLICATED_USER_NAME, "이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .nickname(dto.getNickname())
                .role(UserRole.USER)
                .build();
        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
//        throw new YouTreeApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", email));
    }
}

