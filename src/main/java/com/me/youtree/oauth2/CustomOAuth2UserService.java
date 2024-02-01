package com.me.youtree.oauth2;

import com.me.youtree.domain.AuthProvider;
import com.me.youtree.domain.User;
import com.me.youtree.oauth2.CustomOAuth2User;
import com.me.youtree.oauth2.OAuthAttributes;
import com.me.youtree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider authProvider = getAuthProvider(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes extractAttributes = OAuthAttributes.of(authProvider, userNameAttributeName, attributes);

        User createdUser = getUser(extractAttributes, authProvider); // getUser() 메소드로 User 객체 생성 후 반환

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser.getEmail(),
                createdUser.getRole()
        );
    }

    private AuthProvider getAuthProvider(String registrationId) {
        return AuthProvider.GOOGLE;
    }

    private User getUser(OAuthAttributes attributes, AuthProvider authProvider) {
        User findUser = userRepository.findByAuthProviderAndSocialId(authProvider, attributes.getOauth2UserInfo().getId())
                .orElse(null);

        if(findUser == null) {
            return saveUser(attributes, authProvider);
        }
        return findUser;
    }

    private User saveUser(OAuthAttributes attributes, AuthProvider authProvider) {
        User createdUser = attributes.toEntity(authProvider, attributes.getOauth2UserInfo());
        return userRepository.save(createdUser);
    }
}
