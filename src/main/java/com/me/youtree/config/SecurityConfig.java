package com.me.youtree.config;

import com.me.youtree.exception.CustomAuthenticationEntryPoint;
import com.me.youtree.jwt.JwtAuthenticationFilter;
import com.me.youtree.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic().disable() // rest api이므로 기본설정 미사용
                .csrf().disable() // rest api이므로 csrf 보안 미사용
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations()
                        ).permitAll()
                        .requestMatchers(
                                antMatcher("/api/v1/users/**")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        return http.build();
    }
}
