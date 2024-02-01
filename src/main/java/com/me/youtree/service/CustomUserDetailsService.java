package com.me.youtree.service;

import com.me.youtree.domain.User;
import com.me.youtree.dto.CustomUserDetails;
import com.me.youtree.exception.ErrorCode;
import com.me.youtree.exception.YouTreeApplicationException;
import com.me.youtree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new YouTreeApplicationException(ErrorCode.USER_NOT_FOUND));

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().getKey()));

        return new CustomUserDetails(user.getEmail(), user.getPassword(), roles);
    }
}
