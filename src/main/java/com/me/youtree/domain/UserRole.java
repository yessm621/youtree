package com.me.youtree.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    // OAuth2 로그인 시 첫 로그인을 구분하기 위해 GUEST 추가
    GUEST("ROLE_GUEST"),
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String key;
}
