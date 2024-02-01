package com.me.youtree.oauth2;

import com.me.youtree.domain.User;
import com.me.youtree.exception.ErrorCode;
import com.me.youtree.exception.YouTreeApplicationException;
import com.me.youtree.jwt.JwtTokenProvider;
import com.me.youtree.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
        } catch (Exception e) {
            throw e;
        }
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtTokenProvider.createToken(oAuth2User.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Authorization-Refresh", "Bearer " + refreshToken);
        jwtTokenProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        User user = userRepository.findByEmail(oAuth2User.getEmail())
                .orElseThrow(() -> new YouTreeApplicationException(ErrorCode.USER_NOT_FOUND));
        user.updateRefreshToken(refreshToken);
        userRepository.saveAndFlush(user);
    }
}
