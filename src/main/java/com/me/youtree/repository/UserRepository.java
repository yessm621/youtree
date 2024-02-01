package com.me.youtree.repository;

import com.me.youtree.domain.AuthProvider;
import com.me.youtree.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByAuthProviderAndSocialId(AuthProvider authProvider, String socialId);
}
