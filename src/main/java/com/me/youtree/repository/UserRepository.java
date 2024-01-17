package com.me.youtree.repository;

import com.me.youtree.domain.SocialType;
import com.me.youtree.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findBySocialTypeAAndSocialId(SocialType socialType, String socialId);
}
